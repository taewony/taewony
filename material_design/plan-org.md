좋아요. 아래는 업로드된 `MainActivity.kt`를 기반으로, **“UI 통일성을 유지한 채 점진적으로 추가 가능한 구조로 리팩토링”** 하기 위한 **실행 가능한 구체적 Plan 문서**입니다.

단순 제안이 아니라, **개발 단계별로 실제로 코드 구조를 재구성하는 작업 지침(Task 단위)** 으로 정리했습니다.
(즉, 바로 Android Studio에서 구현 가능한 형태로 설계되었습니다.)

---

## 🧭 프로젝트 리팩토링 실행 계획서

**대상 파일:** `MainActivity.kt`
**목표:**

* 각 Preview 화면(`MainScreen`, `CollapsedTopBarScreen`, `MainScreenWithTabs`, `MainScreenWithTabsAndDrawer`) 간의 **디자인·구조 통일성 확보**
* 점진적으로 확장 가능한 UI 구조 정립
* 재사용 가능한 컴포넌트 기반 구조 확립

---

## ⚙️ 1단계 — 기본 구조 정리 및 공통 Scaffold 생성

### 🎯 목표

모든 화면이 같은 기본 틀(TopBar / Content / BottomNav / FAB / Drawer)을 공유하도록 `BaseScaffold` 컴포저블을 만든다.

### ✅ Tasks

| 번호  | 작업 내용                         | 세부 수정 내용                                                                                                                                                                                                                  |
| --- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.1 | `BaseScaffold.kt` 새 파일 생성     | - `@Composable fun BaseScaffold(...)` 정의<br>- 파라미터: `title: String`, `onMenuClick: () -> Unit`, `content: @Composable () -> Unit`, `bottomBar: @Composable (() -> Unit)? = null`, `fab: @Composable (() -> Unit)? = null` |
| 1.2 | `MediumTopAppBar` 공통화         | - 기존 `MainScreen`, `CollapsedTopBarScreen` 등에서 상단바 코드 제거<br>- `BaseScaffold` 내부에서 공통 AppBar 생성<br>- AppBar 색상: `MaterialTheme.colorScheme.primaryContainer` 통일                                                            |
| 1.3 | `Scaffold` 내부 padding, 색상 공통화 | - `Modifier.fillMaxSize().padding(innerPadding)` 적용<br>- `Surface(color = MaterialTheme.colorScheme.background)` 통일                                                                                                       |

---

## 🧩 2단계 — TopBar 및 Drawer 컴포넌트 분리

### 🎯 목표

중복된 상단바와 Drawer 정의를 컴포넌트로 분리해 재사용.

### ✅ Tasks

| 번호  | 작업 내용                | 세부 수정 내용                                                                                                                                                                                                              |
| --- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1 | `AppTopBar.kt` 파일 생성 | - `@Composable fun AppTopBar(title: String, onMenuClick: (() -> Unit)? = null)` 정의<br>- 내부에 `MediumTopAppBar` 사용<br>- 로고와 텍스트 구성(현재 CollapsedTopBarScreen의 Row 구조 참고)<br>- 텍스트 컬러, 높이 통일                              |
| 2.2 | `AppDrawer.kt` 파일 생성 | - 기존 `ModalNavigationDrawer` 블록을 추출<br>- `drawerItems`를 `List<Pair<String, ImageVector>>`로 전달받게 설계<br>- Header 스타일(`background = Color.Red`, `Text` = White) 그대로 유지<br>- `NavigationDrawerItem` spacing 및 padding 일관화 |
| 2.3 | `DrawerState` 관리 통일  | - `BaseScaffold`에서 `drawerState`를 remember 하여 AppDrawer에 전달                                                                                                                                                           |

---

## 📱 3단계 — 카드 레이아웃 컴포넌트화

### 🎯 목표

모든 카드 UI(`CardItem`, `CardList`)를 하나의 스타일로 통일.

### ✅ Tasks

| 번호  | 작업 내용               | 세부 수정 내용                                                                                                           |
| --- | ------------------- | ------------------------------------------------------------------------------------------------------------------ |
| 3.1 | `CardInfo` 데이터 유지   | - `CardInfo(title, content, color)` 그대로 사용                                                                         |
| 3.2 | `CardItem.kt` 파일 생성 | - `@Composable fun CardItem(info: CardInfo)`로 변경<br>- `title`, `content` 공통 타이포그래피 적용: `titleMedium`, `bodyMedium` |
| 3.3 | 색상 일관성              | - `containerColor = MaterialTheme.colorScheme.surfaceContainerHigh` 기본값 사용<br>- 필요 시 강조색만 오버라이드                    |
| 3.4 | `CardList` 재사용화     | - `CardList(cards: List<CardInfo>)`를 단일 파일로 분리<br>- 모든 Preview에서 이 컴포넌트를 사용하도록 수정                                  |

---

## 🧭 4단계 — 탭 + 페이지 구조 일원화

