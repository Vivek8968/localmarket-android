package com.localmarket.app.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    val category: String?,
    @SerializedName("shop_id")
    val shopId: String,
    @SerializedName("in_stock")
    val inStock: Boolean = true,
    @SerializedName("stock_quantity")
    val stockQuantity: Int? = null,
    @SerializedName("created_at")
    val createdAt: String,
    val specifications: Map<String, String>? = null,
    val stock: Int = 0
)