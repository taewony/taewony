package com.example.stopwatchgame.data

/**
 * 게임 플레이 중의 실시간 상태 데이터를 나타내는 클래스.
 * ViewModel의 UI State에 포함될 수 있습니다.
 * @property currentTimeMs 현재 스톱워치 시간 (밀리초 단위)
 * @property isRunning 스톱워치가 현재 작동 중인지 여부
 */
data class GameData(
    val currentTimeMs: Long = 0L,
    val isRunning: Boolean = false,
    val currentPoint: Int = 0 // 현재 게임 세션에서 획득한 포인트
)
