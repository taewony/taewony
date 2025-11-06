package com.example.fake_store.store.data.repository

import com.example.fake_store.store.data.remote.ProductsApi
import com.example.fake_store.store.data.remote.toProduct
import com.example.fake_store.store.domain.model.Product
import com.example.fake_store.store.domain.repository.ProductsRepository
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
/*
    override suspend fun getProducts(): Result<List<Product>> {
        // 2단계: 가짜 데이터 반환
        val fakeProducts = listOf(
            Product(
                id = 1,
                title = "샘플 상품 1 (로컬)",
                price = 109.95,
                description = "멋진 샘플 상품입니다.",
                category = "electronics",
                image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
                rating = Rating(rate = 3.9, count = 120)
            ),
            Product(
                id = 2,
                title = "샘플 상품 2 (로컬)",
                price = 22.3,
                description = "또 다른 멋진 샘플 상품입니다.",
                category = "jewelery",
                image = "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg",
                rating = Rating(rate = 4.1, count = 259)
            )
        )
        delay(1500) // 인위적인 로딩 딜레이
        return Result.success(fakeProducts)
    }
 */