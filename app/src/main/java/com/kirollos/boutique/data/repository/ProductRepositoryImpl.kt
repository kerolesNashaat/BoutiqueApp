package com.kirollos.boutique.data.repository

import com.kirollos.boutique.data.mapper.toDomain
import com.kirollos.boutique.data.source.ApiService
import com.kirollos.boutique.di.IoDispatcher
import com.kirollos.boutique.domain.model.Product
import com.kirollos.boutique.domain.model.ProductList
import com.kirollos.boutique.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val apiService: ApiService
) : ProductRepository {
    override fun getProductsList(): Flow<Result<ProductList>> =
        flow {
            val result = try {
                Result.success(apiService.getProductsList().toDomain())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
            emit(result)

        }.flowOn(ioDispatcher)

    override fun searchProduct(text: String): Flow<Result<ProductList>> =
        flow {
            val result = try {
                Result.success(apiService.searchProduct(text).toDomain())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
            emit(result)

        }.flowOn(ioDispatcher)

    override suspend fun getProductDetails(id: String): Result<Product> {
        return try {
            Result.success(apiService.getProductDetails(id).toDomain())
        } catch (ex: Exception) {
            Result.failure(ex)
        }

    }
}
