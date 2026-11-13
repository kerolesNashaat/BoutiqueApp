package com.kirollos.boutique.ui.productDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirollos.boutique.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val productId: Int = checkNotNull(savedStateHandle[PRODUCT_ID_ARG])

    private val _uiState = MutableStateFlow(ProductDetailsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<ProductDetailsUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private var detailsJob: Job? = null

    init {
        onEvent(ProductDetailsUiEvent.GetProductDetails)
    }

    fun onEvent(event: ProductDetailsUiEvent) {
        when (event) {
            ProductDetailsUiEvent.GetProductDetails -> getProductDetails()
            ProductDetailsUiEvent.OnBackClicked -> {
                _uiEffect.trySend(ProductDetailsUiEffect.NavigateBack)
            }
        }
    }

    private fun getProductDetails() {
        // Ignore repeated Retry taps while a request is already in flight
        if (detailsJob?.isActive == true) return

        detailsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            productRepository.getProductDetails(productId.toString())
                .onSuccess { product ->
                    _uiState.update {
                        it.copy(isLoading = false, product = product, error = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.localizedMessage ?: "UnHandled Exception"
                        )
                    }
                }
        }
    }

    companion object {
        const val PRODUCT_ID_ARG = "productId"
    }
}
