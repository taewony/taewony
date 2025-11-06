package com.example.fake_store.store.presentation.products_screen

import com.example.fake_store.store.domain.model.Product

data class ProductsViewState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)
