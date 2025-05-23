package com.localmarket.app

import android.app.Application
import com.localmarket.app.di.appModule
import com.localmarket.app.di.networkModule
import com.localmarket.app.di.repositoryModule
import com.localmarket.app.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class LocalMarketApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin for dependency injection
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@LocalMarketApp)
            modules(listOf(
                appModule,
                networkModule,
                repositoryModule,
                viewModelModule
            ))
        }
    }
}