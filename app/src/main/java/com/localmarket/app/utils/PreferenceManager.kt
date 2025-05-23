package com.localmarket.app.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    
    companion object {
        private const val PREF_NAME = "local_market_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_VENDOR = "is_vendor"
        private const val KEY_SHOP_ID = "shop_id"
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    // Auth Token
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, "") ?: ""
    }
    
    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }
    
    // User ID
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }
    
    // Vendor Status
    fun saveVendorStatus(isVendor: Boolean) {
        prefs.edit().putBoolean(KEY_IS_VENDOR, isVendor).apply()
    }
    
    fun isVendor(): Boolean {
        return prefs.getBoolean(KEY_IS_VENDOR, false)
    }
    
    // Shop ID
    fun saveShopId(shopId: String?) {
        if (shopId != null) {
            prefs.edit().putString(KEY_SHOP_ID, shopId).apply()
        } else {
            prefs.edit().remove(KEY_SHOP_ID).apply()
        }
    }
    
    fun getShopId(): String? {
        val shopId = prefs.getString(KEY_SHOP_ID, null)
        return if (shopId.isNullOrEmpty()) null else shopId
    }
    
    // Clear all preferences
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}