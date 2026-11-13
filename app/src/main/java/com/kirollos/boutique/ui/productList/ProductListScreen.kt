package com.kirollos.boutique.ui.productList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
fun ProductListRoute(
    viewModel: ProductListViewModel = hiltViewModel(),
    navigateToDetails: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ProductListUiEffect.NavigateToProductDetails -> navigateToDetails(effect.productId)
            }
        }
    }
    ProductListScreen(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ProductListScreen(
    state: ProductListUiState,
    onEvent: (ProductListUiEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.productsError != null -> MessageContent(
                icon = Icons.Default.CloudOff,
                title = "Something went wrong",
                message = state.productsError,
                onRetry = { onEvent(ProductListUiEvent.GetProducts) }
            )

            state.productList == null || state.isEmpty -> MessageContent(
                icon = Icons.Default.SearchOff,
                title = "No products yet",
                message = "Check back later for new arrivals.",
                onRetry = { onEvent(ProductListUiEvent.GetProducts) }
            )

            else -> ProductsContent(
                products = state.productList.products,
                searchText = state.searchText.orEmpty(),
                isSearchLoading = state.isSearchLoading,
                searchError = state.searchError,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun ProductsContent(
    products: List<Product>,
    searchText: String,
    isSearchLoading: Boolean,
    searchError: String?,
    onEvent: (ProductListUiEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar stays visible in every sub-state so the user can always edit the query
        SearchTextField(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            searchText = searchText,
            onEvent = onEvent
        )
        Box(modifier = Modifier.height(2.dp).fillMaxWidth()) {
            if (isSearchLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        when {
            !searchError.isNullOrBlank() -> MessageContent(
                icon = Icons.Default.CloudOff,
                title = "Search failed",
                message = searchError,
                // TODO: add a RetrySearch event in the ViewModel; re-sending the same text is
                //  swallowed by distinctUntilChanged, so for now the user retries by editing the query
            )

            products.isEmpty() -> MessageContent(
                icon = Icons.Default.SearchOff,
                title = "No results",
                message = "Nothing matches \"${searchText.trim()}\". Try another keyword."
            )

            else -> LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
                state = rememberLazyGridState(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = products,
                    key = { it.id },
                ) { product ->
                    ProductCell(
                        product = product,
                        onClick = { onEvent(ProductListUiEvent.OnProductClicked(product.id)) },
                    )
                }
                // TODO: pagination — trigger loading the next page when the last items become visible
            }
        }
    }
}

@Composable
private fun SearchTextField(
    searchText: String,
    onEvent: (ProductListUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = searchText,
        onValueChange = { onEvent(ProductListUiEvent.SearchText(it)) },
        placeholder = { Text("Search products") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = { onEvent(ProductListUiEvent.SearchText("")) }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
    )
}

@Composable
private fun ProductCell(
    product: Product,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box {
            ProductImage(
                imageUrl = product.thumbnail,
                contentDescription = product.title
            )
            if (product.discountPercentage > 0.0) {
                DiscountBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    discountPercentage = product.discountPercentage
                )
            }
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                // Fixed 2 lines keeps cards in the same row aligned
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                // Some products (e.g. groceries) have no brand; fall back to the category
                text = product.brand.ifBlank { product.category.replaceFirstChar { it.uppercase() } },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale.US).format(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Rating(rating = product.rating)
            }
        }
    }
}

@Composable
private fun ProductImage(imageUrl: String, contentDescription: String) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(R.drawable.ic_placeholder),
        error = painterResource(R.drawable.ic_error),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun DiscountBadge(discountPercentage: Double, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.error)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = "-${"%.0f".format(Locale.US, discountPercentage)}%",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onError,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun Rating(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "%.1f".format(Locale.US, rating),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Loading products…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessageContent(
    icon: ImageVector,
    title: String,
    message: String,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