### 🎯 목표

`MainScreenWithTabs`와 `MainScreenWithTabsAndDrawer`의 중복 구조 제거.

### ✅ Tasks

| 번호  | 작업 내용                                                   | 세부 수정 내용                                                                                                                                                     |
| --- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 4.1 | `TabsLayout.kt` 파일 생성                                   | - `@Composable fun TabsLayout(tabs: List<String>, pagerState: PagerState, onTabSelected: suspend (Int) -> Unit)` 정의<br>- 내부에 `TabRow` 및 `HorizontalPager` 배치 |
| 4.2 | `MainScreenWithTabs` 및 `MainScreenWithTabsAndDrawer` 수정 | - 둘 다 `TabsLayout()` 호출 구조로 변경<br>- 콘텐츠 차이는 `cards` 데이터만 다르게 주입                                                                                              |
| 4.3 | Tab 색상 통일                                               | - `selectedContentColor = MaterialTheme.colorScheme.primary`, `unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant`                          |

---

## 🧭 5단계 — BottomNav, FAB 컴포넌트화

### 🎯 목표

NavigationBar와 FAB 구성 중복 제거 및 디자인 일관성 확보.

### ✅ Tasks

| 번호  | 작업 내용                   | 세부 수정 내용                                                                                                                                                    |
| --- | ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 5.1 | `AppBottomNav.kt` 파일 생성 | - `@Composable fun AppBottomNav(items: List<Pair<String, ImageVector>>, selectedIndex: Int, onSelect: (Int) -> Unit)` 정의<br>- 내부 `NavigationBarItem` 스타일 통일 |
| 5.2 | FAB 분리                  | - `@Composable fun AppFab(icon: ImageVector, text: String, onClick: () -> Unit)` 정의<br>- 기존 Extended FAB 재활용                                                |

---

## 🧱 6단계 — Preview 통합 및 단계별 UI 확장 구조화

### 🎯 목표

Preview를 “단계별 성장 버전”으로 유지하되, 모두 공통 컴포넌트를 기반으로 함.

### ✅ Tasks

| 번호  | 작업 내용                     | 세부 수정 내용                                         |
| --- | ------------------------- | ------------------------------------------------ |
| 6.1 | `PreviewBaseScaffold`     | - TopBar + 단순 텍스트 (“Hello”)                      |
| 6.2 | `PreviewWithCards`        | - BaseScaffold + CardList                        |
| 6.3 | `PreviewWithTabs`         | - BaseScaffold + TabsLayout + BottomNav          |
| 6.4 | `PreviewWithDrawerAndFab` | - BaseScaffold + Drawer + Tabs + BottomNav + FAB |

---

## 🎨 7단계 — 테마 정비

### 🎯 목표

색상·폰트·간격 시스템 통일로 전체적인 시각 일관성 확보.

### ✅ Tasks

| 번호  | 작업 내용               | 세부 수정 내용                                                                          |
| --- | ------------------- | --------------------------------------------------------------------------------- |
| 7.1 | `theme/Color.kt` 보강 | - `surfaceContainerHigh`, `surfaceVariant`, `onPrimaryContainer` 등 일관적 사용         |
| 7.2 | `typography` 정리     | - `titleSmall` → `titleMedium` 통일<br>- `headlineMedium` → `headlineSmall`로 정렬 균형화 |
| 7.3 | Padding 규칙          | - 공통 spacing 값(8.dp, 12.dp, 16.dp) 지정 후 각 Card, Drawer, Tab에 적용                   |

---

## 🧠 최종 구조 개요

```
com.example.app_12_material_design
│
├── ui/
│   ├── BaseScaffold.kt
│   ├── AppTopBar.kt
│   ├── AppDrawer.kt
│   ├── CardItem.kt
│   ├── TabsLayout.kt
│   ├── AppBottomNav.kt
│   └── AppFab.kt
│
├── MainActivity.kt   ← 각 Preview에서 위 컴포넌트 조합
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

---

## 🧩 기대 효과

| 항목      | 개선 전                     | 개선 후                     |
| ------- | ------------------------ | ------------------------ |
| 코드 중복   | 4개 Preview에 동일 UI 반복     | 1개 공통 컴포넌트 구조            |
| 스타일 통일성 | 색상·폰트·정렬 불균형             | MaterialTheme 기반 완전 통일   |
| 확장성     | Preview마다 개별 작성          | BaseScaffold 조합만으로 확장 가능 |
| 유지보수성   | 코드 수정 시 모든 Preview 변경 필요 | 단일 컴포넌트 수정으로 전체 반영       |

---

원하신다면, 위 **Plan을 바로 코드로 구현한 리팩토링 버전**을 만들어드릴 수 있습니다.
이때는 `BaseScaffold` 중심으로 컴포넌트화된 Compose 코드 전체를 제공합니다.

👉 지금 바로 코드 구현 단계로 진행할까요? (예: `BaseScaffold.kt`부터 생성)
