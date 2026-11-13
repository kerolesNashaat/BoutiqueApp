package com.kirollos.boutique.data.source

import com.kirollos.boutique.data.dto.ProductDto
import com.kirollos.boutique.data.dto.ProductListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    suspend fun getProductsList(
        @Query("limit") limit: Int = 20,
    ): ProductListDto

    @GET("products/search")
    suspend fun searchProduct(
        @Query("q") text: String,
    ): ProductListDto

    @GET("products/{id}")
    suspend fun getProductDetails(
        @Path("id") id: String,
    ): ProductDto
}
