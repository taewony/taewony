package com.example.cupcake.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cupcake.viewmodel.OrderViewModel

@Composable
fun PickupScreen(
    viewModel: OrderViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val dates = listOf("Today", "Tomorrow", "Next Week")

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select pickup date", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        dates.forEach { date ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.date = date }
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(date)
                RadioButton(
                    selected = viewModel.date == date,
                    onClick = { viewModel.date = date }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onBack) { Text("Back") }
            Button(onClick = onNext, enabled = viewModel.date.isNotEmpty()) { Text("Next") }
        }
    }
}
