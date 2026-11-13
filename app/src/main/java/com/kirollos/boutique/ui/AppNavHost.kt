package com.kirollos.boutique.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kirollos.boutique.ui.productDetails.ProductDetailsRoute
import com.kirollos.boutique.ui.productDetails.ProductDetailsViewModel.Companion.PRODUCT_ID_ARG
import com.kirollos.boutique.ui.productList.ProductListRoute

private const val PRODUCT_LIST_ROUTE = "ProductListScreen"
private const val PRODUCT_DETAILS_ROUTE = "ProductDetailsScreen"

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = PRODUCT_LIST_ROUTE,
    ) {
        composable(route = PRODUCT_LIST_ROUTE) {
            ProductListRoute(
                navigateToDetails = { productId ->
                    navController.navigate("$PRODUCT_DETAILS_ROUTE/$productId") {
                        // Avoid stacking duplicate details screens on fast double taps
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = "$PRODUCT_DETAILS_ROUTE/{$PRODUCT_ID_ARG}",
            arguments = listOf(navArgument(PRODUCT_ID_ARG) { type = NavType.IntType })
        ) {
            ProductDetailsRoute(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
