package com.localmarket.app.data.api

import com.localmarket.app.utils.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val preferenceManager: PreferenceManager) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip token for auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/login") ||
            originalRequest.url.encodedPath.contains("/auth/register")) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from preferences
        val token = preferenceManager.getAuthToken()
        
        // Add token to request if available
        return if (token.isNotEmpty()) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}