package com.localmarket.app.ui.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.localmarket.app.R
import com.localmarket.app.data.model.CatalogItem
import com.localmarket.app.databinding.FragmentCatalogBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VendorViewModel by viewModel()
    private lateinit var catalogAdapter: CatalogAdapter
    
    private var currentSearchQuery: String? = null
    private var currentCategory: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
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
        
        // Load catalog items
        viewModel.loadCatalogItems()
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Product Catalog"
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        catalogAdapter = CatalogAdapter(
            onItemClicked = { item ->
                showItemDetailsDialog(item)
            },
            onAddClicked = { item ->
                showAddProductDialog(item)
            }
        )
        
        binding.recyclerViewCatalog.adapter = catalogAdapter
    }
    
    private fun setupSearchAndFilter() {
        // Search functionality
        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.editTextSearch.text.toString().trim()
                currentSearchQuery = if (query.isEmpty()) null else query
                refreshCatalog()
                return@setOnEditorActionListener true
            }
            false
        }
        
        // Filter button
        binding.imageViewFilter.setOnClickListener {
            showFilterDialog()
        }
    }
    
    private fun showFilterDialog() {
        val categories = arrayOf("All", "Electronics", "Clothing", "Home", "Food", "Other")
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter by Category")
            .setItems(categories) { _, which ->
                currentCategory = if (which == 0) null else categories[which]
                refreshCatalog()
            }
            .show()
    }
    
    private fun showItemDetailsDialog(item: CatalogItem) {
        val message = """
            Name: ${item.name}
            Category: ${item.category}
            Description: ${item.description ?: "No description available"}
            Brand: ${item.brand ?: "N/A"}
        """.trimIndent()
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Product Details")
            .setMessage(message)
            .setPositiveButton("Add to Shop") { _, _ ->
                showAddProductDialog(item)
            }
            .setNegativeButton("Close", null)
            .show()
    }
    
    private fun showAddProductDialog(item: CatalogItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val binding = DialogAddProductBinding.bind(dialogView)
        
        binding.textViewProductName.text = item.name
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Product to Shop")
            .setView(dialogView)
            .setPositiveButton("Add", null) // Set to null initially
            .setNegativeButton("Cancel", null)
            .create()
        
        dialog.show()
        
        // Override the positive button to prevent dialog dismissal on validation failure
        dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val priceText = binding.editTextPrice.text.toString().trim()
            val stockText = binding.editTextStock.text.toString().trim()
            
            if (priceText.isEmpty()) {
                binding.textInputLayoutPrice.error = "Please enter a price"
                return@setOnClickListener
            }
            
            if (stockText.isEmpty()) {
                binding.textInputLayoutStock.error = "Please enter stock quantity"
                return@setOnClickListener
            }
            
            try {
                val price = priceText.toDouble()
                val stock = stockText.toInt()
                
                if (price <= 0) {
                    binding.textInputLayoutPrice.error = "Price must be greater than 0"
                    return@setOnClickListener
                }
                
                if (stock < 0) {
                    binding.textInputLayoutStock.error = "Stock cannot be negative"
                    return@setOnClickListener
                }
                
                // Add product to shop
                viewModel.addProductFromCatalog(item.id, price, stock)
                dialog.dismiss()
                
            } catch (e: NumberFormatException) {
                binding.textInputLayoutPrice.error = "Invalid price or stock value"
            }
        }
    }
    
    private fun refreshCatalog() {
        viewModel.loadCatalogItems(currentSearchQuery, currentCategory)
    }
    
    private fun observeViewModel() {
        // Observe catalog items
        viewModel.catalogItems.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.textViewEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    if (resource.data.isEmpty()) {
                        binding.textViewEmpty.visibility = View.VISIBLE
                        binding.recyclerViewCatalog.visibility = View.GONE
                    } else {
                        binding.textViewEmpty.visibility = View.GONE
                        binding.recyclerViewCatalog.visibility = View.VISIBLE
                        catalogAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.textViewEmpty.visibility = View.VISIBLE
                    binding.textViewEmpty.text = resource.message
                    binding.recyclerViewCatalog.visibility = View.GONE
                }
            }
        }
        
        // Observe add product result
        viewModel.addProductResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Product added successfully")
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast("Failed to add product: ${resource.message}")
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
        menuInflater.inflate(R.menu.menu_catalog, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_refresh -> {
                refreshCatalog()
                true
            }
            else -> false
        }
    }
}