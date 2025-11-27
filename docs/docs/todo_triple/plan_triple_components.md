# 단계별 강의자료 + 상세 의존성 설명 (Kotlin 2.0.21, AGP 8.13.0 기준)

좋습니다 — 이전 답변의 전체 흐름을 유지하면서, **각 라이브러리의 역할과 선택 이유**, 그리고 **`libs.versions.toml` 설정 방법**과 **루트 / 모듈 `build.gradle.kts`에서의 적용 위치와 차이**를 명확히 정리해 드립니다.
학생들이 따라 하며 dependency를 하나씩 추가할 때 혼동하지 않도록 예시 코드와 권장 적용 순서(단계별)를 함께 제공합니다.

---

# 1. 기초 안내 — 왜 `libs.versions.toml`을 쓰는가? 그리고 루트 vs 모듈의 역할

* **`libs.versions.toml`(버전 카탈로그)**

  * 중앙에서 모든 라이브러리 버전·별칭을 관리합니다. 프로젝트 전체에서 동일 버전 사용을 강제하고, 버전 변경이 필요할 때 한곳만 수정하면 됩니다.
  * TOML에 `versions`, `libraries`, `plugins` 섹션을 둡니다.

* **루트 `build.gradle.kts` (프로젝트 레벨)**

  * 빌드 시스템과 플러그인(Gradle 플러그인, 버전 카탈로그 참조 등)을 설정합니다.
  * 일반적으로 `pluginManagement`, `dependencyResolutionManagement`, `plugins { ... apply false }` 등 전역 설정을 둡니다.
  * `libs.versions.toml`은 루트의 `versionCatalogs`로 자동 인식됩니다(Gradle 7.0+).

* **모듈 `build.gradle.kts` (앱 또는 라이브러리 모듈)**

  * 실제 `implementation(...)`, `ksp(...)`, `kapt(...)` 등 의존성을 선언하고, Compose/AGP 관련 설정(`compileSdk`, `composeOptions`, `buildFeatures`)을 둡니다.
  * 모듈에서는 루트에 선언한 alias(`libs.plugins.xxx`)를 `plugins { alias(libs.plugins.xxx) }`로 사용 가능.

---

# 2. 단계별로 추가할 라이브러리와 *자세한 설명*

아래는 각 단계에서 추가할 라이브러리와 "이 라이브러리는 무엇을 하는가"를 설명합니다.

---

## 1단계 — Compose 기본 + Navigation

### 라이브러리

* **AndroidX Compose BOM** (`platform("androidx.compose:compose-bom:...")`)

  * Compose 관련 여러 아티팩트를 동일한 호환 버전으로 묶어 관리해 줍니다. BOM을 사용하면 `implementation("androidx.compose.ui:ui")` 같은 선언에서 버전을 직접 쓰지 않아도 됩니다.
* **`androidx.navigation:navigation-compose`**

  * Compose 기반의 화면 전환(NavHost, NavController, composable)을 제공합니다.

### 왜 필요한가?

* Compose UI 구성요소와 네비게이션을 안정적으로 사용하게 해 줍니다. `NavHost` / `composable`을 통해 화면 간 라우팅을 구현합니다.

---

## 2단계 — Battery 상태 표시

### 라이브러리

* **`androidx.lifecycle:lifecycle-runtime-compose`**

  * Compose에서 Lifecycle-aware 행동을 쉽게 하기 위한 보조 기능을 제공합니다. (`repeatOnLifecycle`, `collectAsStateWithLifecycle` 등)
* (필요 시) 표준 Android framework API: `BroadcastReceiver`, `BatteryManager` — 별도 외부 라이브러리 불필요

### 왜 필요한가?

* 시스템 브로드캐스트를 UI에 안전하게 바인딩하고, lifecycle에 따라 리스너를 등록/해제하는 패턴을 쉽게 적용하기 위함입니다.

---

## 3단계 — MP3 Player

### 라이브러리

