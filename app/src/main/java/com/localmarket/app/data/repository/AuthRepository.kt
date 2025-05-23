package com.localmarket.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.model.User
import com.localmarket.app.utils.PreferenceManager
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val preferenceManager: PreferenceManager
) {
    
    // Get current Firebase user
    fun getCurrentFirebaseUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    
    // Sign in with phone credential
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Resource<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithCredential(credential).await()
                val user = result.user
                
                if (user != null) {
                    // Get ID token and save it
                    val token = user.getIdToken(false).await().token
                    if (token != null) {
                        preferenceManager.saveAuthToken(token)
                    }
                    Resource.Success(user)
                } else {
                    Resource.Error("Authentication failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Authentication failed")
            }
        }
    }
    
    // Sign in with Google
    suspend fun signInWithGoogle(idToken: String): Resource<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                val user = result.user
                
                if (user != null) {
                    // Get ID token and save it
                    val token = user.getIdToken(false).await().token
                    if (token != null) {
                        preferenceManager.saveAuthToken(token)
                    }
                    Resource.Success(user)
                } else {
                    Resource.Error("Google authentication failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Google authentication failed")
            }
        }
    }
    
    // Sign out
    fun signOut() {
        firebaseAuth.signOut()
        preferenceManager.clearAuthToken()
    }
    
    // Register user with backend
    suspend fun registerWithBackend(apiService: ApiService, name: String?, email: String?, phone: String?): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val request = mapOf(
                    "name" to (name ?: ""),
                    "email" to (email ?: ""),
                    "phone" to (phone ?: "")
                )
                
                val response = apiService.registerUser(request)
                
                if (response.success && response.data != null) {
                    Resource.Success(response.data)
                } else {
                    Resource.Error(response.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Registration failed")
            }
        }
    }
}