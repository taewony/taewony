# Todo-Triple 앱 Room 및 DataStore 단계별 적용 계획 (개정판)

이 문서는 `todo_triple` 모듈에 **DataStore**와 **Room**을 순차적으로 적용하여, MVVM 아키텍처 기반의 영속성 있는 데이터 관리를 구현하기 위한 개발 계획을 정의합니다.

특히, 관심사 분리 원칙(SoC)에 따라 `ViewModel`을 **`PreferenceViewModel`**과 **`TodoViewModel`**로 분리하여 설정 관련 로직과 할 일 목록 관리 로직을 명확하게 구분합니다.

---

## Part 1: DataStore를 이용한 설정 기능 구현

### 1단계: 프로젝트 설정 및 의존성 추가
- **KSP(Kotlin Symbol Processing) 플러그인**을 `build.gradle.kts`에 추가합니다.
- **Room, DataStore, ViewModel-Compose, Navigation** 등 필요한 모든 라이브러리를 `dependencies` 블록에 추가합니다.

### 2단계: DataStore 구현 (Repository)
- **`SortOrder` Enum**: 정렬 옵션(`TIME`, `TASK_NAME`)을 정의하는 열거형 클래스를 `data/settings` 패키지에 생성합니다.
- **`SettingsRepository.kt`**: `preferencesDataStore`를 사용하여 정렬 순서 값을 저장하고, `Flow<SortOrder>`로 외부에 노출하는 리포지토리를 `data/settings` 패키지에 구현합니다. 이 리포지토리는 데이터의 영속성을 책임집니다.

### 3단계: ViewModel 및 UI 구현 (설정 연동)
- **`PreferenceViewModel.kt` 생성**:
    - `viewmodel` 패키지에 설정 관련 로직만을 담당하는 `PreferenceViewModel`을 새로 생성합니다.
    - `SettingsRepository`와 연동하여 `sortOrder: StateFlow<SortOrder>`를 UI에 노출하고, `updateSortOrder` 함수를 구현하여 사용자의 선택을 DataStore에 저장하도록 합니다.
- **`TodoViewModel.kt` 리팩토링**:
    - 기존 `TodoViewModel`에서 `SettingsRepository` 관련 로직(정렬 순서 상태 및 업데이트 함수)을 모두 제거하여 할 일 목록 관리에만 집중하도록 수정합니다. (이 단계에서는 `todoItems`를 임시 빈 리스트로 유지합니다.)
- **`PreferenceScreen.kt` 구현**:
    - `screen` 패키지 내에 설정 화면을 구현합니다.
    - `PreferenceViewModel`을 주입받아 현재 정렬 상태를 표시하고, `RadioButton`을 통해 정렬 순서를 변경할 수 있는 UI를 제공합니다.
- **`MainScreen.kt` 및 `AppContent.kt` 수정**:
    - 상단 앱 바 또는 네비게이션 드로어에 설정 메뉴를 추가하고, 클릭 시 `PreferenceScreen`으로 이동하도록 탐색 로직을 구성합니다.

### 4단계: DataStore 기능 테스트
- 앱을 실행하여 `PreferenceScreen`에서 정렬 순서를 변경합니다.
- 앱을 완전히 종료했다가 다시 시작했을 때, 이전에 선택한 정렬 순서가 `PreferenceScreen`에 올바르게 유지되는지 확인하여 DataStore의 영속성 기능이 정상 작동하는지 검증합니다.

---

## Part 2: Room을 이용한 ToDo 목록 구현

### 5단계: Room 데이터베이스 구현
- **`TodoItem.kt` (Entity)**: `data/local` 패키지에 `@Entity` 어노테이션을 사용하여 `todo_items` 테이블과 매핑될 데이터 클래스를 정의합니다.
- **`TodoDao.kt` (DAO)**: `data/local` 패키지에 `@Dao` 인터페이스를 정의하고, `Flow<List<TodoItem>>`을 반환하는 정렬 쿼리 메서드와 CRUD 메서드를 추가합니다.
- **`AppDatabase.kt` (Database)**: `data/local` 패키지에 Room 데이터베이스 클래스를 싱글톤 패턴으로 구현합니다.

