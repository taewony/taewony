package com.example.stopwatchgame.data

/**
 * GameScreen 전체의 UI 상태를 담는 단일 데이터 클래스 (Single Source of Truth).
 * ViewModel은 이 클래스의 인스턴스를 MutableStateFlow로 관리합니다.
 *
 * @property config 현재 게임 설정 (GameConfig) - Repository에서 로드/업데이트
 * @property gameData 현재 게임 진행 상태 (GameData) - 실시간 업데이트
 * @property userDataMap 사용자 점수 맵 (Map<userId, UserData>) - Repository에서 로드
 * @property isGameActive 게임이 현재 플레이 가능한 상태인지 여부 (UI 활성화 상태)
 */
data class GameUiState(
    // 영구 데이터 (Repository에서 관리)
    val gameConfig: GameConfig = DEFAULT_GAME_CONFIG,
    val userData: UserData = INITIAL_USER_DATA,

    // 휘발성 게임 데이터 (ViewModel에서 로직 처리)
    val gameData: GameData = GameData(), // currentTimeMs, isRunning, currentPoint 포함

    // 기타 UI 관련 상태
    val isGameActive: Boolean = true,
    val feedbackMessage: String? = null // 사용자에게 표시할 메시지 ("정확!", "실패", null)
)