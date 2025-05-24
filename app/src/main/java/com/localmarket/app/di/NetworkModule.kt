package com.localmarket.app.di

import com.localmarket.app.data.api.ApiService
import com.localmarket.app.data.api.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    // OkHttp Client
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val authInterceptor = AuthInterceptor(get())
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Using 10.0.2.2 which points to localhost from Android emulator
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // API Service
    single { get<Retrofit>().create(ApiService::class.java) }
}