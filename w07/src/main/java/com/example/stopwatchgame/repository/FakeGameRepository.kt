package com.example.stopwatchgame.repository

import com.example.stopwatchgame.data.GameConfig
import com.example.stopwatchgame.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * GameRepository의 가짜 구현체입니다.
 * 실제 데이터베이스 대신 메모리 내 StateFlow에 데이터를 저장합니다.
 */
class FakeGameRepository : GameRepository {

    // 메모리 내에 UserData를 저장하고 외부로 Flow를 노출합니다.
    private val _userData = MutableStateFlow(UserData(userId = "guest"))

    // 메모리 내에 GameConfig를 저장합니다.
    private val _gameConfig = MutableStateFlow(GameConfig())

    override fun getUserData(): Flow<UserData> = _userData.asStateFlow()

    override fun getGameConfig(): Flow<GameConfig> = _gameConfig.asStateFlow()

    override suspend fun addPoint() {
        _userData.update { currentUserData ->
            val newTotalScore = currentUserData.totalScore + 1
            val newLevel = if (newTotalScore > 0 && newTotalScore % 3 == 0) {
                currentUserData.level + 1
            } else {
                currentUserData.level
            }
            currentUserData.copy(totalScore = newTotalScore, level = newLevel)
        }
    }
}