* **AndroidX Media3 (ExoPlayer 기반)**

  * `androidx.media3:media3-exoplayer` — 재생 엔진(ExoPlayer 래퍼)
  * `androidx.media3:media3-ui` — 미디어 컨트롤 UI(기본 컨트롤러 뷰 등)

### 왜 필요한가?

* Android 표준 `MediaPlayer`도 가능하지만, Media3(ExoPlayer)는 스트리밍, 포맷 지원, 세밀한 제어에 더 강력합니다. 수업에서는 간단히 로컬 MP3 재생/정지/정지 후 자원 해제 패턴을 실습하게 됩니다.

---

## 4단계 — Gallery 이미지 표시

### 라이브러리

* **Coil (Coil-kt, Compose 통합)**: `io.coil-kt:coil-compose`

  * Compose `AsyncImage`(또는 `Image`와 `rememberImagePainter`)로 URL이나 Uri를 비동기 로드합니다.

### 왜 필요한가?

* 이미지를 효율적으로 캐시(메모리/디스크)하고 비동기 로딩과 placeholder, 에러 핸들링을 제공하여 성능과 사용자 경험을 개선합니다.

---

## 5단계(확장) — 구조 정리 및 Hilt 도입

### 라이브러리

* **Hilt** (`com.google.dagger:hilt-android`, `hilt-compiler`)

  * DI(의존성 주입)를 통해 ViewModel, Repository 등 의존성을 자동 주입합니다.
* **Hilt Navigation Compose** (`androidx.hilt:hilt-navigation-compose`)

  * NavGraph와 Hilt를 연동해 ViewModel 주입을 간단히 합니다.
* **KSP (Kotlin Symbol Processing)**: Hilt 컴파일러를 KSP로 돌리는 설정 권장 (또는 KAPT)

### 왜 필요한가?

* 프로젝트가 커질수록 의존성 주입 패턴이 코드 가독성과 테스트성에 큰 이점을 줍니다. 강의 마지막에 구조화 연습으로 적합합니다.

---

# 3. `libs.versions.toml` — 예시 (단계별로 추가하는 방식)

아래는 **초기 BOM + Navigation**부터 **Hilt**까지 차례로 확장한 `gradle/libs.versions.toml` 예시입니다. (파일 위치: 프로젝트 루트 `gradle/libs.versions.toml` 권장)

```toml
[versions]
# Compose BOM은 날짜/버전 스타일로 관리하거나 명시 버전 사용
composeBom = "2024.10.00"    # 예: Compose BOM 버전 (예시)
navigation = "2.8.3"
lifecycleRuntimeCompose = "2.8.6"
media3 = "1.4.1"
coil = "2.7.0"
hilt = "2.53"
hiltNavigationCompose = "1.2.0"
okhttp = "4.12.0"
retrofit = "2.11.0"

[libraries]
# Compose BOM을 platform으로 참조
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeCompose" }

androidx-media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }
androidx-media3-ui = { group = "androidx.media3", name = "media3-ui", version.ref = "media3" }

coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

[plugins]
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "1.9.0-1.0.13" } # KSP 버전은 Kotlin 버전에 맞게 조정
```

> 주의: `ksp` plugin의 버전은 Kotlin 버전(2.0.21)에 맞는 KSP 릴리즈를 사용해야 합니다. (예시는 형식)

---

# 4. 루트 `build.gradle.kts` (예시)

루트에서는 `versionCatalogs`는 자동 인식되므로 보통 `plugins` 블록에 alias 등록 및 `buildscript`/`dependencyResolutionManagement` 설정을 둡니다.

