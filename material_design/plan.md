# Jetpack Compose와 Material Design 3 마스터하기

이 문서는 Jetpack Compose 환경에서 Material Design 3(MD3)를 활용하여 아름답고 기능적인 UI를 구축하는 과정을 담은 실습 가이드입니다. 우리는 단순히 앱을 만드는 것을 넘어, **재사용 가능한 컴포넌트**를 설계하고, **체계적인 Preview**를 작성하며, **통합된 UI 시스템**을 구축하는 모범 사례를 단계별로 학습합니다.

## 무엇을 배울 수 있나요?

이 프로젝트는 다음과 같은 핵심 역량을 기르는 것을 목표로 합니다.

1.  **Material 3 컴포넌트 활용:** `TopAppBar`, `NavigationBar`, `FloatingActionButton`, `Card` 등 MD3에서 제공하는 핵심 컴포넌트들의 사용법을 익힙니다.
2.  **UI 모듈화 및 재사용:** 화면의 각 부분을 독립적인 컴포저블(`AppTopBar`, `InfoCard` 등)로 분리하여 재사용성을 높이고, 유지보수가 용이한 코드 구조를 설계하는 방법을 배웁니다.
3.  **Preview의 힘: TDD 방식의 UI 개발:** `@Preview`는 단순한 미리보기를 넘어, UI 개발의 핵심 도구입니다. 우리는 각 컴포넌트별 기본 Preview부터, 여러 컴포넌트가 결합된 복잡한 상호작용을 테스트하는 통합 Preview까지 작성합니다. 이 과정은 마치 테스트 주도 개발(TDD)처럼, UI를 작은 단위로 검증하며 점진적으로 완성해나가는 효율적인 개발 방식을 체득하게 합니다.
4.  **상태 관리 및 상호작용:** `remember`, `mutableStateOf` 등을 사용한 상태 관리 기법과, `Drawer`의 열림/닫힘, `BottomNav`의 탭 선택과 같은 실제 사용자 상호작용을 구현하는 방법을 학습합니다.

## Preview 코드를 통한 학습 가이드

이 프로젝트의 `ui` 및 `previews` 패키지 내에 있는 다양한 `@Preview` 함수들은 그 자체로 훌륭한 학습 자료입니다.

*   **단일 컴포넌트 Preview (`InfoCardPreview`, `AppFabPreview` 등):** 각 컴포넌트가 어떤 인자를 받으며, 어떻게 렌더링되는지 가장 작은 단위에서 분석할 수 있습니다.
*   **통합 Preview (`PreviewWithCards`, `PreviewWithDrawerAndFab` 등):** 여러 컴포넌트가 어떻게 조립되어 하나의 완성된 화면을 구성하는지, 그리고 그 과정에서 상태가 어떻게 연결되고 상호작용이 일어나는지에 대한 실제적인 예시를 보여줍니다.

**학생들은 이 Preview 코드들을 직접 수정하고, 다양한 상태와 데이터를 테스트해보며 MD3 컴포넌트 활용법과 Compose의 선언형 UI 패러다임을 깊이 있게 이해할 수 있습니다.**

---

## 프로젝트 실행 계획

**최종 목표:** UI의 통일성을 확보하고, 점진적으로 기능을 확장할 수 있는 재사용 가능한 컴포넌트 기반 구조를 정립합니다.

---

## 1단계: 기본 구조 정리 및 공통 Scaffold 생성

*   **🎯 목표:** 모든 화면이 공유할 수 있는 공통 레이아웃(`TopBar`, `Content`, `Drawer` 등)의 기본 틀을 `BaseScaffold`로 정의합니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `BaseScaffold.kt` 파일이 프로젝트에 존재하며, 기본 Preview가 추가되었습니다.
*   **✍️ 학습 내용:**
    *   **`Scaffold`**: MD3의 가장 기본적인 화면 구조를 제공하는 컨테이너입니다. `topBar`, `bottomBar`, `floatingActionButton`, `content` 등 주요 UI 영역을 슬롯(slot) 형태로 제공하여, 개발자가 각 영역에 원하는 컴포저블을 쉽게 배치할 수 있도록 돕습니다. `BaseScaffold`는 이 `Scaffold`를 한 번 더 감싸, 우리 앱만의 공통 레이아웃 규칙을 적용하는 기반이 됩니다.

