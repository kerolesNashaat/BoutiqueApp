package com.kirollos.boutique.ui.productDetails

sealed interface ProductDetailsUiEvent {
    data object GetProductDetails : ProductDetailsUiEvent
    data object OnBackClicked : ProductDetailsUiEvent
}
