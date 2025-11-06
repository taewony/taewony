package com.example.fake_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.fake_store.ui.theme.TaewonyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// 간단한 의존성 클래스
class Greeting @Inject constructor() {
    fun message(): String = "Hello from Hilt!"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt가 주입하는 의존성
    @Inject lateinit var greeting: Greeting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaewonyTheme {
                Text(text = greeting.message())
            }
        }
    }
}
