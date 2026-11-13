package com.kirollos.boutique.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductListDto(
    @SerialName("limit")
    val limit: Int? = null,
    @SerialName("products")
    val productsDto: List<ProductDto?>? = null,
    @SerialName("skip")
    val skip: Int? = null,
    @SerialName("total")
    val total: Int? = null
)