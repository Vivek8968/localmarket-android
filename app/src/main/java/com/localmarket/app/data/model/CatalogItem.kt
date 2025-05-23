package com.localmarket.app.data.model

import com.google.gson.annotations.SerializedName

data class CatalogItem(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    val category: String,
    val brand: String?,
    val specifications: Map<String, String>? = null
)