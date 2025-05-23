package com.localmarket.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.localmarket.app.data.model.Shop
import com.localmarket.app.data.model.User
import com.localmarket.app.data.repository.AuthRepository
import com.localmarket.app.data.repository.ShopRepository
import com.localmarket.app.data.repository.UserRepository
import com.localmarket.app.utils.Resource
import kotlinx.coroutines.launch

class HomeViewModel(
    private val shopRepository: ShopRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _shops = MutableLiveData<Resource<List<Shop>>>()
    val shops: LiveData<Resource<List<Shop>>> = _shops
    
    private val _currentUser = MutableLiveData<Resource<User>>()
    val currentUser: LiveData<Resource<User>> = _currentUser
    
    init {
        loadShops()
        loadCurrentUser()
    }
    
    fun loadShops() {
        _shops.value = Resource.Loading
        
        viewModelScope.launch {
            val result = shopRepository.getAllShops()
            _shops.value = result
        }
    }
    
    fun loadCurrentUser() {
        if (authRepository.getCurrentFirebaseUser() != null) {
            _currentUser.value = Resource.Loading
            
            viewModelScope.launch {
                val result = userRepository.getCurrentUser()
                _currentUser.value = result
            }
        }
    }
    
    fun refreshData() {
        loadShops()
        loadCurrentUser()
    }
    
    fun signOut() {
        authRepository.signOut()
    }
    
    fun isUserLoggedIn(): Boolean {
        return authRepository.getCurrentFirebaseUser() != null
    }
}