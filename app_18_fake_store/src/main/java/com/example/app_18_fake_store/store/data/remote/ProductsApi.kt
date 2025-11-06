package com.example.app_18_fake_store.store.data.remote

import retrofit2.http.GET

interface ProductsApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

}