# w07 스톱워치 게임 앱 단계별 설계서

본 문서는 w07 스톱워치 게임 앱을 MVVM 아키텍처의 각 요소를 단계적으로 학습할 수 있도록 재구성하는 설계 계획을 설명합니다. 최종 목표는 데이터의 종류(휘발성/영속성)에 따라 다르게 처리하는 방법을 이해하고, SOLID 원칙에 기반한 확장 가능한 구조를 만드는 것입니다.

---

### 1단계: UI와 휘발성 상태(GameData)를 이용한 기본 앱 구현

**목표:** 화면(View)과 화면의 상태를 관리하는 뷰모델(ViewModel)의 기본적인 역할에 집중합니다. 데이터 영속성은 전혀 고려하지 않고, 앱이 실행되는 동안만 유지되는 데이터를 다룹니다.

- **상태 관리 방식:**
    - **`StateFlow` vs `MutableState`**: `MutableState`는 Compose 프레임워크에 내장된 간단한 상태 홀더로, 주로 Composable 함수 내에서 UI 상태를 관리할 때 사용됩니다. 반면, `StateFlow`는 코루틴 기반의 스트림 데이터 홀더로, ViewModel에서 더 복잡한 비즈니스 로직을 처리하고 UI 레이어에 상태를 안전하게 노출하는 데 적합합니다. 
    - **본 프로젝트에서는 ViewModel과 UI 레이어의 역할을 명확히 분리하는 아키텍처를 학습하기 위해 `StateFlow`를 사용합니다.**
---
- **구현 내용:**
    1.  **`GameData` 정의:** 스톱워치 시간, 랩 타임 등 게임 진행과 관련된 순수 데이터 클래스를 정의합니다.
    2.  **`GameViewModel` 구현:**
        - `UiState`를 `StateFlow`로 관리하여 UI가 관찰할 수 있도록 합니다.
        - 스톱워치 시작, 중지, 리셋 등 UI 이벤트를 처리하는 메서드를 구현합니다. 이 메서드들은 `UiState` 내의 `GameData` 상태를 변경합니다.
        - Repository나 다른 의존성 없이 ViewModel 자체적으로 모든 로직을 처리합니다.
    3.  **`GameScreen` 구현:**
        - `GameViewModel`의 `uiState`를 구독하여 화면을 그립니다.
        - 버튼 클릭과 같은 사용자 입력을 `GameViewModel`의 메서드로 전달합니다.
---
- **UI State 정의:**
    ```kotlin
    data class GameUiState(
        val gameData: GameData = GameData()
    )
    
    data class GameData(
        val time: Long = 0L,
        val laps: List<Long> = emptyList(),
        val isRunning: Boolean = false
    )
    ```

**결과물:** 화면에 스톱워치가 표시되고 시작, 중지, 리셋 기능이 동작하는 최소 기능의 앱. 앱을 종료하면 모든 데이터는 사라집니다.

---

### 2단계: 영속성 데이터(GameConfig, UserData)의 도입 및 레벨 시스템 설계

**목표:** 앱을 재시작해도 유지되어야 하는 설정(`GameConfig`)과 사용자 데이터(`UserData`)의 개념을 도입하고, 사용자 데이터와 연동되는 간단한 게임 로직(레벨 시스템)을 추가합니다.
---
- **구현 내용:**
    1.  **`GameConfig`, `UserData` 정의:**
        - `GameConfig`: 목표 시간, 진동 설정 등 게임 설정을 담는 데이터 클래스.
        - `UserData`: 최고 기록, 사용자 이름, 총점(`totalScore`), 등급(`level`)을 담는 데이터 클래스.
    2.  **`GameViewModel` 확장:**
        - `UiState`에 `GameConfig`와 `UserData`를 추가하여 관리합니다.
        - **레벨 시스템 로직 추가**: 랩 타임을 기록할 때마다 `totalScore`가 1씩 증가하고, `totalScore`가 3의 배수가 될 때마다 `level`이 1씩 오르는 로직을 ViewModel에 구현합니다.
        - 설정 변경이나 최고 기록 갱신과 같은 로직을 ViewModel 내에 간단히 구현합니다.
