package com.kirollos.boutique.ui.productList

sealed interface ProductListUiEvent {
    data object GetProducts : ProductListUiEvent
    data class SearchText(val text: String) : ProductListUiEvent
    data class OnProductClicked(val id: Int) : ProductListUiEvent
}