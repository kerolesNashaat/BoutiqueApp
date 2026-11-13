# 🛍️ Boutique

An Android app for browsing products from the [DummyJSON](https://dummyjson.com/) API.
I built it as a timed live-coding exercise to practice modern Android architecture
and Jetpack Compose.

## Features

- **Product grid**: a 2-column grid with each product's thumbnail, title, brand, price and discount badge
- **Screen states**: loading, empty, error with a **Retry** button, and content
- **Search**: live search with a 400 ms debounce. If you keep typing, the earlier request is cancelled (`flatMapLatest`)
- **Product details**: image, title, description, original price and discounted price
- **Survives rotation**: data is kept in the ViewModel, so rotating the screen doesn't reload it

## Tech Stack

| Layer | Libraries |
|---|---|
| UI | Jetpack Compose, Material 3, Navigation Compose, Coil 3 |
| Architecture | MVVM + Clean Architecture (data / domain / ui), single `StateFlow<UiState>` per screen |
| DI | Hilt |
| Networking | Retrofit, OkHttp, kotlinx.serialization |
| Async | Kotlin Coroutines & Flow |
| Testing | JUnit, kotlinx-coroutines-test, Turbine, Truth |

## Architecture

```
com.kirollos.boutique
├── data
│   ├── dto          # API response models (nullable-safe)
│   ├── mapper       # DTO → domain mapping
│   ├── repository   # ProductRepositoryImpl
│   └── source       # Retrofit ApiService
├── domain
│   ├── model        # Product, ProductList, Review, ...
│   └── repository   # ProductRepository interface
├── di               # Hilt modules (network, repository, dispatchers)
└── ui
    ├── productList     # Screen, ViewModel, UiState, UiEvent, UiEffect
    └── productDetails  # Screen, ViewModel, UiState, UiEvent, UiEffect
```

Each screen follows a unidirectional data flow:
- **UiState**: a single `StateFlow` that the screen renders
- **UiEvent**: user actions sent to the ViewModel through `onEvent()`
- **UiEffect**: one-time actions such as navigation, sent through a `Channel`

## API

| Purpose | Endpoint |
|---|---|
| Product list | `GET /products?limit=20` |
| Search | `GET /products/search?q={query}` |
| Details | `GET /products/{id}` |

## Getting Started

1. Clone the repo
2. Open it in Android Studio
3. Run on a device or emulator with API 24 or higher. No API key is needed.

Run the unit tests:
```bash
./gradlew test
```

## Roadmap

- [ ] Load more products as the user scrolls (pagination)
- [ ] Show all product images in a `HorizontalPager` on the details screen
- [ ] Add a dedicated Retry action for failed searches
- [ ] Add more ViewModel tests, including the error path
