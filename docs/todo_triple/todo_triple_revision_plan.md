# Todo-Triple 앱 Room 및 DataStore 단계별 적용 계획

이 문서는 기존 `todo_triple` 모듈에 DataStore와 Room을 순차적으로 적용하여, MVVM 아키텍처 기반의 영속성 있는 데이터 관리를 구현하기 위한 개발 계획을 정의합니다. 작업은 **DataStore 설정 기능 구현**과 **Room DB 연동** 두 단계로 나누어 진행합니다.

---

## 1단계: 프로젝트 설정 및 전체 리팩토링

본격적인 기능 구현에 앞서, 프로젝트의 구조를 개선하고 필요한 모든 의존성을 추가합니다.

### 1.1. 의존성 추가 (`build.gradle.kts`)
- **KSP(Kotlin Symbol Processing) 플러그인**을 추가합니다.
- **Room, DataStore, ViewModel-Compose** 등 `docs/todo_triple/plan_todo_triple_revised.md`에 명시된 모든 라이브러리를 `dependencies` 블록에 추가합니다.

### 1.2. 전체 구조 리팩토링
- **패키지 구조화**: `data/local`, `data/settings`, `screen`, `viewmodel` 등 MVVM 아키텍처에 맞는 패키지를 생성하여 파일을 체계적으로 관리할 준비를 합니다.
- **파일 분리**: `ToDoApp.kt`에 혼재된 `MainScreen`, `AddScreen` 등을 각 기능에 맞는 파일로 분리합니다. (`screen/MainScreen.kt`, `screen/AddScreen.kt`)
- **네이밍 컨벤션 적용**: `ToDoApp` -> `AppContent`, `Screen` -> `Route` 등 새로운 명명 규칙을 적용하고 `sealed interface Route`를 정의하여 화면 경로를 관리합니다.

---

## Part 1: DataStore를 이용한 설정 기능 구현

### 2단계: DataStore 구현 (설정 데이터)
- **`SortOrder` Enum**: 정렬 옵션(`TIME`, `TASK_NAME`)을 정의하는 열거형 클래스를 `data/settings` 패키지에 생성합니다.
- **`SettingsRepository.kt`**: `preferencesDataStore`를 사용하여 정렬 순서 값을 저장하고 `Flow<SortOrder>`로 노출하는 리포지토리를 `data/settings` 패키지에 구현합니다.

### 3단계: ViewModel 및 UI 수정 (설정 연동)
- **`TodoViewModel.kt` 수정**:
    - `SettingsRepository`와 연동하여 `sortOrder: StateFlow<SortOrder>`를 UI에 노출합니다.
    - `updateSortOrder` 함수를 구현하여 사용자가 선택한 정렬 순서를 DataStore에 저장하도록 합니다.
    - **(중요)** 이 단계에서 `todoItems`는 Room과 연동하지 않고, 기존의 인메모리 `mutableStateListOf()`를 임시로 유지하거나 빈 리스트로 초기화합니다.
- **`PreferenceScreen.kt` 생성**:
    - `screen` 패키지 내에 설정 화면을 새로 생성합니다.
    - `TodoViewModel`을 사용하여 현재 정렬 상태(`sortOrder`)를 표시하고, `RadioButton`을 통해 정렬 순서를 변경할 수 있는 UI를 구현합니다.
- **`MainScreen.kt` 수정**:
    - 상단 앱 바에 설정 아이콘을 추가하고, 클릭 시 `PreferenceScreen`으로 이동하도록 탐색 로직을 추가합니다.

### 4단계: DataStore 기능 테스트
- 앱을 실행하여 `PreferenceScreen`에서 정렬 순서를 변경합니다.
- 앱을 완전히 종료했다가 다시 시작했을 때, 이전에 선택한 정렬 순서가 `PreferenceScreen`에 올바르게 유지되는지 확인합니다.

---

## Part 2: Room을 이용한 ToDo 목록 구현

### 5단계: Room 데이터베이스 구현
- **`TodoItem.kt` (Entity)**: `data/local` 패키지에 `@Entity` 어노테이션을 사용하여 `todo_items` 테이블과 매핑될 데이터 클래스를 정의합니다.
- **`TodoDao.kt` (DAO)**: `data/local` 패키지에 `@Dao` 인터페이스를 정의하고, `Flow<List<TodoItem>>`을 반환하는 정렬 쿼리 메서드와 CRUD 메서드를 추가합니다.
- **`AppDatabase.kt` (Database)**: `data/local` 패키지에 Room 데이터베이스 클래스를 싱글톤 패턴으로 구현합니다.

### 6단계: ViewModel 계층 확장 (Room 연동)
- **`TodoViewModel.kt` 확장**:
    - `AppDatabase`를 통해 `TodoDao` 인스턴스를 초기화합니다.
    - `flatMapLatest` 연산자를 사용하여 `sortOrderFlow`의 변경에 따라 `TodoDao`의 정렬 쿼리를 동적으로 전환하고, 그 결과를 `todoItems: StateFlow<List<TodoItem>>`로 UI에 노출합니다. 이로써 기존의 인메모리 리스트를 대체합니다.
    - `addTodo`, `deleteTodo` 등 CRUD 함수가 Room 데이터베이스와 상호작용하도록 로직을 수정합니다.

### 7단계: UI 계층 수정 (Room 연동)
- **`MainScreen.kt` 수정**: `todoViewModel.todoItems`에서 수집한 `List<TodoItem>`을 `LazyColumn`에 표시하도록 수정합니다.
- **`AddScreen.kt` 수정**: 저장 버튼 클릭 시 `todoViewModel.addTodo(task)`를 호출하여 새로운 할 일을 데이터베이스에 추가하도록 수정합니다.

### 8단계: 통합 테스트
- **기능 테스트**:
    1. 할 일을 추가하고, 목록이 메인 화면에 나타나는지 확인합니다.
    2. 앱을 재시작해도 이전에 추가한 할 일 목록이 그대로 유지되는지 확인합니다. (Room 영속성 테스트)
    3. 설정 화면에서 정렬 순서를 '이름 순'으로 변경하고 메인 화면으로 돌아왔을 때, 목록이 이름 순으로 정렬되는지 확인합니다. (DataStore와 Room 연동 테스트)
- **UI/UX 최종 검토**: `plan_todo_triple_revised.md`의 모든 UI 요구사항(예: 목록이 비어있을 때 안내 문구 표시 등)이 충족되었는지 최종 확인합니다.