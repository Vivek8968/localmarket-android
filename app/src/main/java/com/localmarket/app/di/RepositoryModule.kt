package com.localmarket.app.di

import com.localmarket.app.data.repository.AuthRepository
import com.localmarket.app.data.repository.CatalogRepository
import com.localmarket.app.data.repository.ShopRepository
import com.localmarket.app.data.repository.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    // Repositories
    single { AuthRepository(get(), get()) }
    single { ShopRepository(get(), get()) }
    single { CatalogRepository(get()) }
    single { UserRepository(get()) }
}