```kotlin
// root build.gradle.kts
plugins {
    // 필요한 경우 Gradle 플러그인 선언 (예: com.android.application 은 모듈레벨에서)
    // Hilt 플러그인을 루트에서 apply false로 미리 선언해서 모듈에서 alias로 사용 가능하게 함
    alias(libs.plugins.hilt.android) apply false
    // KSP 도 미리 선언
    alias(libs.plugins.ksp) apply false
}

buildscript {
    // (대부분 최신 Gradle에서는 이 블록을 많이 쓰지 않음)
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

또는 `settings.gradle.kts` 에서 pluginManagement, dependencyResolutionManagement 관련 설정을 추가할 수 있습니다.

---

# 5. 모듈 `build.gradle.kts` (예시: `fake_store/build.gradle.kts`)

모듈에는 실제 `plugins` 및 `dependencies`를 선언합니다. Compose 설정, compileSdk 등도 포함합니다.

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    // alias 사용
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.fake_store"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fake_store"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Compose compiler version은 Kotlin, AGP와 호환되는 버전으로 맞출 것
        kotlinCompilerExtensionVersion = "1.5.6" // 예시: 실제 호환 버전 확인 필요
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose 기본
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Lifecycle Compose helpers
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Media3
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Coil
    implementation(libs.coil.compose)

    // Networking (optional)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
}
```

> 중요한 포인트:
>
> * `implementation(platform(libs.androidx.compose.bom))` 를 먼저 선언하면 Compose 각 모듈에 버전을 따로 지정할 필요 없음.
> * `ksp(libs.hilt.compiler)` : Hilt annotation processor를 KSP로 사용할 때. (KAPT를 쓰면 `kapt`로 대체)
> * `composeOptions.kotlinCompilerExtensionVersion` 은 Compose 및 Kotlin 버전과 호환되어야 하므로 버전 조정 필요.

---

# 6. 단계별로 `libs.versions.toml` 과 모듈 `build.gradle.kts`에 **무엇을 언제 추가할지** (권장 순서)

1. **프로젝트 초기**

   * `libs.versions.toml` : `composeBom`, `navigation` 등록
   * 모듈 `build.gradle.kts` : Compose BOM, Compose 기본 라이브러리, Navigation 추가
   * 결과: 기본 네비게이션 + Main 화면 구현

2. **Battery 기능 추가**

   * `libs.versions.toml` : `lifecycleRuntimeCompose` 추가
   * 모듈 : `implementation(libs.androidx.lifecycle.runtime.compose)`
   * 결과: BatteryScreen 구현 (BroadcastReceiver와 Compose 상태 연결)

3. **MP3 Player 추가**

   * `libs.versions.toml` : `media3` 추가
   * 모듈 : `implementation(libs.androidx.media3.exoplayer)`, `implementation(libs.androidx.media3.ui)`
   * 결과: ExoPlayer로 로컬 mp3 재생/멈춤 UI 완성

4. **Gallery + Coil 추가**

   * `libs.versions.toml` : `coil` 추가
   * 모듈 : `implementation(libs.coil.compose)`
   * 결과: 이미지 선택, `AsyncImage`로 표시

5. **확장(선택) — Hilt**

   * `libs.versions.toml` : `hilt`, `hiltNavigationCompose`, `ksp` plugin 등록
   * 루트 `build.gradle.kts` : `alias(libs.plugins.hilt.android) apply false`, `alias(libs.plugins.ksp) apply false`
   * 모듈 : `plugins { alias(libs.plugins.hilt.android); alias(libs.plugins.ksp) }` 및 `implementation(libs.hilt.android); ksp(libs.hilt.compiler)`
   * 결과: DI로 ViewModel/Repository 주입 구조화

---

# 7. 추가 권장·주의사항 (실전 팁)

* **Compose 컴파일러 버전 체크**: Kotlin 2.0.21일 때 Compose compiler와의 호환성을 반드시 확인하세요. Compose compiler-extension 버전은 Kotlin 버전에 따라 다르므로 공식 문서(또는 BOM)와 맞춰야 합니다.
* **KSP vs KAPT**: Hilt의 경우 KSP 지원이 좋은 선택이지만, 프로젝트에서 다른 라이브러리가 KAPT만 지원하면 충돌 가능성 있으니 동시 사용 여부를 확인하세요.
* **플러그인 별칭(alias) 사용**: `alias(libs.plugins.hilt.android) apply false` 를 루트에 선언해 모듈에서 `alias(...)`로 활성화하는 패턴이 안전합니다.
* **디버그 전용 라이브러리**: Compose 툴링은 `debugImplementation("androidx.compose.ui:ui-tooling")`로만 추가하세요.
* **네트워크 라이브러리**: 개발 중에는 `okhttp-logging-interceptor`를 `debugImplementation`으로 추가하면 요청/응답 로그 디버깅에 도움됩니다.
* **Gradle / AGP 호환성**: AGP 8.13.0 사용 시 Gradle wrapper(예: 8.7 이상)와 Kotlin plugin 호환성 확인 필요.