---
- **UI State 정의:**
    ```kotlin
    data class GameUiState(
        val gameData: GameData = GameData(),
        val gameConfig: GameConfig = GameConfig(),
        val userData: UserData = UserData()
    )

    data class GameConfig(
        val targetTime: Long = 10000L,
        val vibrationEnabled: Boolean = true
    )

    data class UserData(
        val name: String = "Player",
        val bestRecord: Long = 0L,
        val totalScore: Int = 0,
        val level: Int = 1
    )
    ```
---
**결과물:** UI에 설정, 최고 기록, 현재 점수와 등급이 표시됩니다. 랩 타임을 기록하면 점수가 오르고, 특정 점수에 도달하면 등급이 올라가는 것을 확인할 수 있습니다. 데이터는 여전히 앱 종료 시 사라집니다.

---

### 3단계: Repository 패턴 도입 (Interface + Fake Implementation)

**목표:** ViewModel에서 데이터 처리 로직을 분리하여 Repository 패턴의 필요성을 학습합니다. ViewModel은 "어떻게" 저장하는지가 아닌, "저장한다"라는 사실에만 의존하게 만듭니다.
---
- **구현 내용:**
    1.  **`GameRepository` 인터페이스 정의:**
        - `fun saveConfig(config: GameConfig)`
        - `fun loadConfig(): Flow<GameConfig>`
        - `fun saveUserData(user: UserData)`
        - `fun loadUserData(): Flow<UserData>`
        와 같이 데이터 영속성에 필요한 메서드를 추상적으로 정의합니다.
    2.  **`FakeGameRepository` 구현:**
        - `GameRepository` 인터페이스를 구현하는 "가짜" 클래스를 만듭니다.
        - 이 클래스는 데이터를 DB나 파일에 저장하는 대신, 내부 변수(예: `MutableStateFlow`)에 저장하여 실제 저장소처럼 동작을 흉내 냅니다.
---
    3.  **`GameViewModel` 리팩토링:**
        - 생성자에서 `GameRepository` 인터페이스를 의존성으로 주입받습니다.
        - 기존에 ViewModel이 직접 처리하던 `GameConfig`, `UserData` 관련 로직을 모두 Repository의 메서드를 호출하도록 변경합니다.
---
- **UI State 정의:** (2단계와 동일)
    ```kotlin
    data class GameUiState(
        val gameData: GameData = GameData(),
        val gameConfig: GameConfig = GameConfig(),
        val userData: UserData = UserData()
    )
    ```
---
**결과물:** 앱의 동작은 이전 단계와 동일하지만, 내부적으로 ViewModel과 데이터 로직이 분리되어 테스트가 용이하고 확장성 있는 구조가 됩니다.

---

### 4단계: Local Data Source 구현 (이론)

**참고:** 이 단계는 실제 구현은 진행하지 않으며, 아키텍처의 최종 단계를 이해하기 위한 이론적인 가이드입니다.

**목표:** 추상화된 Repository에 실제 구현체를 연결하여 데이터를 로컬에 저장하는 방법을 학습합니다.
---
- **구현 내용 (이론):**
    1.  **`LocalDataSource` 구현:**
        - Jetpack DataStore 또는 Room Database를 사용하여 `GameConfig`와 `UserData`를 실제로 파일이나 DB에 읽고 쓰는 클래스를 구현합니다.
    2.  **`GameRepositoryImpl` 구현:**
        - `GameRepository` 인터페이스를 구현하는 "실제" 클래스를 만듭니다.
        - 이 클래스는 생성자에서 `LocalDataSource`를 주입받아 실제 I/O 작업을 수행합니다.
    3.  **의존성 주입(Dependency Injection) 설정:**
        - 앱의 진입점에서 `GameRepositoryImpl`과 `LocalDataSource`의 인스턴스를 생성하고, `GameViewModel`에 `GameRepositoryImpl`을 주입하도록 설정합니다.
---
- **UI State 정의:** (2, 3단계와 동일)
    ```kotlin
    data class GameUiState(
        val gameData: GameData = GameData(),
        val gameConfig: GameConfig = GameConfig(),
        val userData: UserData = UserData()
    )
    ```
---
**결과물 (이론상):** 앱을 종료했다가 다시 켜도 설정이나 최고 기록, 점수, 등급이 유지되는 완전한 기능을 갖춘 앱이 완성됩니다. DI를 통해 `FakeGameRepository`와 `GameRepositoryImpl`을 손쉽게 교체하며 테스트할 수 있음을 이해하게 됩니다.