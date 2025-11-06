package com.example.app_18_fake_store.store.data.repository

import com.example.app_18_fake_store.store.data.remote.ProductsApi
import com.example.app_18_fake_store.store.data.remote.toProduct
import com.example.app_18_fake_store.store.domain.model.Product
import com.example.app_18_fake_store.store.domain.repository.ProductsRepository
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(
    private val productsApi: ProductsApi
) : ProductsRepository {

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val productDtos = productsApi.getProducts()
            Result.success(productDtos.map { it.toProduct() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}