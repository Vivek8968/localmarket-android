package com.localmarket.app.ui.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentVendorDashboardBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.loadImage
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class VendorDashboardFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentVendorDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VendorViewModel by viewModel()
    private lateinit var productAdapter: VendorProductAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVendorDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        // Load vendor data
        viewModel.loadVendorShop()
        viewModel.loadVendorProducts()
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Vendor Dashboard"
    }
    
    private fun setupRecyclerView() {
        productAdapter = VendorProductAdapter(
            onEditClicked = { product ->
                val action = VendorDashboardFragmentDirections.actionVendorDashboardFragmentToEditProductFragment(
                    productId = product.id
                )
                findNavController().navigate(action)
            },
            onDeleteClicked = { product ->
                showDeleteProductDialog(product.id, product.name)
            }
        )
        
        binding.recyclerViewProducts.adapter = productAdapter
    }
    
    private fun setupClickListeners() {
        // Edit shop button
        binding.buttonEditShop.setOnClickListener {
            val shopId = (viewModel.vendorShop.value as? Resource.Success)?.data?.id ?: ""
            val action = VendorDashboardFragmentDirections.actionVendorDashboardFragmentToEditShopFragment(shopId)
            findNavController().navigate(action)
        }
        
        // Add product button
        binding.buttonAddProduct.setOnClickListener {
            val action = VendorDashboardFragmentDirections.actionVendorDashboardFragmentToCatalogFragment()
            findNavController().navigate(action)
        }
        
        // Create shop FAB
        binding.fabCreateShop.setOnClickListener {
            val action = VendorDashboardFragmentDirections.actionVendorDashboardFragmentToCreateShopFragment()
            findNavController().navigate(action)
        }
    }
    
    private fun showDeleteProductDialog(productId: String, productName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete $productName?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.removeProduct(productId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun observeViewModel() {
        // Observe vendor shop
        viewModel.vendorShop.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.fabCreateShop.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.fabCreateShop.visibility = View.GONE
                    
                    val shop = resource.data
                    
                    // Update UI with shop details
                    binding.apply {
                        textViewShopName.text = shop.name
                        textViewShopAddress.text = shop.address
                        
                        // Load shop banner image
                        imageViewShopBanner.loadImage(shop.bannerImage)
                        
                        // Update toolbar title
                        collapsingToolbar.title = shop.name
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    
                    // Show create shop button if no shop exists
                    if (resource.message.contains("not found") || resource.message.contains("No shop")) {
                        binding.fabCreateShop.visibility = View.VISIBLE
                    } else {
                        showToast(resource.message)
                    }
                }
            }
        }
        
        // Observe vendor products
        viewModel.vendorProducts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarProducts.visibility = View.VISIBLE
                    binding.textViewEmptyProducts.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBarProducts.visibility = View.GONE
                    
                    if (resource.data.isEmpty()) {
                        binding.textViewEmptyProducts.visibility = View.VISIBLE
                        binding.recyclerViewProducts.visibility = View.GONE
                    } else {
                        binding.textViewEmptyProducts.visibility = View.GONE
                        binding.recyclerViewProducts.visibility = View.VISIBLE
                        productAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBarProducts.visibility = View.GONE
                    binding.textViewEmptyProducts.visibility = View.VISIBLE
                    binding.textViewEmptyProducts.text = resource.message
                    binding.recyclerViewProducts.visibility = View.GONE
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    // Menu methods
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_vendor_dashboard, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_refresh -> {
                viewModel.loadVendorShop()
                viewModel.loadVendorProducts()
                true
            }
            else -> false
        }
    }
}