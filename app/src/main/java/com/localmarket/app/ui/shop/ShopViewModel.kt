package com.localmarket.app.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localmarket.app.data.model.Product
import com.localmarket.app.data.model.Shop
import com.localmarket.app.data.repository.ShopRepository
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.launch

class ShopViewModel(private val shopRepository: ShopRepository) : ViewModel() {
    
    private val _shop = MutableLiveData<Resource<Shop>>()
    val shop: LiveData<Resource<Shop>> = _shop
    
    private val _products = MutableLiveData<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> = _products
    
    private val _productDetails = MutableLiveData<Resource<Product>>()
    val productDetails: LiveData<Resource<Product>> = _productDetails
    
    private val _shopDetails = MutableLiveData<Resource<Shop>>()
    val shopDetails: LiveData<Resource<Shop>> = _shopDetails
    
    // Load shop details
    fun loadShopDetails(shopId: String) {
        _shop.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getShopById(shopId)
            _shop.value = result
        }
    }
    
    // Load shop products
    fun loadShopProducts(
        shopId: String,
        search: String? = null,
        category: String? = null,
        sort: String? = null
    ) {
        _products.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getShopProducts(shopId, search, category, sort)
            _products.value = result
        }
    }
    
    // Load product details
    fun loadProductDetails(productId: String, shopId: String) {
        _productDetails.value = Resource.Loading
        _shopDetails.value = Resource.Loading
        
        viewModelScope.launch {
            // Load product details
            val productResult = shopRepository.getProductById(productId)
            _productDetails.value = productResult
            
            // Load shop details
            val shopResult = shopRepository.getShopById(shopId)
            _shopDetails.value = shopResult
        }
    }
    
    // Refresh shop data
    fun refreshShopData(shopId: String) {
        loadShopDetails(shopId)
        loadShopProducts(shopId)
    }
}