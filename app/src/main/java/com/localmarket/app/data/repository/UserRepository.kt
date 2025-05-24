package com.localmarket.app.data.repository

import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.model.User
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: ApiService) {
    
    // Get current user profile
    suspend fun getCurrentUser(): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUser()
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to fetch user profile")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to fetch user profile")
            }
        }
    }
    
    // Update user profile
    suspend fun updateUserProfile(
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        isVendor: Boolean? = null
    ): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val updateMap = mutableMapOf<String, Any>()
                
                name?.let { updateMap["name"] = it }
                email?.let { updateMap["email"] = it }
                phone?.let { updateMap["phone"] = it }
                isVendor?.let { updateMap["is_vendor"] = it }
                
                val response = apiService.updateUserProfile(updateMap)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update profile")
            }
        }
    }
}