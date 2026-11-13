package com.kirollos.boutique.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DimensionsDto(
    @SerialName("depth")
    val depth: Double? = null,
    @SerialName("height")
    val height: Double? = null,
    @SerialName("width")
    val width: Double? = null
)