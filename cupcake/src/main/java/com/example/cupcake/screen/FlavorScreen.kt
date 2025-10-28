package com.example.cupcake.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cupcake.viewmodel.OrderViewModel

@Composable
fun FlavorScreen(
    viewModel: OrderViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val flavors = listOf("Vanilla", "Chocolate", "Strawberry")

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select your cupcake flavor", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        flavors.forEach { flavor ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.flavor = flavor }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(flavor)
                RadioButton(
                    selected = viewModel.flavor == flavor,
                    onClick = { viewModel.flavor = flavor }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = onNext, enabled = viewModel.flavor.isNotEmpty()) { Text("Next") }
        }
    }
}
