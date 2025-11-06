
## 📚 Android Dependency 관리 심화 학습: TOML 및 Gradle KTS 활용

### 1\. 설정 파일의 역할 및 기능 설명

Android 프로젝트에서 Dependency 관리를 위해 사용하는 핵심 파일들의 역할을 이해하는 것이 중요합니다.

| 파일 이름 | 역할 | 설명 |
| :--- | :--- | :--- |
| `libs.versions.toml` | **중앙 버전/라이브러리 정의** | **TOML (Tom's Obvious, Minimal Language)** 포맷을 사용하여 모든 라이브러리의 버전, 그룹, 이름, 플러그인을 한곳에 모아 정의합니다. **버전 불일치(Version Conflict)** 문제를 예방하고 관리를 용이하게 합니다. |
| `build.gradle.kts` (Project Level) | **전역 설정 및 플러그인 버전** | 프로젝트 전체에 적용되는 설정 (예: `clean` 태스크)과 하위 모듈에서 공통적으로 사용할 **플러그인**의 버전을 정의합니다. |
| `build.gradle.kts` (Module Level) | **모듈별 Dependency** | 특정 모듈(예: `app` 모듈)이 실제 필요로 하는 라이브러리 목록을 정의합니다. 여기에 `libs.versions.toml`에서 정의한 별칭을 사용하여 Dependency를 추가합니다. |

-----

### 2\. `libs.versions.toml` 심층 분석

이 파일은 프로젝트의 모든 Dependency를 중앙에서 관리하는 **Version Catalog** 역할을 합니다.

#### 2.1. 구성 섹션 (Sections)

| 섹션 이름 | 역할 | `libs.versions.toml` 예시 | 설명 |
| :--- | :--- | :--- | :--- |
| `[versions]` | **버전 정의** | `room = "2.8.3"`, `navigation3 = "1.0.0-alpha08"` | 라이브러리 버전 번호를 정의합니다. `build.gradle.kts`에서 `$version.ref`로 참조됩니다. |
| `[libraries]` | **라이브러리 정의** | `androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }` | 각 라이브러리의 **그룹(group)**, **이름(name)**, \*\*버전(version.ref)\*\*을 정의합니다. |
| `[plugins]` | **플러그인 정의** | `ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }` | Android Gradle Plugin (`agp`), Kotlin 플러그인, KSP 등의 ID와 버전을 정의합니다. |

#### 2.2. 라이브러리 3요소: Group, Name, Version

라이브러리는 Maven Repository (예: Maven Central)에 등록될 때 다음 세 가지 요소로 식별됩니다.

1.  **Group (`group` / ID):** 라이브러리를 만든 조직이나 회사. (예: `androidx.room`, `com.google.dagger`)
2.  **Name (`name`):** 라이브러리 자체의 이름. (예: `room-runtime`, `hilt-android`)
3.  **Version (`version`):** 라이브러리의 특정 릴리스 버전. (예: `2.8.3`, `1.0.0-alpha08`)

> 💡 **중요성:** Dependency를 추가할 때 이 세 가지를 정확히 지정해야 Gradle이 네트워크를 통해 올바른 파일을 다운로드하고 빌드에 포함할 수 있습니다.

-----

### 3\. `build.gradle.kts` Dependency 설정 가이드

#### 3.1. Project Level (`settings.gradle.kts` 또는 Project `build.gradle.kts`)

현재 파일 목록에서는 Project Level `build.gradle.kts`가 제공되지 않았지만, 일반적으로 **Version Catalog (`libs`)를 사용하도록 설정**하며, 모든 모듈에 필요한 **플러그인을 등록**하는 곳입니다.

#### 3.2. Module Level (`app/build.gradle.kts`)

이 파일의 **`plugins`** 블록과 **`dependencies`** 블록이 가장 중요합니다.

| 블록 | 설정 내용 | `build.gradle.kts` 예시 |
| :--- | :--- | :--- |
| `plugins` | 모듈에 적용할 **플러그인 ID** 등록 | `alias(libs.plugins.ksp)` |
| `dependencies` | 모듈에 필요한 **실제 라이브러리** 등록 | `implementation(libs.androidx.room.runtime)` |

#### **핵심 Dependency 설정 (Room & DataStore)**

학생들이 추가해야 할 주요 Dependency와 그 의미입니다.

```kotlin
// build.gradle.kts (Module: app)

plugins {
    // KSP (Kotlin Symbol Processing) 플러그인을 활성화합니다.
    // Room 라이브러리가 코드를 생성할 수 있도록 반드시 필요합니다.
    alias(libs.plugins.ksp) //
}

dependencies {
    // Jetpack Compose를 위한 기본 Dependency (Navigation3, ViewModel 포함)
    implementation(libs.androidx.navigation3.runtime) //
    implementation(libs.androidx.lifecycle.viewmodel.compose) //

    // DataStore (사용자 설정 저장)
    // - 직접 문자열로 버전을 지정할 수도 있습니다.
    implementation("androidx.datastore:datastore-preferences:1.1.1") //

    // Room (데이터베이스 - 할 일 목록 저장)
    implementation(libs.androidx.room.runtime) // Room 기본 기능
    implementation(libs.androidx.room.ktx) // Coroutines 지원 기능

    // KSP 프로세서: 컴파일 시점에 Room 관련 코드(DB, DAO 구현체 등)를 자동 생성합니다.
    ksp(libs.androidx.room.compiler) //
    
    // Kotlinx Serialization: DataStore나 Room에서 복잡한 데이터 구조를 저장할 때 필요합니다.
    implementation(libs.kotlinx.serialization.core) //
}
```

-----

### 4\. 🚨 주의사항: 왜 Dependency 설정은 혼란스럽고 중요한가?

Dependency 설정은 앱의 **안정성**과 **성능**에 직접적인 영향을 미치므로 매우 중요하고, 사소한 오류에도 빌드가 실패할 수 있어 주의가 필요합니다.

1.  **버전 충돌 (Version Conflicts):**
      * 두 개 이상의 라이브러리가 같은 이름의 다른 버전을 요구할 때 발생합니다. (예: A 라이브러리는 `Coroutines 1.5.0`을 원하고, B 라이브러리는 `Coroutines 1.7.0`을 원할 때)
      * **해결책:** `libs.versions.toml`을 사용하여 모든 모듈이 통일된 버전을 사용하게 강제함으로써 충돌 위험을 줄일 수 있습니다. 이것이 Version Catalog를 사용하는 가장 큰 이유입니다.
2.  **프로세서 설정 (KSP/Kapt):**
      * Room과 같은 일부 라이브러리는 컴파일 시 코드를 자동 생성하기 위해 특별한 설정이 필요합니다. **`implementation` 대신 `ksp` 또는 `kapt`** 접두사를 사용해야 하며, 이를 누락하면 `@Entity`나 `@Dao` 같은 어노테이션이 무시되어 컴파일 오류가 발생합니다.
3.  **스코프 (Scope)의 혼란:**
      * **`implementation`**: 앱 실행 시 코드에 포함됩니다. 가장 일반적입니다.
      * **`testImplementation`**: 단위 테스트 코드에만 사용됩니다.
      * **`ksp`**: 컴파일 시 코드 생성에만 사용되며, 최종 앱 바이너리에는 포함되지 않습니다.
        이 스코프를 잘못 지정하면 앱 크기가 불필요하게 커지거나 필요한 코드가 누락될 수 있습니다.

학생들은 이 단계가 앱의 "뼈대"를 세우는 작업임을 인지하고, **오타**나 **버전 불일치**를 피하기 위해 `libs.versions.toml`의 별칭을 정확히 참조하는 습관을 들이는 것이 핵심입니다.

다음 단계로, `TodoItem.kt` 및 `TodoDao.kt` 파일을 구현하여 Room 데이터베이스 설정을 시작하는 방법을 설명해 드릴까요?