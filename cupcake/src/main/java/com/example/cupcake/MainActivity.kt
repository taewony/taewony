package com.example.cupcake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cupcake.ui.theme.CupcakeTheme
import com.example.cupcake.viewmodel.OrderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CupcakeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val orderViewModel: OrderViewModel = viewModel()
                    CupcakeApp(viewModel = orderViewModel)
                }
            }
        }
    }
}
