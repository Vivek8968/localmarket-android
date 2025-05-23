package com.localmarket.app.ui.vendor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.localmarket.app.R
import com.localmarket.app.databinding.FragmentEditShopBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.loadImage
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditShopFragment : Fragment() {
    
    private var _binding: FragmentEditShopBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VendorViewModel by viewModel()
    private var selectedImageUri: Uri? = null
    
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imageViewBanner.setImageURI(uri)
                binding.textViewUploadBanner.visibility = View.GONE
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditShopBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
        
        // Load shop details
        viewModel.loadVendorShop()
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_shop)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }
    
    private fun setupClickListeners() {
        // Upload banner image
        binding.textViewUploadBanner.setOnClickListener {
            openImagePicker()
        }
        
        binding.imageViewBanner.setOnClickListener {
            openImagePicker()
        }
        
        // Save changes button
        binding.buttonSaveChanges.setOnClickListener {
            if (validateInputs()) {
                updateShop()
            }
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Validate shop name
        val shopName = binding.editTextShopName.text.toString().trim()
        if (shopName.isEmpty()) {
            binding.textInputLayoutShopName.error = getString(R.string.shop_name_required)
            isValid = false
        } else {
            binding.textInputLayoutShopName.error = null
        }
        
        // Validate address
        val address = binding.editTextAddress.text.toString().trim()
        if (address.isEmpty()) {
            binding.textInputLayoutAddress.error = getString(R.string.address_required)
            isValid = false
        } else {
            binding.textInputLayoutAddress.error = null
        }
        
        return isValid
    }
    
    private fun updateShop() {
        val shopName = binding.editTextShopName.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val whatsappNumber = binding.editTextWhatsapp.text.toString().trim().let {
            if (it.isEmpty()) null else it
        }
        
        // TODO: Upload image to server and get URL
        val bannerImageUrl = selectedImageUri?.toString()
        
        viewModel.updateShopDetails(shopName, address, whatsappNumber, bannerImageUrl)
    }
    
    private fun observeViewModel() {
        // Observe vendor shop
        viewModel.vendorShop.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    
                    val shop = resource.data
                    
                    // Populate UI with shop details
                    binding.apply {
                        editTextShopName.setText(shop.name)
                        editTextAddress.setText(shop.address)
                        editTextWhatsapp.setText(shop.whatsappNumber ?: "")
                        
                        // Load shop banner image
                        imageViewBanner.loadImage(shop.bannerImage)
                        
                        if (shop.bannerImage != null) {
                            textViewUploadBanner.visibility = View.GONE
                        } else {
                            textViewUploadBanner.visibility = View.VISIBLE
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(resource.message)
                    findNavController().navigateUp()
                }
            }
        }
        
        // Observe update shop result
        viewModel.updateShopResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSaveChanges.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSaveChanges.isEnabled = true
                    
                    showToast(getString(R.string.shop_updated_successfully))
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