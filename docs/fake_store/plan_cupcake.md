# Navigation3 기준 Cupcake 앱 — 단계별 학습자료

> 대상: Android Studio가 만든 default module 템플릿(기본 app 모듈) 상태에서 시작하여, Jetpack Compose + Navigation3로 'Cupcake(컵케이크) 앱'을 모듈 단위로 완성하는 것을 목표로 합니다.

이 문서는 학생들이 따라 하면서 실습할 수 있도록 단계별(설정 → 구조 → UI → ViewModel → 내비게이션 → 테스트/배포)로 작성되어 있습니다. 각 단계 끝에는 확인 항목과 실습 과제가 포함되어 있습니다.

---

## 1. 사전 준비 및 프로젝트 생성

1. Android Studio 최신 버전(권장)을 설치합니다.
2. `New Project` → `Empty Compose Activity` 템플릿으로 새 프로젝트를 생성하세요.

   * 최소 SDK: `API 21+` 권장
   * Kotlin: 사용
   * Compose 옵션 활성화

**실습 확인:** 프로젝트가 열리고 `MainActivity.kt`, `AndroidManifest.xml`, `build.gradle.kts`(app 및 프로젝트 레벨)이 있는지 확인하세요.

---

## 2. 레포지토리 의존성 관리 (libs.versions.toml 사용)

본 실습에서는 버전 관리를 `libs.versions.toml`로 중앙화합니다. 제공된 예시 파일을 참고하세요(이 파일은 이미 업로드된 `libs.versions.toml`에 주요 버전과 라이브러리 키가 정의되어 있습니다).

> 핵심 라이브러리(예시):
>
> * `androidx.navigation3:navigation3-runtime` 및 `navigation3-ui` (버전: navigation3 = "1.0.0-alpha08")
> * Compose BOM 및 Compose 관련 모듈
> * Lifecycle, Activity Compose 등

### 프로젝트 루트 설정 (settings.gradle.kts)

* `dependencyResolutionManagement` 및 `versionCatalogs`가 설정되어 있는지 확인하세요. 예:

```kotlin
dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("gradle/libs.versions.toml"))
    }
  }
}
```

### app 모듈 `build.gradle.kts`(주요 포인트)

* `plugins { id("com.android.application") ... }`
* `kotlin-kapt` 또는 `ksp` 설정(필요 시)
* Compose 활성화: `buildFeatures { compose = true }`
* Compose compiler, bom 사용
* 라이브러리는 `libs` 카탈로그로 참조: `implementation(libs.findLibrary("androidx-navigation3-runtime").get())`처럼.

> **실습 과제:** 제공된 `libs.versions.toml` 값을 사용해 `app/build.gradle.kts`에 필요한 의존성을 추가하세요.

---

## 3. 모듈/패키지 구조 설계

권장 패키지 구조 (단일 app 모듈 기준):

```
app/src/main/java/com/example/cupcake/
  - ui/
    - start/
    - flavor/
    - pickup/
    - summary/
    - components/ (공통 컴포저블)
  - data/
    - model/
    - repository/ (필요 시)
  - ui/theme/
  - viewmodel/
  - navigation/
  - MainActivity.kt
```

각 화면은 별도 파일로 분리하여 관리합니다.

**확인 항목:** 각 폴더에 `Screen` 컴포저블과 필요한 리소스 파일(문자열, 이미지 등)을 준비하세요.

---

## 4. Navigation3 기초 — 왜 사용할까?

* Nav3는 Compose 환경에서 상태 기반으로 백스택을 관리하는 비교적 간단한 내비게이션 라이브러리입니다.
* 기존 Navigation Compose(graph 기반)와 비교했을 때 코드가 더 선언적이며, 화면간 데이터 전달/백 스택 관리를 자체 상태로 처리합니다.

(앱 설계 문서에 있는 Nav3 사용 예시는 이미 업로드된 디자인 문서 `cupcake_디자인.md`에서 확인할 수 있습니다.)

---

## 5. 내비게이션 구현 단계 (Nav3)

### 5.1. 화면(목록) 정의

`CupcakeScreen` 같은 봉인(sealed) 클래스를 만들어 앱의 모든 화면을 열거합니다. 예:

```kotlin
sealed interface CupcakeScreen {
  object Start : CupcakeScreen
  object Flavor : CupcakeScreen
  object Pickup : CupcakeScreen
  object Summary : CupcakeScreen
}
```

### 5.2. 백스택 관리

* `mutableStateListOf<CupcakeScreen>()` 를 `remember` 하여 백스택을 유지합니다.
* `NavDisplay`라는 컴포저블에 백스택을 전달해 현재 화면을 렌더링합니다.

### 5.3. Entry provider 구현

* `entryProvider` 람다로 각 `CupcakeScreen`를 해당 컴포저블로 매핑합니다.
* 예: `if (entry == CupcakeScreen.Flavor) FlavorScreen(onNext = { backStack += CupcakeScreen.Pickup })`

### 5.4. 뒤로가기 처리

* `BackHandler` 또는 `LocalOnBackPressedDispatcherOwner`를 통해 백스택에서 `removeLastOrNull()` 호출로 처리합니다.

---

## 6. 화면별 UI 설계 (Compose)

각 화면은 다음 책임을 갖습니다.

1. StartScreen: 환영 및 주문 시작 버튼
2. FlavorScreen: 맛 선택(리스트 + 선택 상태)
3. PickupScreen: 픽업 날짜 선택(달력/DatePicker)
4. SummaryScreen: 주문 요약 및 Submit 버튼

### Compose 구성요소 예시