## 2단계: TopBar 및 Drawer 컴포넌트 분리

*   **🎯 목표:** 중복으로 정의될 수 있는 상단 바와 내비게이션 서랍을 각각 `AppTopBar`와 `AppDrawer` 컴포넌트로 분리하여 재사용성을 높입니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `AppTopBar.kt`, `AppDrawer.kt` 파일이 존재하며, 각각의 Preview가 구현되어 있습니다.
*   **✍️ 학습 내용:**
    *   **`TopAppBar` (`MediumTopAppBar`)**: 화면 상단에 위치하여 제목, 내비게이션 아이콘, 액션 버튼 등을 표시합니다. `MediumTopAppBar`는 스크롤 시 크기가 자연스럽게 변하는 동적 기능을 제공하여 풍부한 사용자 경험을 만듭니다. `TopAppBarDefaults.enterAlwaysScrollBehavior()`와 같은 스크롤 동작을 연결하여 사용합니다.
    *   **`ModalNavigationDrawer`**: 화면 밖(주로 왼쪽)에 숨겨져 있다가 사용자의 요청(예: 메뉴 아이콘 클릭) 시 나타나는 내비게이션 메뉴입니다. `drawerContent`에 서랍의 UI(`AppDrawer`)를, 주 콘텐츠 영역에는 나머지 화면(`BaseScaffold`)을 배치하는 방식으로 사용합니다.
    *   **`rememberDrawerState`**: `Drawer`의 열림(`DrawerValue.Open`) 또는 닫힘(`DrawerValue.Closed`) 상태를 기억하고 관리하는 훅(hook)입니다. `coroutineScope`와 함께 사용하여 `drawerState.open()` 또는 `drawerState.close()`를 호출함으로써 서랍을 제어할 수 있습니다.

## 3단계: 카드 레이아웃 컴포넌트화

*   **🎯 목표:** 앱 전반에서 사용될 카드 UI의 스타일을 통일하고, `CardInfo` 데이터와 결합된 재사용 가능한 컴포넌트를 만듭니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `CardInfo.kt` 데이터 클래스와 이를 사용하는 `InfoCard.kt` 컴포저블 및 Preview가 존재합니다.
*   **✍️ 학습 내용:**
    *   **`Card`**: 정보를 담는 컨테이너 UI 컴포넌트로, 그림자(elevation), 테두리, 둥근 모서리 등을 통해 다른 콘텐츠와 시각적으로 구분됩니다. `Card` 내부에 `Column`이나 `Row`를 사용하여 이미지, 텍스트 등 다양한 정보를 체계적으로 배치할 수 있습니다. `InfoCard`는 이 `Card`를 기반으로 앱의 특정 데이터(`CardInfo`)에 맞게 스타일링된 재사용 컴포넌트입니다.

## 4단계: 탭 + 페이지 구조 일원화

*   **🎯 목표:** `TabRow`와 `HorizontalPager`를 결합한 탭 레이아웃을 공통 컴포넌트로 분리합니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `TabsLayout.kt` 파일이 생성되었고, Preview가 포함되어 있습니다.
*   **✍️ 학습 내용:**
    *   **`TabRow`와 `Tab`**: 여러 화면 섹션을 탭 형태로 구성할 때 사용합니다. `TabRow`는 탭들의 컨테이너 역할을 하며, 각 `Tab`은 개별 탭 항목을 나타냅니다. `selectedTabIndex`를 통해 현재 선택된 탭을 제어합니다.
    *   **`HorizontalPager`**: 좌우 스와이프로 화면을 전환할 수 있는 컨테이너입니다. `TabRow`의 탭 선택과 `HorizontalPager`의 페이지 상태를 `rememberPagerState`를 통해 동기화하면, 사용자가 탭을 클릭하거나 화면을 스와이프할 때 일관된 상호작용을 구현할 수 있습니다.

## 5단계: BottomNav, FAB 컴포넌트화

