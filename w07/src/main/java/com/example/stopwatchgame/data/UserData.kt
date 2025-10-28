package com.example.stopwatchgame.data

/**
 * 사용자의 영구적인 정보 및 누적된 게임 결과를 나타내는 클래스.
 * @property userId 사용자 식별자 (DB 사용 시 Primary Key)
 * @property userName 사용자 이름
 * @property totalScore 누적된 총 게임 점수
 */
data class UserData(
    val userId: String,
    val level: Int = 0,
    val totalScore: Int = 0
)

// 싱글 플레이어를 위한 초기 사용자 데이터 정의
val INITIAL_USER_DATA = UserData(
    userId = "player1",
    level = 0,
    totalScore = 0
)
