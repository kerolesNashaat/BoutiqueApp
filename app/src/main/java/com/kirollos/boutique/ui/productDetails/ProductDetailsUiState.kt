package com.kirollos.boutique.ui.productDetails

import com.kirollos.boutique.domain.model.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)
