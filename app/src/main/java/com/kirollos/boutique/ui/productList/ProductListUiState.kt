package com.kirollos.boutique.ui.productList

import com.kirollos.boutique.domain.model.ProductList

data class ProductListUiState(
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    val productList: ProductList? = null,
    val productsError: String? = null,
    val searchText: String? = null,
    val isSearchLoading: Boolean = false,
    val searchError: String? = null
)
