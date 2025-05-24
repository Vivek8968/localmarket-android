package com.localmarket.app.di

import com.localmarket.app.ui.auth.AuthViewModel
import com.localmarket.app.ui.home.HomeViewModel
import com.localmarket.app.ui.vendor.VendorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // ViewModels
    viewModel { AuthViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { VendorViewModel(get(), get()) }
}