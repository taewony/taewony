package com.example.stopwatchgame.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stopwatchgame.ui.theme.StopwatchGameTheme
import java.util.concurrent.TimeUnit

@Composable
fun GameScreen(
    // ViewModel을 생성하고, Composable의 Lifecycle에 연결합니다.
    viewModel: GameViewModel = viewModel()
) {
    // 1. 상태 관찰 (State Collection):
    // ViewModel의 단일 StateFlow<GameUiState>를 관찰하여 상태가 변경될 때마다 UI를 재구성합니다.
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 사용자 및 점수 정보
        Text(
            text = "플레이어: ${uiState.userData.userId} (총점: ${uiState.userData.totalScore}점)",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "현재 레벨: ${uiState. userData.level}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 설정 정보
        Text(
            text = "목표 시간: ${formatTime(uiState.gameConfig.targetTimeMs)}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "오차 범위: ±${formatTime(uiState.gameConfig.toleranceMs)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // 2. 스톱워치 디스플레이 컴포넌트 (State Hoisting)
        StopwatchDisplay(currentTimeMs = uiState.gameData.currentTimeMs)

        Spacer(modifier = Modifier.height(64.dp))

        // 3. 컨트롤 버튼 (Event Forwarding)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.onStartClicked() }, // 이벤트 전달
                enabled = !uiState.gameData.isRunning // 실행 중이 아닐 때만 활성화
            ) {
                Text("Start", fontSize = 20.sp)
            }

            Button(
                onClick = { viewModel.onStopClicked(uiState.gameData.currentTimeMs) }, // 이벤트 전달
                enabled = uiState.gameData.isRunning // 실행 중일 때만 활성화
            ) {
                Text("Stop", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        // 피드백 메시지 출력
        uiState.feedbackMessage?.let { message ->
            Text(
                text = message,
                color = if (message.contains("정확")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontSize = 22.sp
            )
        }
    }
}

// 스톱워치 시간을 표시하는 하위 Composable (재사용 가능한 UI 요소)
@Composable
fun StopwatchDisplay(currentTimeMs: Long) {
    Text(
        text = formatTime(currentTimeMs),
        fontSize = 72.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

// 시간 포맷팅 유틸리티 함수
fun formatTime(timeMs: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMs)
    val totalMillis = timeMs % 1000

    val seconds = totalSeconds
    val millis = totalMillis / 10 // 100분의 1초 단위로 표시

    return String.format("%02d.%02d", seconds, millis)
}
