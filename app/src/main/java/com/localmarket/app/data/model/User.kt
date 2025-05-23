package com.localmarket.app.data.model

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val phone: String?,
    val isVendor: Boolean = false,
    val shopId: String? = null
)