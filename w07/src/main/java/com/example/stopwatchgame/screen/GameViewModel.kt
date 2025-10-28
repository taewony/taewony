package com.example.stopwatchgame.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopwatchgame.data.GameData
import com.example.stopwatchgame.data.GameUiState
import com.example.stopwatchgame.repository.FakeGameRepository
import com.example.stopwatchgame.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 게임의 비즈니스 로직을 처리하고 UI 상태를 관리하는 ViewModel입니다.
 * 모든 UI 상태는 단일 MutableStateFlow<GameUiState>로 관리됩니다.
 */
class GameViewModel(
    private val repository: GameRepository = FakeGameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var stopwatchJob: Job? = null

    init {
        Log.d("GameViewModel", "ViewModel created")
        // ViewModel이 생성될 때, Repository가 제공하는 데이터(config, user)의 변경을 감지하고
        // UI 상태를 자동으로 업데이트하도록 구독을 시작합니다.
        viewModelScope.launch {
            combine(repository.getGameConfig(), repository.getUserData()) { config, user ->
                Log.d("GameViewModel", "Data updated: config=$config, user=$user")
                _uiState.update { currentState ->
                    currentState.copy(
                        gameConfig = config,
                        userData = user
                    )
                }
            }.collect() // Flow 구독 시작
        }
    }

    fun onStartClicked() {
        if (uiState.value.gameData.isRunning) return
        Log.d("GameViewModel", "onStartClicked")

        // 휘발성인 게임 데이터를 초기화하고, 타이머를 시작합니다.
        _uiState.update {
            it.copy(
                gameData = GameData(isRunning = true),
                feedbackMessage = null
            )
        }

        stopwatchJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (true) {
                delay(10L)
                _uiState.update {
                    it.copy(gameData = it.gameData.copy(currentTimeMs = System.currentTimeMillis() - startTime))
                }
            }
        }
    }

    fun onStopClicked(finalTimeMs: Long) {
        stopwatchJob?.cancel()
        Log.d("GameViewModel", "onStopClicked: finalTimeMs=$finalTimeMs")

        val config = uiState.value.gameConfig
        val diff = abs(finalTimeMs - config.targetTimeMs)
        val isSuccess = diff <= config.toleranceMs

        // isRunning 상태와 피드백 메시지 같은 휘발성 상태를 즉시 업데이트합니다.
        _uiState.update {
            it.copy(
                gameData = it.gameData.copy(isRunning = false),
                feedbackMessage = if (isSuccess) "정확! 오차($diff ms)" else "실패... 오차($diff ms)"
            )
        }

        // 성공했을 경우, Repository에 점수 업데이트를 요청합니다.
        // 점수와 레벨(userData)은 Repository의 Flow를 통해 자동으로 UI에 반영됩니다.
        if (isSuccess) {
            Log.d("GameViewModel", "Success! Adding point.")
            viewModelScope.launch {
                repository.addPoint()
            }
        }
    }
}
