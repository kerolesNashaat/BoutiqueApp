package com.kirollos.boutique.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MetaDto(
    @SerialName("barcode")
    val barcode: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("qrCode")
    val qrCode: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null
)