*   **🎯 목표:** 하단 내비게이션 바와 플로팅 액션 버튼의 구현을 분리하여 디자인 일관성을 확보합니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `AppBottomNav.kt`와 `AppFab.kt` 파일이 생성되었고, 각각의 Preview가 포함되어 있습니다.
*   **✍️ 학습 내용:**
    *   **`NavigationBar`와 `NavigationBarItem`**: 화면 하단에 위치하여 앱의 최상위 목적지(destination) 간 빠른 이동을 돕습니다. `NavigationBar`는 컨테이너이며, 그 안에 3~5개의 `NavigationBarItem`을 배치하는 것이 일반적입니다. 각 `Item`은 `icon`, `label`, `selected` 상태를 가집니다.
    *   **`FloatingActionButton` (`FAB`)**: 화면의 주된 긍정적 액션(예: 새로 만들기, 추가)을 나타내는 원형 버튼입니다. 보통 `Scaffold`의 `floatingActionButton` 슬롯에 배치되며, 화면 콘텐츠 위에 떠 있는 것처럼 보입니다. `ExtendedFloatingActionButton`은 아이콘과 텍스트를 함께 표시하여 더 명확한 액션을 안내할 수 있습니다.

## 6단계: Preview 통합 및 단계별 UI 확장 구조화

*   **🎯 목표:** 모든 공통 컴포넌트를 조합하여, 앱의 기능이 단계별로 확장되는 과정을 보여주는 통합 Preview들을 구성합니다.
*   **✅ 상태:** **완료**
*   **현재 결과물:** `previews/CombinedPreviews.kt` 파일에 `PreviewWithCards`, `PreviewWithTabs`, `PreviewWithDrawerAndFab`가 모두 구현되어 있습니다.
*   **✍️ 학습 내용:**
    *   **`@Preview`의 활용**: 단순한 미리보기를 넘어, UI 개발의 생산성을 극대화하는 핵심 도구입니다.
        *   **단위 테스트**: 각 컴포넌트를 독립적으로 렌더링하여 시각적으로 테스트합니다.
        *   **통합 테스트**: 여러 컴포넌트를 조합하여 실제 화면과 유사한 복잡한 레이아웃 및 상호작용을 검증합니다. `CombinedPreviews.kt`처럼 단계별로 UI가 어떻게 확장되는지 한눈에 파악하고 테스트할 수 있어, TDD(테스트 주도 개발) 방식의 UI 개발을 가능하게 합니다.

## 추가 기능: 상단 바 동적 스크롤

*   **🎯 목표:** 콘텐츠 스크롤 시 상단 앱 바가 자연스럽게 반응(축소/확장)하도록 하여 사용자 경험을 향상시킵니다.
*   **✅ 상태:** **완료**
*   **수정 내역:**
    1.  **`BaseScaffold.kt`:** `Scaffold`에 `nestedScroll`을 적용할 수 있도록 `modifier` 파라미터를 추가했습니다.
    2.  **`MainActivity.kt`:** `MainScreen`에 `enterAlwaysScrollBehavior`를 생성하고, 이를 `BaseScaffold`와 `AppTopBar`에 연결하여 동적 스크롤 효과를 구현했습니다.

---

## 7단계: 디자인 시스템 정비 및 적용

*   **✅ 상태:** **완료**
*   **✍️ 학습 내용:**
    *   **`MaterialTheme`**: 앱 전체의 디자인 시스템(색상, 타이포그래피, 도형)을 정의하고 하위 컴포저블에 일관되게 적용하는 역할을 합니다.
    *   **`colorScheme`**: `primary`, `surface`, `onPrimary` 등 역할 기반의 색상 집합입니다. 다크/라이트 모드 전환을 쉽게 지원하며, "버튼 색상"과 같이 의미론적인 접근을 통해 디자인 일관성을 유지합니다.
    *   **`typography`**: `titleLarge`, `bodyMedium` 등 텍스트의 위계와 스타일을 정의한 집합입니다. `Text(style = MaterialTheme.typography.titleLarge)`와 같이 사용하여 앱 전체의 텍스트 스타일을 통일합니다.
