package com.kirollos.boutique.ui.productList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kirollos.boutique.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _uiState = MutableStateFlow(ProductListUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _searchFlow = MutableStateFlow("")

    private val _uiEffect = Channel<ProductListUiEffect>()
    var uiEffect = _uiEffect.receiveAsFlow()

    init {
        onEvent(ProductListUiEvent.GetProducts)
        observeSearchFlow()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearchFlow() {
        _searchFlow
            .drop(1)
            .map { it.trim() }
            .debounce(400.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { text ->
                val flow = if (text.isEmpty()) productRepository.getProductsList()
                else productRepository.searchProduct(text)

                flow.onStart {
                    _uiState.update {
                        it.copy(
                            isSearchLoading = true,
                            searchError = null
                        )
                    }
                }
            }
            .onEach { result ->
                when {
                    result.isSuccess -> {
                        _uiState.update {
                            it.copy(
                                isSearchLoading = false,
                                productList = result.getOrNull(),
                                searchError = null,
                            )
                        }
                    }

                    result.isFailure -> {
                        _uiState.update {
                            it.copy(
                                isSearchLoading = false,
                                searchError = result.exceptionOrNull()?.localizedMessage
                                    ?: "UnHandled Exception"
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ProductListUiEvent) {
        when (event) {
            ProductListUiEvent.GetProducts -> getProducts()
            is ProductListUiEvent.SearchText -> searchText(event.text)
            is ProductListUiEvent.OnProductClicked -> {
                _uiEffect.trySend(ProductListUiEffect.NavigateToProductDetails(event.id))
            }
        }
    }

    private fun searchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        _searchFlow.update { text }
    }

    private fun getProducts() {
        viewModelScope.launch {
            productRepository.getProductsList()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            productsError = null,
                            isEmpty = false
                        )
                    }
                }
                .collect { result ->
                    when {
                        result.isSuccess -> {
                            _uiState.update {
                                val products = result.getOrNull()
                                it.copy(
                                    isLoading = false,
                                    productList = products,
                                    productsError = null,
                                    isEmpty = products == null || products.products.isEmpty()
                                )
                            }
                        }

                        result.isFailure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    productsError = result.exceptionOrNull()?.localizedMessage
                                        ?: "UnHandled Exception"
                                )
                            }
                        }
                    }
                }
        }
    }
}
