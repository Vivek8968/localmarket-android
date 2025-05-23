package com.localmarket.app.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentShopDetailBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.loadImage
import com.localmarket.app.utils.openWhatsApp
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShopDetailFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentShopDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: ShopDetailFragmentArgs by navArgs()
    private val viewModel: ShopViewModel by viewModel()
    private lateinit var productAdapter: ProductAdapter
    
    private var currentSearchQuery: String? = null
    private var currentCategory: String? = null
    private var currentSortOption: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchAndFilter()
        observeViewModel()
        
        // Load shop data
        viewModel.loadShopDetails(args.shopId)
        viewModel.loadShopProducts(args.shopId)
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClicked = { product ->
                val action = ShopDetailFragmentDirections.actionShopDetailFragmentToProductDetailFragment(
                    productId = product.id,
                    shopId = args.shopId
                )
                findNavController().navigate(action)
            },
            onAddToCartClicked = { product ->
                // Future implementation for cart functionality
                showToast("Added ${product.name} to cart")
            }
        )
        
        binding.recyclerViewProducts.adapter = productAdapter
    }
    
    private fun setupSearchAndFilter() {
        // Search functionality
        binding.editTextSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                currentSearchQuery = query
                refreshProducts()
            }
            true
        }
        
        // Filter button
        binding.imageViewFilter.setOnClickListener {
            showFilterDialog()
        }
    }
    
    private fun showFilterDialog() {
        val categories = arrayOf("All", "Electronics", "Clothing", "Home", "Food", "Other")
        val sortOptions = arrayOf("Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A")
        
        val dialogView = layoutInflater.inflate(R.layout.dialog_filter, null)
        
        // Show category selection dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter Products")
            .setItems(categories) { _, which ->
                currentCategory = if (which == 0) null else categories[which]
                
                // Show sort options dialog
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sort By")
                    .setItems(sortOptions) { _, sortWhich ->
                        currentSortOption = when (sortWhich) {
                            0 -> "price_asc"
                            1 -> "price_desc"
                            2 -> "name_asc"
                            3 -> "name_desc"
                            else -> null
                        }
                        
                        refreshProducts()
                    }
                    .show()
            }
            .show()
    }
    
    private fun refreshProducts() {
        viewModel.loadShopProducts(
            shopId = args.shopId,
            search = currentSearchQuery,
            category = currentCategory,
            sort = currentSortOption
        )
    }
    
    private fun observeViewModel() {
        // Observe shop details
        viewModel.shop.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    val shop = resource.data
                    
                    // Update UI with shop details
                    binding.apply {
                        textViewShopName.text = shop.name
                        textViewShopAddress.text = shop.address
                        
                        // Set distance if available
                        if (shop.distance != null) {
                            textViewDistance.text = shop.distanceFormatted
                            textViewDistance.visibility = View.VISIBLE
                        } else {
                            textViewDistance.visibility = View.GONE
                        }
                        
                        // Load shop banner image
                        imageViewShopBanner.loadImage(shop.bannerImage)
                        
                        // Set up WhatsApp button
                        buttonWhatsApp.isEnabled = !shop.whatsappNumber.isNullOrEmpty()
                        buttonWhatsApp.setOnClickListener {
                            shop.whatsappNumber?.let { number ->
                                requireContext().openWhatsApp(number)
                            }
                        }
                        
                        // Update toolbar title
                        collapsingToolbar.title = shop.name
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(resource.message)
                }
            }
        }
        
        // Observe shop products
        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBarProducts.visibility = View.VISIBLE
                    binding.textViewEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBarProducts.visibility = View.GONE
                    
                    if (resource.data.isEmpty()) {
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.recyclerViewProducts.visibility = View.GONE
                    } else {
                        binding.textViewEmpty.visibility = View.GONE
                        binding.recyclerViewProducts.visibility = View.VISIBLE
                        productAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBarProducts.visibility = View.GONE
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.textViewEmpty.text = resource.message
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
        menuInflater.inflate(R.menu.menu_shop_detail, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_refresh -> {
                viewModel.refreshShopData(args.shopId)
                true
            }
            else -> false
        }
    }
}