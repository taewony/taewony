package com.example.fake_store.store.data.remote

import com.example.fake_store.store.domain.model.Product
import com.example.fake_store.store.domain.model.Rating

// API의 JSON 필드 이름과 일치시켜야 합니다.
// 예시: @SerializedName("product_name")
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto
)

data class RatingDto(
    val rate: Double,
    val count: Int
)

fun ProductDto.toProduct(): Product {
    return Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = rating.toRating()
    )
}

fun RatingDto.toRating(): Rating {
    return Rating(
        rate = rate,
        count = count
    )
}
