package com.localmarket.app.data.repository

import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.model.Product
import com.localmarket.app.data.model.Shop
import com.localmarket.app.utils.LocationHelper
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopRepository(
    private val apiService: ApiService,
    private val locationHelper: LocationHelper
) {
    
    // Get all shops with distance calculation
    suspend fun getAllShops(): Resource<List<Shop>> {
        return withContext(Dispatchers.IO) {
            try {
                // Get current location
                val location = locationHelper.getLastLocation()
                
                val response = apiService.getAllShops(
                    latitude = location?.latitude,
                    longitude = location?.longitude
                )
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch shops")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch shops")
            }
        }
    }
    
    // Get shop by ID
    suspend fun getShopById(shopId: String): Resource<Shop> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getShopById(shopId)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch shop details")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch shop details")
            }
        }
    }
    
    // Get products for a shop
    suspend fun getShopProducts(
        shopId: String,
        search: String? = null,
        category: String? = null,
        sort: String? = null
    ): Resource<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getShopProducts(
                    shopId = shopId,
                    search = search,
                    category = category,
                    sort = sort
                )
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch shop products")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch shop products")
            }
        }
    }
    
    // Get product by ID
    suspend fun getProductById(productId: String): Resource<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProductById(productId)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch product details")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch product details")
            }
        }
    }
    
    // Create a new shop
    suspend fun createShop(
        name: String,
        address: String,
        phone: String?,
        whatsappNumber: String?,
        latitude: Double?,
        longitude: Double?,
        bannerImage: String?
    ): Resource<Shop> {
        return withContext(Dispatchers.IO) {
            try {
                val shopData = mapOf(
                    "name" to name,
                    "address" to address,
                    "phone" to (phone ?: ""),
                    "whatsapp_number" to (whatsappNumber ?: ""),
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "banner_image" to bannerImage
                )
                
                val response = apiService.createShop(shopData)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to create shop")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to create shop")
            }
        }
    }
    
    // Update an existing shop
    suspend fun updateShop(
        shopId: String,
        name: String? = null,
        address: String? = null,
        phone: String? = null,
        whatsappNumber: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        bannerImage: String? = null
    ): Resource<Shop> {
        return withContext(Dispatchers.IO) {
            try {
                val updateData = mutableMapOf<String, Any?>()
                
                name?.let { updateData["name"] = it }
                address?.let { updateData["address"] = it }
                phone?.let { updateData["phone"] = it }
                whatsappNumber?.let { updateData["whatsapp_number"] = it }
                latitude?.let { updateData["latitude"] = it }
                longitude?.let { updateData["longitude"] = it }
                bannerImage?.let { updateData["banner_image"] = it }
                
                val response = apiService.updateShop(shopId, updateData)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to update shop")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update shop")
            }
        }
    }
    
    // Add product to shop
    suspend fun addProductToShop(
        shopId: String,
        productId: String,
        price: Double,
        stock: Int
    ): Resource<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val productData = mapOf(
                    "product_id" to productId,
                    "price" to price,
                    "stock" to stock
                )
                
                val response = apiService.addProductToShop(shopId, productData)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to add product to shop")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to add product to shop")
            }
        }
    }
    
    // Update product in shop
    suspend fun updateShopProduct(
        shopId: String,
        productId: String,
        price: Double? = null,
        stock: Int? = null
    ): Resource<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val updateData = mutableMapOf<String, Any?>()
                
                price?.let { updateData["price"] = it }
                stock?.let { updateData["stock"] = it }
                
                val response = apiService.updateShopProduct(shopId, productId, updateData)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to update product")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update product")
            }
        }
    }
    
    // Remove product from shop
    suspend fun removeProductFromShop(shopId: String, productId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.removeProductFromShop(shopId, productId)
                
                if (response.success) {
                    Resource.Success(true)
                } else {
                    Resource.Error(response.message ?: "Failed to remove product from shop")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to remove product from shop")
            }
        }
    }
}