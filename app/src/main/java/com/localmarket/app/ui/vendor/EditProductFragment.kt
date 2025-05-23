package com.localmarket.app.ui.vendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentEditProductBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.loadImage
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditProductFragment : Fragment() {
    
    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    
    private val args: EditProductFragmentArgs by navArgs()
    private val viewModel: VendorViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        
        // Load product details
        viewModel.loadProductDetails(args.productId)
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_product)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }
    
    private fun setupClickListeners() {
        // Save changes button
        binding.buttonSaveChanges.setOnClickListener {
            if (validateInputs()) {
                updateProduct()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate price
        val priceText = binding.editTextPrice.text.toString().trim()
        if (priceText.isEmpty()) {
            binding.textInputLayoutPrice.error = getString(R.string.price_required)
            isValid = false
        } else {
            try {
                val price = priceText.toDouble()
                if (price <= 0) {
                    binding.textInputLayoutPrice.error = getString(R.string.price_must_be_positive)
                    isValid = false
                } else {
                    binding.textInputLayoutPrice.error = null
                }
            } catch (e: NumberFormatException) {
                binding.textInputLayoutPrice.error = getString(R.string.invalid_price)
                isValid = false
            }
        }
        
        // Validate stock
        val stockText = binding.editTextStock.text.toString().trim()
        if (stockText.isEmpty()) {
            binding.textInputLayoutStock.error = getString(R.string.stock_required)
            isValid = false
        } else {
            try {
                val stock = stockText.toInt()
                if (stock < 0) {
                    binding.textInputLayoutStock.error = getString(R.string.stock_cannot_be_negative)
                    isValid = false
                } else {
                    binding.textInputLayoutStock.error = null
                }
            } catch (e: NumberFormatException) {
                binding.textInputLayoutStock.error = getString(R.string.invalid_stock)
                isValid = false
            }
        }
        
        return isValid
    }
    
    private fun updateProduct() {
        val price = binding.editTextPrice.text.toString().trim().toDouble()
        val stock = binding.editTextStock.text.toString().trim().toInt()
        
        viewModel.updateProduct(args.productId, price, stock)
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
                    
                    // Populate UI with product details
                    binding.apply {
                        textViewProductName.text = product.name
                        editTextPrice.setText(product.price.toString())
                        editTextStock.setText(product.stock.toString())
                        
                        // Load product image
                        imageViewProduct.loadImage(product.imageUrl)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(resource.message)
                    findNavController().navigateUp()
                }
            }
        }
        
        // Observe update product result
        viewModel.updateProductResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSaveChanges.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSaveChanges.isEnabled = true
                    
                    showToast(getString(R.string.product_updated_successfully))
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSaveChanges.isEnabled = true
                    
                    showToast(resource.message)
                }
            }
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}