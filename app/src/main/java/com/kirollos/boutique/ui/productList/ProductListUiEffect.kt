package com.kirollos.boutique.ui.productList

sealed interface ProductListUiEffect {
    data class NavigateToProductDetails(val productId: Int) : ProductListUiEffect
}