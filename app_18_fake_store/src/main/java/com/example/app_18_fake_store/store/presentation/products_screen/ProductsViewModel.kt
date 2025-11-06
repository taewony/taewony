package com.example.app_18_fake_store.store.presentation.products_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_18_fake_store.store.domain.repository.ProductsRepository
import com.example.app_18_fake_store.store.presentation.util.sendEvent
import com.example.app_18_fake_store.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsViewState())
    val state = _state.asStateFlow()

    init {
        getProducts()
    }

    fun getProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            productsRepository.getProducts()
                .onSuccess { products ->
                    _state.update {
                        it.copy(products = products)
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            error = error.message
                        )
                    }
                    sendEvent(Event.Toast(error.message ?: "An unknown error occurred"))
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
