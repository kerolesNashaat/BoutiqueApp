package com.kirollos.boutique.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("availabilityStatus")
    val availabilityStatus: String? = null,
    @SerialName("brand")
    val brand: String? = null,
    @SerialName("category")
    val category: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("dimensions")
    val dimensionsDto: DimensionsDto? = null,
    @SerialName("discountPercentage")
    val discountPercentage: Double? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("images")
    val images: List<String?>? = null,
    @SerialName("meta")
    val metaDto: MetaDto? = null,
    @SerialName("minimumOrderQuantity")
    val minimumOrderQuantity: Int? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("rating")
    val rating: Double? = null,
    @SerialName("returnPolicy")
    val returnPolicy: String? = null,
    @SerialName("reviews")
    val reviewDtos: List<ReviewDto?>? = null,
    @SerialName("shippingInformation")
    val shippingInformation: String? = null,
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("stock")
    val stock: Int? = null,
    @SerialName("tags")
    val tags: List<String?>? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("warrantyInformation")
    val warrantyInformation: String? = null,
    @SerialName("weight")
    val weight: Int? = null
)