package com.example.cupcake

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.cupcake.navigation.CupcakeScreen
import com.example.cupcake.screen.FlavorScreen
import com.example.cupcake.screen.PickupScreen
import com.example.cupcake.screen.StartScreen
import com.example.cupcake.screen.SummaryScreen
import com.example.cupcake.ui.*
import com.example.cupcake.viewmodel.OrderViewModel

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel
) {
    // ✅ Navigation 3의 핵심: 상태 기반 백스택
    val backStack = remember { mutableStateListOf<Any>(CupcakeScreen.Start) }

    // ✅ NavDisplay: 상태에 따라 화면을 표시
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is CupcakeScreen.Start -> NavEntry(key) {
                    StartScreen(
                        onNext = { backStack += CupcakeScreen.Flavor }
                    )
                }

                is CupcakeScreen.Flavor -> NavEntry(key) {
                    FlavorScreen(
                        viewModel = viewModel,
                        onNext = { backStack += CupcakeScreen.Pickup },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }

                is CupcakeScreen.Pickup -> NavEntry(key) {
                    PickupScreen(
                        viewModel = viewModel,
                        onNext = { backStack += CupcakeScreen.Summary },
                        onBack = { backStack.removeLastOrNull() }
                    )
                }

                is CupcakeScreen.Summary -> NavEntry(key) {
                    SummaryScreen(
                        viewModel = viewModel,
                        onRestart = {
                            viewModel.resetOrder()
                            backStack.clear()
                            backStack += CupcakeScreen.Start
                        }
                    )
                }
                else -> NavEntry(key) {
                    StartScreen(
                        onNext = { backStack += CupcakeScreen.Flavor }
                    )
                }
            }
        }
    )
}