* 재사용 가능한 컴포넌트: `FlavorItem`, `Header`, `PrimaryButton` 등
* State 전달: 상위(호스트)에서 `viewModel`을 주입하여 상태를 공유합니다.

---

## 7. ViewModel 및 상태 관리 (MVVM)

* `OrderViewModel` 하나를 만들어 주문 상태(수량, flavor, pickupDate 등)를 보관합니다.
* `MutableState` 또는 `StateFlow` 중 하나를 사용하세요. Compose 바인딩은 `by viewModel.state.collectAsState()` 또는 `by remember { mutableStateOf(...) }` 형태로 연결합니다.
* ViewModel에서 내비게이션을 직접 트리거하지 말고, UI 레벨에서 백스택 조작을 호출하게 합니다(테스트와 재사용성 향상).

---

## 8. 의존성 선언—예시 코드 조각

> 주의: 아래는 `libs.versions.toml`에서 정의한 버전 카탈로그를 사용하는 예시입니다. 실제 `libs` ID는 프로젝트의 `versionCatalog` 이름과 일치시키세요.

```kotlin
// app/build.gradle.kts (dependencies 섹션)
dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("androidx-activity-compose").get())
    implementation(libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())

    // Navigation3
    implementation(libs.findLibrary("androidx-navigation3-runtime").get())
    implementation(libs.findLibrary("androidx-navigation3-ui").get())

    // Compose material3
    implementation(libs.findLibrary("androidx-compose-material3").get())

    // Room 등 다른 의존성은 필요 시 추가
}
```

---

## 9. 코드 샘플 (핵심 부분)

* `CupcakeApp.kt` - 백스택 생성과 `NavDisplay` 사용
* `StartScreen.kt` - 주문 시작 버튼(백스택에 Flavor 추가)
* `OrderViewModel.kt` - 주문 상태와 helper 함수

(실전 코드 블록은 본 문서 하단의 `예제 코드` 섹션에 정리되어 있습니다.)

---

## 10. 빌드·디버깅 팁

* 먼저 `Clean` → `Rebuild Project`를 해보고 컴파일 오류가 나면 `gradle sync`에서 어떤 버전이 충돌하는지 확인하세요.
* Compose 관련 오류는 종종 `composeCompilerVersion`과 Kotlin 버전 불일치에서 발생합니다. `libs.versions.toml`의 `kotlin` 항목과 일치시키세요.

---

## 11. 테스트 및 QA

* UI 테스트: Compose Testing 라이브러리를 사용하여 버튼 클릭 흐름을 검증하세요.
* 단위 테스트: ViewModel의 상태 전환 로직을 테스트하세요.

---

## 12. 배포 준비

* `minifyEnabled` 설정, 프로가드 규칙 확인
* 릴리즈 빌드 생성 및 서명

---

## 13. 실습 과제(권장 순서)

1. `libs.versions.toml`을 프로젝트에 추가하고 `settings.gradle.kts`에 연결하세요.
2. `app/build.gradle.kts`에 Compose, Navigation3 의존성을 추가하세요.
3. `CupcakeScreen` 봉인 인터페이스를 만들고 각 화면의 빈 컴포저블을 구현하세요.
4. `CupcakeApp`에서 백스택을 만들고 `NavDisplay`를 통해 화면 전환을 확인하세요.
5. `OrderViewModel`을 만들어 화면 간 상태 공유를 구현하세요.
6. UI 테스트 2개, ViewModel 단위 테스트 1개를 작성하세요.

---

## 14. 예제 코드(요약)

> *원문보다 요약된 버전입니다. 전체 예제 코드는 실습에서 복사하여 붙여넣어 사용하세요.*

### CupcakeScreen.kt

```kotlin
sealed interface CupcakeScreen {
  object Start : CupcakeScreen
  object Flavor : CupcakeScreen
  object Pickup : CupcakeScreen
  object Summary : CupcakeScreen
}
```

### CupcakeApp.kt (핵심)

```kotlin
@Composable
fun CupcakeApp() {
  val backStack = remember { mutableStateListOf<CupcakeScreen>(CupcakeScreen.Start) }

  NavDisplay(backStack = backStack) { entry ->
    when(entry) {
      CupcakeScreen.Start -> StartScreen(onStart = { backStack += CupcakeScreen.Flavor })
      CupcakeScreen.Flavor -> FlavorScreen(onNext = { backStack += CupcakeScreen.Pickup }, onBack = { backStack.removeLastOrNull() })
      CupcakeScreen.Pickup -> PickupScreen(onNext = { backStack += CupcakeScreen.Summary }, onBack = { backStack.removeLastOrNull() })
      CupcakeScreen.Summary -> SummaryScreen(onBack = { backStack.removeLastOrNull() })
    }
  }
}
```

### OrderViewModel.kt (간단)

```kotlin
class OrderViewModel : ViewModel() {
  var flavor by mutableStateOf<String?>(null)
  var pickupDate by mutableStateOf("")

  fun setFlavor(f: String) { flavor = f }
  fun setPickupDate(d: String) { pickupDate = d }
}
```

---

## 15. 추가 참고자료

* Nav3 디자인 문서 및 예제(프로젝트의 design 문서를 참고하세요)
* Compose 및 lifecycle 공식 문서

---

### 끝으로

이 문서는 학생들이 `Android Studio`의 기본 모듈에서 출발해 Navigation3 기반으로 컵케이크 앱을 완성할 수 있도록 단계별로 구성했습니다. 필요하면 "완성본 프로젝트 코드(깃 레포)" 또는 "실습용 단계별 커밋 히스토리"를 만들어 드릴게요.

(문서 끝)
