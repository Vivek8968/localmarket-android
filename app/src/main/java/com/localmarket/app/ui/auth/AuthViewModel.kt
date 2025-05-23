package com.localmarket.app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.model.User
import com.localmarket.app.data.repository.AuthRepository
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val apiService: ApiService
) : ViewModel() {
    
    private val _phoneAuthState = MutableLiveData<PhoneAuthState>()
    val phoneAuthState: LiveData<PhoneAuthState> = _phoneAuthState
    
    private val _googleAuthState = MutableLiveData<Resource<FirebaseUser>>()
    val googleAuthState: LiveData<Resource<FirebaseUser>> = _googleAuthState
    
    private val _userRegistrationState = MutableLiveData<Resource<User>>()
    val userRegistrationState: LiveData<Resource<User>> = _userRegistrationState
    
    // Phone authentication callback
    val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            _phoneAuthState.value = PhoneAuthState.VerificationCompleted(credential)
        }
        
        override fun onVerificationFailed(exception: FirebaseException) {
            _phoneAuthState.value = PhoneAuthState.VerificationFailed(exception.message ?: "Verification failed")
        }
        
        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            _phoneAuthState.value = PhoneAuthState.CodeSent(verificationId, token)
        }
    }
    
    // Verify phone number with OTP
    fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        _phoneAuthState.value = PhoneAuthState.Loading
        
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneCredential(credential)
        } catch (e: Exception) {
            _phoneAuthState.value = PhoneAuthState.VerificationFailed(e.message ?: "Invalid verification code")
        }
    }
    
    // Sign in with phone credential
    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            when (val result = authRepository.signInWithPhoneCredential(credential)) {
                is Resource.Success -> {
                    _phoneAuthState.value = PhoneAuthState.SignInSuccess(result.data)
                }
                is Resource.Error -> {
                    _phoneAuthState.value = PhoneAuthState.SignInFailed(result.message)
                }
                else -> {}
            }
        }
    }
    
    // Sign in with Google
    fun signInWithGoogle(idToken: String) {
        _googleAuthState.value = Resource.Loading
        
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            _googleAuthState.value = result
        }
    }
    
    // Register user with backend
    fun registerUserWithBackend(name: String?, email: String?, phone: String?) {
        _userRegistrationState.value = Resource.Loading
        
        viewModelScope.launch {
            val result = authRepository.registerWithBackend(apiService, name, email, phone)
            _userRegistrationState.value = result
        }
    }
    
    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentFirebaseUser() != null
    }
}

// Phone authentication states
sealed class PhoneAuthState {
    object Loading : PhoneAuthState()
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : PhoneAuthState()
    data class VerificationCompleted(val credential: PhoneAuthCredential) : PhoneAuthState()
    data class VerificationFailed(val message: String) : PhoneAuthState()
    data class SignInSuccess(val user: FirebaseUser) : PhoneAuthState()
    data class SignInFailed(val message: String) : PhoneAuthState()
}