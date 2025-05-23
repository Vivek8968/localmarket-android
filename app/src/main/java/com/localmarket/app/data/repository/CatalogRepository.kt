package com.localmarket.app.data.repository

import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.model.CatalogItem
import com.localmarket.app.data.model.Product
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CatalogRepository(private val apiService: ApiService) {
    
    // Get catalog items with optional search and category filters
    suspend fun getCatalogItems(
        search: String? = null,
        category: String? = null
    ): Resource<List<CatalogItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCatalogItems(search, category)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch catalog items")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch catalog items")
            }
        }
    }
    
    // Get catalog categories
    suspend fun getCatalogCategories(): Resource<List<String>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCatalogCategories()
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch categories")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch categories")
            }
        }
    }
    
    // Add catalog item to shop inventory
    suspend fun addCatalogItemToShop(
        shopId: String,
        catalogItem: CatalogItem,
        price: Double? = null,
        stockQuantity: Int? = null
    ): Resource<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val productData = mapOf(
                    "name" to catalogItem.name,
                    "description" to (catalogItem.description ?: ""),
                    "price" to (price ?: catalogItem.price),
                    "image_url" to (catalogItem.imageUrl ?: ""),
                    "category" to (catalogItem.category),
                    "in_stock" to true,
                    "stock_quantity" to (stockQuantity ?: 10),
                    "catalog_item_id" to catalogItem.id
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
}