package com.example.stopwatchgame.data

/**
 * 스톱워치 게임의 설정값을 나타내는 데이터 클래스.
 * @property targetTimeMs 목표 시간 (밀리초 단위)
 * @property toleranceMs 오차 범위 (밀리초 단위). 이 범위 내에 멈춰야 점수를 얻습니다.
 */
data class GameConfig(
    val targetTimeMs: Long = 3000L,
    val toleranceMs: Long = 200L
)

// 기본 설정값: 5.000초 (5000ms) 목표, ±0.100초 (100ms) 오차
val DEFAULT_GAME_CONFIG = GameConfig(
    targetTimeMs = 3000L,
    toleranceMs = 200L
)