---

# 8. 요약 체크리스트 (수업 전 점검)

* [ ] 루트에 `libs.versions.toml` 추가 및 필요한 `versions`/`libraries`/`plugins` 등록했는가?
* [ ] 루트 `build.gradle.kts` 에서 `alias(... ) apply false`로 플러그인 준비했는가?
* [ ] 모듈 `build.gradle.kts` 에서 Compose BOM을 먼저 `implementation(platform(...))` 으로 선언했는가?
* [ ] Compose compiler, Kotlin, KSP(또는 KAPT) 버전 호환성 확인했는가?
* [ ] 각 단계별로 의존성 추가 후 빌드·실행 테스트 수행했는가?

---

원하시면 위 내용을 바탕으로 **실습용 레포 구조(폴더), 단계별 커밋 포인트, 각 단계의 수업용 README(.md)** 파일을 실제로 생성해 드릴게요.
또는 `libs.versions.toml`과 루트/모듈 `build.gradle.kts`의 **완전한 템플릿 파일**(실제 값으로 채운)을 만들어 드릴 수도 있습니다. 어느 쪽으로 진행할까요?



지금 주어진 `MainActivity.kt`(app_14_triple 기반)와 첨부된 3개 화면(`BatteryStatusScreen`, `Mp3PlayerScreen`, `GalleryScreen`) 이미지를 보면,
Compose UI를 단계별로 완성해 가며 **dependency를 추가하고 기능을 확장해 가는 학습형 강의 자료**를 설계하기에 아주 좋은 구조입니다.

---

# 🎓 강의용 학습자료: “Jetpack Compose로 기능 확장 앱 만들기”

**버전 기준**

* Kotlin: `2.0.21`
* AGP (Android Gradle Plugin): `8.13.0`
* Compose Compiler: `1.7.4`
* Gradle 버전: `8.7` 이상

---

## 🧭 학습 목표

학생들이 단일 Compose 앱을 단계별로 확장하면서 다음을 배웁니다:

1. **Compose 기본 구조 및 네비게이션 이해**
2. **의존성(dependency) 추가와 기능 확장**
3. **안드로이드 시스템 서비스 접근 (배터리 상태)**
4. **멀티미디어 제어 (MP3 Player)**
5. **이미지 처리 (갤러리 연동 + Coil)**

---

## 🚀 단계별 진행 계획

| 단계  | 기능                      | 주요 라이브러리           | 학습 포인트                         |
| --- | ----------------------- | ------------------ | ------------------------------ |
| 1단계 | 기본 Compose + Navigation | androidx.compose.* | Scaffold, Navigation, Preview  |
| 2단계 | Battery 상태 표시           | androidx.lifecycle | BroadcastReceiver + Compose UI |
| 3단계 | MP3 Player              | androidx.media3    | MediaPlayer 제어                 |
| 4단계 | Gallery 이미지 표시          | coil-compose       | 이미지 로딩, 권한 처리                  |
| 5단계 | 구조 정리 및 확장              | Hilt               | 모듈화, DI 개념 맛보기                 |

---

## 🧩 1단계: 기본 Compose + Navigation

**목표:**
Compose 앱의 뼈대를 만들고, 여러 화면 간 전환을 구현합니다.

### ✅ 필요한 dependency

```toml
[versions]
compose-bom = "2024.10.00"
navigation-compose = "2.8.3"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation-compose" }
```

```kotlin
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
}
```

