package com.localmarket.app.ui.vendor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localmarket.app.data.model.CatalogItem
import com.localmarket.app.data.model.Product
import com.localmarket.app.data.model.Shop
import com.localmarket.app.data.repository.CatalogRepository
import com.localmarket.app.data.repository.ShopRepository
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.launch

class VendorViewModel(
    private val shopRepository: ShopRepository,
    private val catalogRepository: CatalogRepository
) : ViewModel() {
    
    private val _vendorShop = MutableLiveData<Resource<Shop>>()
    val vendorShop: LiveData<Resource<Shop>> = _vendorShop
    
    private val _vendorProducts = MutableLiveData<Resource<List<Product>>>()
    val vendorProducts: LiveData<Resource<List<Product>>> = _vendorProducts
    
    private val _catalogItems = MutableLiveData<Resource<List<CatalogItem>>>()
    val catalogItems: LiveData<Resource<List<CatalogItem>>> = _catalogItems
    
    private val _addProductResult = MutableLiveData<Resource<Product>>()
    val addProductResult: LiveData<Resource<Product>> = _addProductResult
    
    private val _updateShopResult = MutableLiveData<Resource<Shop>>()
    val updateShopResult: LiveData<Resource<Shop>> = _updateShopResult
    
    private val _productDetails = MutableLiveData<Resource<Product>>()
    val productDetails: LiveData<Resource<Product>> = _productDetails
    
    private val _updateProductResult = MutableLiveData<Resource<Product>>()
    val updateProductResult: LiveData<Resource<Product>> = _updateProductResult
    
    // Load vendor shop
    fun loadVendorShop() {
        _vendorShop.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getVendorShop()
            _vendorShop.value = result
        }
    }
    
    // Load vendor products
    fun loadVendorProducts() {
        _vendorProducts.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getVendorProducts()
            _vendorProducts.value = result
        }
    }
    
    // Load catalog items
    fun loadCatalogItems(search: String? = null, category: String? = null) {
        _catalogItems.value = Resource.Loading
        
        viewModelScope.launch {
            val result = catalogRepository.getCatalogItems(search, category)
            _catalogItems.value = result
        }
    }
    
    // Add product from catalog
    fun addProductFromCatalog(catalogItemId: String, price: Double, stock: Int) {
        _addProductResult.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.addProductFromCatalog(catalogItemId, price, stock)
            _addProductResult.value = result
            
            // Refresh vendor products if successful
            if (result is Resource.Success) {
                loadVendorProducts()
            }
        }
    }
    
    // Update shop details
    fun updateShopDetails(
        name: String,
        address: String,
        whatsappNumber: String?,
        bannerImage: String?
    ) {
        _updateShopResult.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.updateShopDetails(name, address, whatsappNumber, bannerImage)
            _updateShopResult.value = result
        }
    }
    
    // Create vendor shop
    fun createVendorShop(
        name: String,
        address: String,
        whatsappNumber: String?,
        bannerImage: String?
    ) {
        _updateShopResult.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.createVendorShop(name, address, whatsappNumber, bannerImage)
            _updateShopResult.value = result
        }
    }
    
    // Load product details
    fun loadProductDetails(productId: String) {
        _productDetails.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getProductById(productId)
            _productDetails.value = result
        }
    }
    
    // Update product
    fun updateProduct(productId: String, price: Double, stock: Int) {
        _updateProductResult.value = Resource.Loading
        
        viewModelScope.launch {
            val shopId = (_vendorShop.value as? Resource.Success)?.data?.id
                ?: return@launch
            
            val result = shopRepository.updateShopProduct(shopId, productId, price, stock)
            _updateProductResult.value = result
            
            // Refresh vendor products if successful
            if (result is Resource.Success) {
                loadVendorProducts()
            }
        }
    }
    
    // Remove product
    fun removeProduct(productId: String) {
        viewModelScope.launch {
            val shopId = (_vendorShop.value as? Resource.Success)?.data?.id
                ?: return@launch
            
            val result = shopRepository.removeProductFromShop(shopId, productId)
            
            if (result is Resource.Success) {
                loadVendorProducts()
            }
        }
    }
}