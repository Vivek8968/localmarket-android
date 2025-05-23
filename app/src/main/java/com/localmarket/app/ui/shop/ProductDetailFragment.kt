package com.localmarket.app.ui.shop

import android.content.Intent
import android.net.Uri
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
import androidx.navigation.fragment.navArgs
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentProductDetailBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.formatPrice
import com.localmarket.app.utils.loadImage
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductDetailFragment : Fragment(), MenuProvider {
    
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: ProductDetailFragmentArgs by navArgs()
    private val viewModel: ShopViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add menu provider
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        
        // Load product details
        viewModel.loadProductDetails(args.productId, args.shopId)
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""
    }
    
    private fun setupClickListeners() {
        // Contact shop on WhatsApp
        binding.buttonContactShop.setOnClickListener {
            viewModel.shopDetails.value?.let { resource ->
                if (resource is Resource.Success) {
                    val shop = resource.data
                    shop.whatsappNumber?.let { number ->
                        openWhatsApp(number)
                    } ?: run {
                        showToast(getString(R.string.shop_has_no_whatsapp))
                    }
                }
            }
        }
        
        // Add to cart
        binding.fabAddToCart.setOnClickListener {
            // TODO: Implement add to cart functionality
            showToast(getString(R.string.added_to_cart))
        }
    }
    
    private fun openWhatsApp(number: String) {
        try {
            val url = "https://api.whatsapp.com/send?phone=$number"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            showToast(getString(R.string.whatsapp_not_installed))
        }
    }
    
    private fun observeViewModel() {
        // Observe product details
        viewModel.productDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    val product = resource.data
                    
                    // Update UI with product details
                    binding.apply {
                        textViewProductName.text = product.name
                        textViewProductPrice.text = formatPrice(product.price)
                        textViewProductStock.text = getString(R.string.in_stock_count, product.stock)
                        
                        // Description
                        if (product.description.isNullOrEmpty()) {
                            textViewDescriptionTitle.visibility = View.GONE
                            textViewProductDescription.visibility = View.GONE
                            divider2.visibility = View.GONE
                        } else {
                            textViewDescriptionTitle.visibility = View.VISIBLE
                            textViewProductDescription.visibility = View.VISIBLE
                            divider2.visibility = View.VISIBLE
                            textViewProductDescription.text = product.description
                        }
                        
                        // Specifications
                        if (product.specifications.isNullOrEmpty()) {
                            textViewSpecificationsTitle.visibility = View.GONE
                            textViewProductSpecifications.visibility = View.GONE
                            divider3.visibility = View.GONE
                        } else {
                            textViewSpecificationsTitle.visibility = View.VISIBLE
                            textViewProductSpecifications.visibility = View.VISIBLE
                            divider3.visibility = View.VISIBLE
                            textViewProductSpecifications.text = product.specifications
                        }
                        
                        // Load product image
                        imageViewProduct.loadImage(product.imageUrl)
                        
                        // Update collapsing toolbar title
                        collapsingToolbar.title = product.name
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(resource.message)
                }
            }
        }
        
        // Observe shop details
        viewModel.shopDetails.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Already showing progress for product details
                }
                is Resource.Success -> {
                    val shop = resource.data
                    
                    // Update UI with shop details
                    binding.apply {
                        textViewShopName.text = shop.name
                        textViewShopAddress.text = shop.address
                        
                        // Show/hide WhatsApp button based on availability
                        buttonContactShop.visibility = if (shop.whatsappNumber != null) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
                is Resource.Error -> {
                    showToast(resource.message)
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
        menuInflater.inflate(R.menu.menu_product_detail, menu)
    }
    
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_share -> {
                shareProduct()
                true
            }
            else -> false
        }
    }
    
    private fun shareProduct() {
        viewModel.productDetails.value?.let { resource ->
            if (resource is Resource.Success) {
                val product = resource.data
                val shareText = """
                    Check out ${product.name} at ${formatPrice(product.price)}
                    
                    ${product.description ?: ""}
                    
                    Available at Local Market App
                """.trimIndent()
                
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this product")
                intent.putExtra(Intent.EXTRA_TEXT, shareText)
                startActivity(Intent.createChooser(intent, "Share via"))
            }
        }
    }
}