package com.example.stopwatchgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stopwatchgame.screen.GameScreen
import com.example.stopwatchgame.ui.theme.StopwatchGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopwatchGameTheme {
                // 2. 화면 배경 설정 (MaterialTheme의 기본 색상 사용)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. UI의 최상위 Composable인 GameScreen 호출
                    // GameScreen 내부에서 ViewModel이 생성 및 관리됩니다.
                    GameScreen()
                }
            }
        }
    }
}