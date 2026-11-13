package com.kirollos.boutique.di

import com.kirollos.boutique.data.repository.ProductRepositoryImpl
import com.kirollos.boutique.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(repositoryImpl: ProductRepositoryImpl): ProductRepository
}
