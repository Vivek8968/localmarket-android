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
import com.localmarket.app.databinding.FragmentCreateShopBinding
import com.localmarket.app.utils.Resource
import com.localmarket.app.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateShopFragment : Fragment() {
    
    private var _binding: FragmentCreateShopBinding? = null
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
        _binding = FragmentCreateShopBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.create_shop)
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
        
        // Create shop button
        binding.buttonCreateShop.setOnClickListener {
            if (validateInputs()) {
                createShop()
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
    
    private fun createShop() {
        val shopName = binding.editTextShopName.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val whatsappNumber = binding.editTextWhatsapp.text.toString().trim().let {
            if (it.isEmpty()) null else it
        }
        
        // TODO: Upload image to server and get URL
        val bannerImageUrl = selectedImageUri?.toString()
        
        viewModel.createVendorShop(shopName, address, whatsappNumber, bannerImageUrl)
    }
    
    private fun observeViewModel() {
        viewModel.updateShopResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonCreateShop.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonCreateShop.isEnabled = true
                    
                    showToast(getString(R.string.shop_created_successfully))
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.buttonCreateShop.isEnabled = true
                    
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