*   **수정 내역:**
    1.  **디자인 시스템 정의 (`theme` 패키지):**
        *   **`Color.kt`:** MD3의 역할 기반 색상(`surfaceContainerHigh` 등)으로 `LightColors`와 `DarkColors`를 상세히 정의했습니다.
        *   **`Type.kt`:** `headlineSmall`, `titleMedium` 등 역할 기반 `AppTypography`를 정의했습니다.
        *   **`Spacing.kt`:** `AppSpacing` 객체를 새로 만들어 `4.dp` 배수의 간격 규칙을 중앙에서 관리하도록 했습니다.
        *   **`Theme.kt`:** 앱의 메인 테마가 새로 정의된 `Color`와 `Typography`를 사용하도록 업데이트했습니다.
    2.  **컴포넌트 적용:**
        *   **`InfoCard`, `AppTopBar`, `AppDrawer`, `MainActivity` 등:** 하드코딩되었던 색상, 텍스트 스타일, 간격 값들을 `MaterialTheme`과 `AppSpacing`을 사용하도록 모두 교체하여 디자인 시스템을 앱 전체에 적용했습니다.

---

## 최종 단계: MainActivity.kt 구현 상세 계획

*   **🎯 목표:** 현재 "Hello, Android!"만 표시하는 `MainActivity.kt`를, 지금까지 개발한 모든 모듈화된 컴포넌트를 조립하여 실제 동작하는 완성된 앱 화면으로 구현합니다.
*   **✅ 상태:** **진행 필요**
*   **✍️ 학습 내용:**
    *   **컴포넌트 조립 (Composition)**: 지금까지 만든 `AppTopBar`, `AppDrawer`, `TabsLayout` 등 모듈화된 컴포넌트들을 `MainActivity`에서 `Scaffold`와 `ModalNavigationDrawer`의 각 슬롯에 배치하여 하나의 완성된 화면으로 조립합니다. 이는 선언형 UI의 핵심 원칙으로, UI 구조를 코드에서 직관적으로 파악할 수 있게 합니다.
    *   **상태 호이스팅 (State Hoisting)**: `rememberDrawerState`, `rememberPagerState`와 같이 하위 컴포넌트에서 사용될 상태를 상위 컴포저블(`MainActivity`)에서 선언하고, 콜백 람다(`onMenuClick`)와 함께 아래로 내려보내는 디자인 패턴입니다. 이를 통해 상태의 흐름을 단방향으로 관리하여 예측 가능하고 테스트하기 쉬운 코드를 작성할 수 있습니다.

### 세부 수정 방식

1.  **기존 코드 제거:** `MainActivity.kt`의 `Greeting` 컴포저블과 그 Preview를 모두 삭제합니다.
2.  **상태 변수 선언:** 화면의 상태를 관리하기 위한 변수들을 `setContent` 블록 최상단에 선언합니다.
    *   `drawerState`: `rememberDrawerState`로 서랍의 열림/닫힘 상태를 관리합니다.
    *   `coroutineScope`: 서랍을 비동기적으로 열고 닫기 위해 `rememberCoroutineScope`를 사용합니다.
    *   `selectedIndex`: `remember { mutableIntStateOf(0) }`로 `AppBottomNav`의 선택된 아이템 상태를 관리합니다.
3.  **최상위 레이아웃 설정:** `ModalNavigationDrawer`를 최상위 컴포저블로 배치합니다.
    *   `drawerContent`에는 `AppDrawer`를 전달하고, `drawerState`를 연결합니다.
4.  **메인 화면 구성:** `ModalNavigationDrawer`의 주 콘텐츠 영역에 `BaseScaffold`를 배치하고 아래와 같이 각 슬롯을 채웁니다.
    *   `topBar`: `AppTopBar`를 배치하고, 메뉴 아이콘(`onMenuClick`)이 `drawerState`를 열도록 `scope.launch { drawerState.open() }`를 연결합니다.
    *   `bottomBar`: `AppBottomNav`를 배치하고, `selectedIndex` 상태와 `onSelect` 콜백을 연결합니다.
    *   `floatingActionButton`: `AppFab`을 배치합니다.
    *   `content`: `TabsLayout`을 배치하여 메인 콘텐츠 영역을 탭 기반 UI로 구성합니다. 각 탭의 페이지에는 `LazyColumn`을 사용한 카드 목록이나 다른 적절한 콘텐츠를 표시합니다.

### 최종 앱의 모습 (The Look of the Final App)

