package com.kirollos.boutique.ui.productDetails

sealed interface ProductDetailsUiEffect {
    data object NavigateBack : ProductDetailsUiEffect
}
