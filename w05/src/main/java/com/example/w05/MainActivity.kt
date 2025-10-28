package com.example.w05

import com.example.w05.ui.theme.TaewonyTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaewonyTheme {
                // TODO: 여기에 카운터와 스톱워치 UI를 만들도록 안내
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CounterApp()
                    Spacer(modifier = Modifier.height(32.dp))
                    StopWatchApp()
                }
            }
        }
    }
}

@Composable
fun CounterApp() {
    // TODO: 상태 변수 정의
    // TODO: 버튼 클릭 시 상태 변경 로직 작성
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Count: 0") // TODO: 상태값 표시
        Button(onClick = { /* TODO: 카운터 증가 로직 */ }) {
            Text("Increase")
        }
    }
}

@Composable
fun StopWatchApp() {
    // TODO: 시작, 중지, 리셋 버튼과 시간 표시 로직 작성
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "00:00") // TODO: 시간 표시
        Row {
            Button(onClick = { /* 시작 */ }) { Text("Start") }
            Button(onClick = { /* 중지 */ }) { Text("Stop") }
            Button(onClick = { /* 리셋 */ }) { Text("Reset") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterAppPreview() {
    CounterApp()
}

@Preview(showBackground = true)
@Composable
fun StopWatchPreview() {
    StopWatchApp()
}