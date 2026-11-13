package com.kirollos.boutique.ui.productList

import app.cash.turbine.test
import com.kirollos.boutique.domain.model.Dimensions
import com.kirollos.boutique.domain.model.Meta
import com.kirollos.boutique.domain.model.Product
import com.kirollos.boutique.domain.model.ProductList
import com.kirollos.boutique.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProductListViewModelTest {
    private var repo: ProductRepository?= null

        @Before
    fun setUp() {
        repo = Mockito.mock()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        repo = null
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `viewmodel loads and then success`() = runTest {
        val testCoroutineDispatcher = StandardTestDispatcher(scheduler = testScheduler)
        Dispatchers.setMain(testCoroutineDispatcher)

        val productsList = listOf(
            Product(
                availabilityStatus = "",
                brand = "",
                category = "",
                description = "",
                dimensions = Dimensions(2.0, 1.0, 5.0),
                discountPercentage = 10.0,
                id = 1,
                images = listOf(),
                meta = Meta(barcode = "", createdAt = "", qrCode = "", updatedAt = ""),
                minimumOrderQuantity = 1,
                price = 9.99,
                rating = 4.5,
                returnPolicy = "",
                reviews = listOf(),
                shippingInformation = "",
                sku = "",
                stock = 10,
                tags = listOf(),
                thumbnail = "",
                title = "",
                warrantyInformation = "",
                weight = 1
            )
        )
        val products = ProductList(
            limit = productsList.size,
            products = productsList,
            skip = 0,
            total = productsList.size
        )
        val result = flowOf(Result.success(products))
        whenever(repo?.getProductsList()).thenReturn(result)

        val viewModel = ProductListViewModel(repo!!)
        viewModel.uiState.test {
            assertEquals(ProductListUiState(isLoading = true), awaitItem())
            advanceUntilIdle()
            assertEquals(ProductListUiState(isLoading = false, productList = products), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(repo)?.getProductsList()
    }
}