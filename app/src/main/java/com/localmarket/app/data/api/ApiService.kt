package com.localmarket.app.data.api

import com.localmarket.app.data.model.*
import retrofit2.http.*

interface ApiService {
    
    // Auth Endpoints
    @POST("auth/register")
    suspend fun registerUser(@Body request: Map<String, Any>): ApiResponse<User>
    
    @POST("auth/login")
    suspend fun loginUser(@Body request: Map<String, Any>): ApiResponse<Map<String, String>>
    
    @POST("auth/verify-token")
    suspend fun verifyToken(): ApiResponse<User>
    
    // User Endpoints
    @GET("users/me")
    suspend fun getCurrentUser(): ApiResponse<User>
    
    @PUT("users/me")
    suspend fun updateUserProfile(@Body request: Map<String, Any>): ApiResponse<User>
    
    // Shop Endpoints
    @GET("shops")
    suspend fun getAllShops(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?
    ): ApiResponse<List<Shop>>
    
    @GET("shops/{shopId}")
    suspend fun getShopById(@Path("shopId") shopId: String): ApiResponse<Shop>
    
    @POST("shops")
    suspend fun createShop(@Body shop: Map<String, Any>): ApiResponse<Shop>
    
    @PUT("shops/{shopId}")
    suspend fun updateShop(
        @Path("shopId") shopId: String,
        @Body shop: Map<String, Any>
    ): ApiResponse<Shop>
    
    // Product Endpoints
    @GET("shops/{shopId}/products")
    suspend fun getShopProducts(
        @Path("shopId") shopId: String,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("sort") sort: String? = null
    ): ApiResponse<List<Product>>
    
    @GET("products/{productId}")
    suspend fun getProductById(@Path("productId") productId: String): ApiResponse<Product>
    
    @POST("shops/{shopId}/products")
    suspend fun addProductToShop(
        @Path("shopId") shopId: String,
        @Body product: Map<String, Any>
    ): ApiResponse<Product>
    
    @PUT("shops/{shopId}/products/{productId}")
    suspend fun updateShopProduct(
        @Path("shopId") shopId: String,
        @Path("productId") productId: String,
        @Body product: Map<String, Any>
    ): ApiResponse<Product>
    
    @DELETE("shops/{shopId}/products/{productId}")
    suspend fun removeProductFromShop(
        @Path("shopId") shopId: String,
        @Path("productId") productId: String
    ): ApiResponse<Any>
    
    @PUT("products/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: String,
        @Body product: Map<String, Any>
    ): ApiResponse<Product>
    
    @DELETE("products/{productId}")
    suspend fun deleteProduct(@Path("productId") productId: String): ApiResponse<Any>
    
    // Catalog Endpoints
    @GET("catalog")
    suspend fun getCatalogItems(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null
    ): ApiResponse<List<CatalogItem>>
    
    @GET("catalog/categories")
    suspend fun getCatalogCategories(): ApiResponse<List<String>>
}