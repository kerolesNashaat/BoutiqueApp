package com.kirollos.boutique.domain.repository

import com.kirollos.boutique.domain.model.Product
import com.kirollos.boutique.domain.model.ProductList
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsList(): Flow<Result<ProductList>>
    fun searchProduct(text: String): Flow<Result<ProductList>>
    suspend fun getProductDetails(id: String): Result<Product>
}