### 6단계: ViewModel 계층 확장 (Room 연동)
- **`TodoViewModel.kt` 확장**:
    - `AppDatabase`를 통해 `TodoDao` 인스턴스를 초기화하고, `SettingsRepository`를 주입받습니다.
    - `flatMapLatest` 연산자를 사용하여 `settingsRepository.sortOrderFlow`의 변경에 따라 `TodoDao`의 정렬 쿼리(`getItemsSortedByName()`, `getItemsSortedByTime()`)를 동적으로 전환합니다.
    - 그 결과를 `todoItems: StateFlow<List<TodoItem>>`로 UI에 노출하여 기존의 인메모리 리스트를 대체합니다.
    - `addTodo`, `deleteTodo` 등 CRUD 함수가 Room 데이터베이스와 상호작용하도록 로직을 수정합니다.

### 7단계: UI 계층 수정 (Room 연동)
- **`MainScreen.kt` 수정**: `todoViewModel.todoItems`에서 수집한 `List<TodoItem>`을 `LazyColumn`에 표시하도록 수정합니다.
- **`AddScreen.kt` 수정**: 저장 버튼 클릭 시 `todoViewModel.addTodo(task)`를 호출하여 새로운 할 일을 데이터베이스에 추가하도록 수정합니다.

### 8단계: 통합 테스트
- **기능 테스트**:
    1. 할 일을 추가하고, 목록이 메인 화면에 나타나는지 확인합니다.
    2. 앱을 재시작해도 추가한 할 일 목록이 그대로 유지되는지 확인합니다. (Room 영속성 테스트)
    3. 설정 화면에서 정렬 순서를 '이름 순'으로 변경하고 메인 화면으로 돌아왔을 때, 목록이 이름 순으로 정렬되는지 확인합니다. (DataStore와 Room 연동 테스트)
- **UI/UX 최종 검토**: 목록이 비어있을 때 안내 문구 표시 등 모든 UI 요구사항이 충족되었는지 최종 확인합니다.

---

## 부록: DataStore를 통한 설정 변경 실행 흐름

`PreferenceScreen`에서 사용자가 정렬 옵션을 변경했을 때 데이터가 처리되는 흐름은 다음과 같은 단방향 데이터 흐름(UDF)을 따릅니다.

1.  **UI (사용자 입력)**
    - `PreferenceScreen`의 `RadioButton`이 클릭됩니다.
    - `selectable` Modifier의 `onClick` 람다가 실행되어 `preferenceViewModel.updateSortOrder(newSortOrder)`를 호출합니다.

2.  **ViewModel (상태 업데이트 요청)**
    - `PreferenceViewModel`의 `updateSortOrder` 함수는 `SettingsRepository`의 `updateSortOrder` 함수를 호출하여 데이터 변경을 요청합니다.

3.  **Repository (데이터 소스 접근)**
    - `SettingsRepository`는 `DataStore.edit { ... }`를 사용하여 "sort_order" 키에 해당하는 값을 새로운 값으로 업데이트합니다.

4.  **DataStore (데이터 영속화 및 발행)**
    - 디스크에 있는 `settings.preferences_pb` 파일의 내용이 변경됩니다.
    - 이 변경을 감지한 `DataStore.data` `Flow`가 새로운 `Preferences` 객체를 자동으로 발행(emit)합니다.

5.  **Flow 데이터 스트림 전파**
    - `SettingsRepository`의 `sortOrderFlow`는 `dataStore.data`를 `map`하여 `SortOrder` Enum 값을 발행합니다.
    - `PreferenceViewModel`의 `sortOrder` `StateFlow`는 `sortOrderFlow`를 구독(`stateIn`)하고 있으므로 새로운 `SortOrder` 값으로 상태를 업데이트합니다.

6.  **UI (상태 수집 및 재구성)**
    - `PreferenceScreen`에서 `viewModel.sortOrder.collectAsState()`를 통해 `StateFlow`를 구독하고 있습니다.
    - `StateFlow`의 값이 변경되면, Compose 런타임이 이를 감지하고 `PreferenceScreen`을 자동으로 재구성(Recomposition)합니다.
    - 결과적으로, UI의 `RadioButton` 선택 상태가 새로운 정렬 순서에 맞게 업데이트됩니다.