package com.example.cupcake.navigation

sealed interface CupcakeScreen {
    data object Start : CupcakeScreen
    data object Flavor : CupcakeScreen
    data object Pickup : CupcakeScreen
    data object Summary : CupcakeScreen
}
