package com.localmarket.app.di

import android.content.Context
import android.location.LocationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.localmarket.app.utils.LocationHelper
import com.localmarket.app.utils.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Firebase Auth
    single<FirebaseAuth> { Firebase.auth }
    
    // Location Services
    single { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    single { LocationHelper(get()) }
    
    // Preferences
    single { PreferenceManager(androidContext()) }
}