*   **초기 화면:** "Material Design App"이라는 제목의 상단 바, 세 개의 탭("Music", "Movies", "Books"), 그리고 하단 내비게이션 바가 표시됩니다. 첫 번째 탭에는 스크롤 가능한 카드 목록이 나타납니다.
*   **상호작용:**
    *   상단 바의 **메뉴 아이콘**을 클릭하면 왼쪽에서 **내비게이션 서랍**(`AppDrawer`)이 부드럽게 나타납니다.
    *   **탭 제목**을 클릭하거나 화면을 **좌우로 스와이프**하면 페이지 콘텐츠가 전환됩니다.
    *   **하단 내비게이션 아이콘**을 클릭하면 아이콘이 활성화 상태로 변경됩니다.
    *   화면 우측 하단의 **플로팅 액션 버튼**(`FAB`)은 클릭 가능한 상태로 표시됩니다.

이 모든 요소가 결합되어, 보기 좋고 상호작용이 자연스러운 하나의 완성된 애플리케이션 화면을 형성하게 됩니다.

---

## 🧠 최종 폴더 구조

```
com.example.material_design
│
├── ui/
│   ├── previews/
│   │   └── CombinedPreviews.kt  // 통합 프리뷰
│   │
│   ├── AppBottomNav.kt          // 하단 네비게이션 바
│   ├── AppDrawer.kt             // 내비게이션 서랍
│   ├── AppFab.kt                // 플로팅 액션 버튼
│   ├── AppTopBar.kt             // 상단 앱 바
│   ├── BaseScaffold.kt          // 기본 레이아웃 구조
│   ├── ImageHeader.kt           // 이미지 배경 헤더 (추가 컴포넌트)
│   ├── InfoCard.kt              // 카드 UI (데이터 클래스 포함)
│   └── TabsLayout.kt            // 탭 및 페이저 레이아웃
│
├── MainActivity.kt              // 메인 액티비티 (모든 컴포넌트 조립)
│
└── ui/theme/
    ├── Color.kt                 // 앱 색상 정의
    ├── Theme.kt                 // 앱 전체 테마
    └── Type.kt                  // 타이포그래피 정의
```

---

## **사용된 주요 Compose UI / Android 요소 정리**

| 카테고리 | 주요 요소 | 설명 |
| :--- | :--- | :--- |
| **레이아웃** | `Scaffold`, `ModalNavigationDrawer`, `Surface`, `Box`, `Column`, `Row`, `Spacer` | 앱의 기본 구조, 서랍, 화면 표면 및 기본 배치를 구성합니다. |
| **스크롤 & 리스트** | `LazyColumn`, `Modifier.nestedScroll` | 스크롤 가능한 목록을 효율적으로 표시하고, 중첩 스크롤 동작을 연결합니다. |
| **상단바** | `MediumTopAppBar`, `TopAppBarDefaults`, `TopAppBarScrollBehavior` | 스크롤 시 동적으로 크기가 변하는 상단 앱 바를 구현합니다. |
| **하단 네비게이션** | `NavigationBar`, `NavigationBarItem` | 앱의 주요 섹션 간 이동을 위한 하단 탭 바를 구성합니다. |
| **서랍 (Drawer)** | `ModalDrawerSheet`, `NavigationDrawerItem`, `rememberDrawerState` | 화면 왼쪽에서 나타나는 메뉴(서랍)와 그 상태를 관리합니다. |
| **탭 & 페이저** | `TabRow`, `Tab`, `HorizontalPager`, `rememberPagerState` | 좌우로 스와이프 가능한 탭 화면을 구성합니다. |
| **컴포넌트** | `Card`, `ExtendedFloatingActionButton`, `Icon`, `IconButton`, `Text` | 카드, 버튼, 아이콘, 텍스트 등 화면을 구성하는 기본 요소들입니다. |
| **상태 관리** | `remember`, `mutableStateOf`, `rememberCoroutineScope` | 컴포저블의 상태를 기억하고, 코루틴 스코프를 통해 상태를 비동기적으로 변경합니다. |
| **테마 & 스타일** | `MaterialTheme`, `colorScheme`, `typography` | 앱의 전체적인 색상, 글꼴 등 디자인 시스템을 일관되게 적용합니다. |
| **프리뷰** | `@Preview` | 다양한 설정(이름, 배경, 시스템 UI)을 통해 UI 컴포넌트를 시각적으로 테스트합니다. |

---