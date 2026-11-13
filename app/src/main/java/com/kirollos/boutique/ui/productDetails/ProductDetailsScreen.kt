package com.kirollos.boutique.ui.productDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kirollos.boutique.R
import com.kirollos.boutique.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductDetailsRoute(
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ProductDetailsUiEffect.NavigateBack -> navigateBack()
            }
        }
    }
    ProductDetailsScreen(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsScreen(
    state: ProductDetailsUiState,
    onEvent: (ProductDetailsUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.product?.title.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ProductDetailsUiEvent.OnBackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> DetailsLoadingContent()
                state.error != null -> DetailsErrorContent(
                    message = state.error,
                    onRetry = { onEvent(ProductDetailsUiEvent.GetProductDetails) }
                )

                state.product != null -> ProductDetailsContent(product = state.product)
            }
        }
    }
}

@Composable
private fun ProductDetailsContent(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // images can be empty in the API data, so fall back to the thumbnail
        val imageUrl = product.images.firstOrNull() ?: product.thumbnail
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = product.title,
            placeholder = painterResource(R.drawable.ic_placeholder),
            error = painterResource(R.drawable.ic_error),
            contentScale = ContentScale.Fit,
        )
        // TODO: show all product.images in a HorizontalPager

        Text(
            text = product.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (product.brand.isNotBlank()) {
            Text(
                text = product.brand,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        PriceContent(
            price = product.price,
            discountPercentage = product.discountPercentage
        )
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PriceContent(price: Double, discountPercentage: Double) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val hasDiscount = discountPercentage > 0.0 && discountPercentage < 100.0

    if (hasDiscount) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = currencyFormat.format(discountedPrice(price, discountPercentage)),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currencyFormat.format(price),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
            )
            Text(
                text = "-${"%.0f".format(Locale.US, discountPercentage)}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        Text(
            text = currencyFormat.format(price),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// discountPercentage is the percentage off (e.g. 7.17 = 7.17% off), not the final price
private fun discountedPrice(price: Double, discountPercentage: Double): Double =
    price * (1 - discountPercentage / 100)

@Composable
private fun DetailsLoadingContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun DetailsErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error happened: $message")
        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = onRetry
        ) {
            Text("Retry")
        }
    }
}
