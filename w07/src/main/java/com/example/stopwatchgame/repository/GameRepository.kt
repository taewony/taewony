package com.example.stopwatchgame.repository

import com.example.stopwatchgame.data.GameConfig
import com.example.stopwatchgame.data.UserData
import kotlinx.coroutines.flow.Flow

/**
 * 게임의 영속성 데이터(UserData, GameConfig)를 관리하는 리포지토리 인터페이스입니다.
 * ViewModel은 이 인터페이스에만 의존하며, 실제 구현(Fake, Local, Remote)은 모릅니다.
 */
interface GameRepository {

    /**
     * 사용자 데이터를 Flow 형태로 제공합니다.
     * 데이터가 변경되면 새로운 UserData가 Flow를 통해 방출됩니다.
     */
    fun getUserData(): Flow<UserData>

    /**
     * 게임 설정을 Flow 형태로 제공합니다.
     */
    fun getGameConfig(): Flow<GameConfig>

    /**
     * 성공적으로 포인트를 획득했을 때 호출됩니다.
     * 내부적으로 점수 및 레벨을 업데이트하는 로직을 수행합니다.
     */
    suspend fun addPoint()
}
