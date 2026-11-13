package com.kirollos.boutique.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    @SerialName("comment")
    val comment: String? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("rating")
    val rating: Int? = null,
    @SerialName("reviewerEmail")
    val reviewerEmail: String? = null,
    @SerialName("reviewerName")
    val reviewerName: String? = null
)