package com.localmarket.app.data.model

import com.google.gson.annotations.SerializedName

data class Shop(
    val id: String,
    val name: String,
    val address: String,
    val phone: String?,
    @SerializedName("whatsapp_number")
    val whatsappNumber: String?,
    @SerializedName("banner_image")
    val bannerImage: String?,
    val latitude: Double?,
    val longitude: Double?,
    @SerializedName("owner_id")
    val ownerId: String,
    val distance: Double? = null
)