### 🧱 학습 포인트

* `NavHost`, `composable()` 구조 이해
* `Scaffold` + `TopAppBar` + `FloatingActionButton` 구성
* `rememberNavController()` 의 역할
* Preview 활용으로 UI 미리보기

---

## 🔋 2단계: Battery Status 기능

**목표:**
BroadcastReceiver를 통해 시스템에서 배터리 상태를 수신하고 UI에 표시.

### ✅ 필요한 dependency

```toml
[libraries]
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version = "2.8.6" }
```

```kotlin
dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
}
```

### 🧱 학습 포인트

* `BroadcastReceiver` + `Context.registerReceiver()`
* Compose에서 `LaunchedEffect` 및 `mutableStateOf` 상태관리
* 배터리 상태 텍스트 실시간 반영

---

## 🎵 3단계: MP3 Player 기능

**목표:**
앱 내 오디오 파일을 재생/정지하는 단순 플레이어 구현.

### ✅ 필요한 dependency

```toml
[versions]
media3 = "1.4.1"

[libraries]
androidx-media3-exoplayer = { group = "androidx.media3", name = "media3-exoplayer", version.ref = "media3" }
androidx-media3-ui = { group = "androidx.media3", name = "media3-ui", version.ref = "media3" }
```

```kotlin
dependencies {
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}
```

### 🧱 학습 포인트

* `ExoPlayer` 인스턴스 생성 및 `remember` 관리
* `DisposableEffect` 로 자원 해제
* Play / Pause / Stop 버튼 Compose UI

---

## 🖼️ 4단계: Gallery 이미지 기능

**목표:**
Coil 라이브러리를 이용해 갤러리에서 선택한 이미지를 화면에 표시.

### ✅ 필요한 dependency

```toml
[versions]
coil = "2.7.0"

[libraries]
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }
```

```kotlin
dependencies {
    implementation(libs.coil.compose)
}
```

### 🧱 학습 포인트

* `rememberLauncherForActivityResult` 를 사용한 이미지 선택
* `Image` 컴포넌트에 `AsyncImage` 적용
* 런타임 권한 처리

---

## 🧠 5단계: 구조화 및 Hilt 도입 (선택 확장)

**목표:**
모듈 구조를 분리하고 Hilt로 의존성 주입(DI)을 학습.

### ✅ 필요한 dependency

```toml
[versions]
hilt = "2.53"
hilt-navigation-compose = "1.2.0"

[libraries]
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation-compose" }

[plugins]
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

```kotlin
plugins {
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
}
```

### 🧱 학습 포인트

* `@HiltAndroidApp`, `@AndroidEntryPoint` 사용
* `ViewModel` 주입 구조 이해
* 각 기능별 Repository 분리 및 주입

---

## 🧩 강의 운영 제안

| 세션  | 주제                    | 실습            | 결과물                      |
| --- | --------------------- | ------------- | ------------------------ |
| 1주차 | Compose 기본 & Scaffold | 기본 Todo UI 구현 | `MainScreen` 완성          |
| 2주차 | Navigation 추가         | 화면 전환         | `AddScreen` 완성           |
| 3주차 | Battery Status        | 시스템 상태 표시     | `BatteryStatusScreen` 완성 |
| 4주차 | MP3 Player            | 오디오 제어 UI     | `Mp3PlayerScreen` 완성     |
| 5주차 | Gallery + Coil        | 이미지 표시        | `GalleryScreen` 완성       |
| 6주차 | Hilt 도입               | 구조화 + 주입      | 모듈형 앱 완성                 |

---

원하신다면, 위 커리큘럼 기반으로
각 단계별 실습용 **폴더 구조 예시 + gradle 설정 + Compose 코드 예시**를 포함한 “교재형 Markdown 자료 (예: `lesson_01_basic_setup.md` ~ `lesson_05_gallery.md`)" 형태로 만들어드릴 수도 있습니다.

👉 그렇게 단계별 강의자료 파일 세트를 만들어드릴까요?
