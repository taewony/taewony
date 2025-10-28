package com.example.cupcake.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cupcake.viewmodel.OrderViewModel

@Composable
fun SummaryScreen(
    viewModel: OrderViewModel,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Order Summary", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Flavor: ${viewModel.flavor}")
        Text("Pickup Date: ${viewModel.date}")
        Text("Quantity: ${viewModel.quantity}")
        Spacer(Modifier.height(32.dp))
        Button(onClick = onRestart) { Text("Place another order") }
    }
}
