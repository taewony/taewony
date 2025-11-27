# `fake_store` 모듈 단계별 학습자료 (의존성 설정 포함)

**환경 기준:** Kotlin `2.0.21`, AGP `8.13.0`
아래 자료는 수업/실습용으로 한 단계씩 기능을 추가하면서 **어떤 dependency를 언제/왜 추가하는지**, `libs.versions.toml`(버전 카탈로그)과 **루트 / 모듈 `build.gradle.kts`에서의 적용 위치**를 명확히 구분해 설명합니다. 각 단계별로 필요한 코드 스니펫(핵심)도 포함되어 있어 학생들이 따라 하기 쉽습니다.

> ⚠️ **주의:** Compose 컴파일러나 KSP 등 일부 툴의 세부 버전은 Kotlin/AGP와 호환성을 반드시 확인해야 합니다. 수업 전 로컬에서 `./gradlew build`로 호환성 체크 권장.

---

# 목차

1. 개요 & 목표
2. 전체 흐름(단계 요약)
3. `libs.versions.toml` — 전체 예시 (단계별 확장 가능)
4. 루트 `build.gradle.kts` 설정 예시
5. 모듈 `build.gradle.kts` (초기 템플릿)
6. 단계별 구현 가이드 (Phase 0 ~ Phase 5)

   * 각 단계에서 추가할 dependency와 이유
   * 필수 파일/코드 스니펫
   * 실습/검증 체크리스트
7. 자주 발생하는 문제와 해결 팁
8. 수업용 체크리스트(교사용)

---

# 1. 개요 & 목표

## **학습 목표:**

* Retrofit으로 Fake Store API 호출 → Product 모델 파싱 → Compose `LazyColumn`으로 표시
* Coil로 이미지 로딩, Hilt로 DI, ViewModel로 상태 관리 패턴 습득
* 모듈화된 프로젝트 구조(`data`, `domain`, `presentation`) 이해

## 핵심 기술:
- StateFlow/Coroutines
- Lifecycle(Compose)
- Retrofit2/OkHttp 
- Coil
- Hilt 

## 프로젝트 구조
```
fake_store/
├── src/main/
│   ├── java/com/example/fake_store/
│   │   ├── di/
│   │   │   └── AppModule.kt            # Hilt 의존성 주입 모듈
│   │   │
│   │   ├── store/
│   │   │   ├── data/
│   │   │   │   ├── remote/
│   │   │   │   │   └── FakeStoreApi.kt     # Retrofit API 인터페이스
│   │   │   │   └── repository/
│   │   │   │       └── ProductsRepositoryImpl.kt # 데이터 소스 구현체
│   │   │   │
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   └── Product.kt          # 상품 데이터 클래스
│   │   │   │   └── repository/
│   │   │   │       └── ProductsRepository.kt # 데이터 소스 추상 인터페이스
│   │   │   │
│   │   │   └── presentation/
│   │   │       ├── products_screen/
│   │   │       │   ├── ProductsScreen.kt   # 메인 UI 컴포저블
│   │   │       │   ├── ProductsViewModel.kt # 화면의 상태와 로직 관리
│   │   │       │   └── ProductsState.kt    # UI 상태 데이터 클래스
│   │   │       └── util/
│   │   │           ├── components/LandingDialog.kt   # UI component
│   │   │           ├── components/MyTopAppBar.kt     # UI component
│   │   │           └── VIewModelExt.kt           # ViewModel 확장 함수 (sendEvent)
│   │   │
│   │   ├── ui/
│   │   │   └── theme/                    # Compose 테마
│   │   │
│   │   ├── FakeStoreApp.kt             # Hilt 설정을 위한 Application 클래스
│   │   └── MainActivity.kt             # 앱의 메인 진입점
│   │
│   └── AndroidManifest.xml
│
└── build.gradle.kts
```
---

# 2. 전체 흐름 (단계 요약)

* **Phase 0 — 프로젝트 준비**: Compose, BOM, Nav 기본 세팅
* **Phase 1 — Data Layer**: Retrofit API + DTO, RepositoryImpl
* **Phase 2 — Domain Layer**: 도메인 모델 + Repository 인터페이스
* **Phase 3 — DI (Hilt)**: Application, AppModule(Provides), Repository 바인딩
* **Phase 4 — Presentation**: ViewModel(StateFlow), ProductsScreen(Compose), Coil 적용
* **Phase 5 — 정리/확장**: 에러 핸들링 개선, 테스트, 네트워크 설명(OkHttp interceptor 등)

---

# 3. `gradle/libs.versions.toml` — 권장(전체 예시)

위 파일은 루트 프로젝트의 `gradle/libs.versions.toml` 에 위치합니다. 단계별로 항목을 추가/확장하세요.

```toml
[versions]
kotlin = "2.0.21"
agp = "8.13.0"
composeBom = "2024.10.00"         # Compose BOM 예시 (로컬/시간에 따라 변경)
navigation = "2.8.3"
lifecycleRuntimeCompose = "2.8.6"
retrofit = "2.11.0"
okhttp = "4.12.0"
coil = "2.7.0"
hilt = "2.53"
hiltNavigationCompose = "1.2.0"
ksp = "1.9.0-1.0.13"              # 예시: Kotlin 버전에 맞는 KSP 사용

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeCompose" }

retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }

coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

[plugins]
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "1.9.0-1.0.13" }  # 예시
```

> **설명:** `versions`에 주 버전들을 모아두고, `libraries`에 group+name+version.ref로 재사용합니다. `plugins`에 플러그인 별칭도 둡니다.

---

# 4. 루트 `build.gradle.kts` 예시 (프로젝트 레벨)

루트에서 플러그인을 `apply false`로 선언해 모듈에서 alias로 활성화하도록 합니다.

```kotlin
// build.gradle.kts (root)
plugins {
    // Gradle 플러그인(루트) - 모듈에서 실제로 apply 할 것들은 apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

> **포인트:** 루트에 `alias(libs.plugins.hilt.android) apply false`를 선언해야 모듈에서 `alias(libs.plugins.hilt.android)`로 `plugins { ... }` 사용 가능합니다.

---

# 5. 모듈 `build.gradle.kts` 템플릿 (초기 — Phase 0)

`app_18_fake_store/build.gradle.kts` 예시:

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.ksp)             // 필요시
    alias(libs.plugins.hilt.android)    // Hilt 사용 시
}

android {
    namespace = "com.example.app_18_fake_store"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app_18_fake_store"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6" // 실제 호환 버전 확인 필요
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose 기본 (BOM 사용으로 버전 명시 생략)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Navigation (Phase 0~)
    implementation(libs.androidx.navigation.compose)
}
```

> **설명:** 초반에는 Compose 기본 + Navigation만 넣고 시작. 이후 단계별로 `dependencies`에 항목을 추가.

---

# 6. 단계별 구현 가이드 (상세)

아래 각 Phase는 실습 순서대로 구성되어 있습니다. 각 Phase마다 **추가할 dependency**, **핵심 코드 스니펫**, **검증 방법**을 적었습니다.

---

## Phase 0 — 프로젝트 준비 (Compose 기본 + Navigation)

**목적:** Compose 프로젝트 골격을 만들고 Navigation으로 화면 전환 구조 준비.

### 추가 dependency

* `androidx.compose:compose-bom` (platform) — 이미 모듈 템플릿에 포함.
* `androidx.navigation:navigation-compose` — `libs.androidx.navigation.compose`

### 핵심 파일

* `MainActivity.kt` : `setContent { FakeStoreApp() }`
* `FakeStoreApp.kt` : `NavHost`를 통한 라우팅(추후 ProductsScreen 연결)

### 검증

* 앱 실행 시 빈 Compose 화면이 뜨는지
* Preview에서 `ProductsScreen` 자리 표시자 UI 보이는지

---

## Phase 1 — Data Layer: Retrofit + DTO (네트워크 연결)

**목적:** Fake Store API(`https://fakestoreapi.com/products`)에서 JSON 받아오기

### 추가 dependency

* `retrofit` (`libs.retrofit`)
* `retrofit-converter-gson` (`libs.retrofit-converter-gson`)
* `okhttp` (`libs.okhttp`)
* (옵션) `okhttp-logging-interceptor` — 개발 편의

`libs.versions.toml`의 항목을 반영한 뒤 모듈에 추가:

```kotlin
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    debugImplementation(libs.okhttp.logging.interceptor)
}
```

### 핵심 코드 스니펫

`store/data/remote/FakeStoreApi.kt`

```kotlin
package com.example.app_18_fake_store.store.data.remote

import retrofit2.http.GET

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}
```

`store/data/remote/ProductDto.kt` (간단 예)

```kotlin
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String?,
    val category: String?,
    val image: String
)
```

`di/NetworkModule.kt` (나중에 Hilt로 전환 전 임시 제공)

```kotlin
fun createRetrofit(): Retrofit {
    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    return Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

### 검증

* 간단한 테스트 스크립트(일회성 `suspend` 함수)에서 `getProducts()` 호출해 결과가 반환되는지 확인

---

## Phase 2 — Domain Layer: 도메인 모델, Repository 인터페이스

**목적:** 외부(네트워크) 모델과 UI에서 사용할 도메인 모델 분리

### 의존성

* 코루틴은 이미 Kotlin 표준에 포함되지만, 필요시 `org.jetbrains.kotlinx:kotlinx-coroutines-android` 추가

### 코드 스니펫

`store/domain/model/Product.kt`

```kotlin
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val imageUrl: String
)
```

`store/domain/repository/ProductsRepository.kt`

```kotlin
interface ProductsRepository {
    suspend fun getProducts(): Result<List<Product>>
}
```

`store/data/repository/ProductsRepositoryImpl.kt`

```kotlin
class ProductsRepositoryImpl(
    private val api: FakeStoreApi
): ProductsRepository {
    override suspend fun getProducts(): Result<List<Product>> = runCatching {
        api.getProducts().map { dto ->
            Product(dto.id, dto.title, dto.price, dto.image)
        }
    }
}
```

### 검증

* Repository를 직접 호출하는 단위 테스트(혹은 간단한 UI 버튼)로 정상적으로 모델이 매핑되는지 확인

---

## Phase 3 — DI (Hilt) 적용

**목적:** Retrofit, Repository 등 인스턴스 주입을 Hilt로 관리

### 추가 dependency

* `hilt-android`, `hilt-compiler` (KSP or KAPT)
* `hilt-navigation-compose` (ViewModel과 Nav 연동 시 편리)

모듈 의존성:

```kotlin
dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)   // 혹은 kapt(libs.hilt.compiler) 선택
    implementation(libs.hilt.navigation.compose)
}
```

루트 `build.gradle.kts`에 이미 `alias(libs.plugins.hilt.android) apply false` 해두었는지 확인 후 모듈에서 활성화.

### 핵심 코드 스니펫

`FakeStoreApplication.kt`

```kotlin
@HiltAndroidApp
class FakeStoreApplication : Application()
```

`di/AppModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun provideFakeStoreApi(retrofit: Retrofit): FakeStoreApi =
        retrofit.create(FakeStoreApi::class.java)
}
```

`di/RepositoryModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindProductsRepository(
        impl: ProductsRepositoryImpl
    ): ProductsRepository
}
```

`MainActivity.kt`

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

### 검증

* 앱 실행 시 Hilt 관련 컴파일 에러 없음
* Hilt 주입된 Repository를 통해 데이터 로드되는지 확인

---

## Phase 4 — Presentation: ViewModel + Compose UI + Coil

**목적:** ViewModel에서 StateFlow로 상태 관리, Compose에서 상태 관찰 및 표시, Coil로 이미지 비동기 로드

### 추가 dependency

* `androidx.lifecycle:lifecycle-runtime-compose` (lifecycle-aware collect)
* `io.coil-kt:coil-compose` (이미지 로딩)

모듈 의존성:

```kotlin
dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.compose)
}
```

### 핵심 코드 스니펫

`products_screen/ProductsState.kt`

```kotlin
data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)
```

`products_screen/ProductsViewModel.kt`

```kotlin
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repo: ProductsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ProductsState(isLoading = true))
    val uiState: StateFlow<ProductsState> = _uiState

    init { loadProducts() }

    fun loadProducts() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        repo.getProducts().fold(
            onSuccess = { list ->
                _uiState.value = ProductsState(isLoading = false, products = list)
            },
            onFailure = { e ->
                _uiState.value = ProductsState(isLoading = false, error = e.message ?: "Unknown")
            }
        )
    }
}
```

`products_screen/ProductsScreen.kt`

```kotlin
@Composable
fun ProductsScreen(viewModel: ProductsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.error}")
        }
        else -> LazyColumn {
            items(state.products) { product ->
                ProductItem(product = product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Row(Modifier.padding(8.dp)) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.title,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(product.title, fontWeight = FontWeight.Bold)
            Text("$${product.price}")
        }
    }
}
```

### 검증

* 앱 실행 → 로딩 → 상품 목록 표시(이미지 포함)
* 네트워크 실패시 에러 메시지 표시

---

## Phase 5 — 정리 및 확장 (테스트, 에러 처리, 성능)

**목적:** 사용자 경험 개선, 캐시, 에러 재시도, 테스트 추가

### 권장 추가 의존성(선택)

* `okhttp` 캐시 설정, `retrofit`에 `OkHttp` 캐시 적용
* Paging 라이브러리(`androidx.paging:paging-compose`) — 많은 항목 처리 시
* 테스트: `mockk`, `junit`, `kotlinx-coroutines-test`

### 개선 포인트

* 이미지 placeholder / error 이미지 지정 (`AsyncImage`의 `placeholder`/`error`)
* 네트워크 재시도 로직(간단한 retry) 또는 상태별 UI 개선
* ViewModel 단위 테스트: Repository를 모킹해서 StateFlow 변화 확인

---

# 7. 자주 발생하는 문제 & 해결 팁

* **Hilt 컴파일 에러 (ksp/kapt 혼용 문제)**

  * 프로젝트에 KAPT와 KSP를 동시에 사용하는 라이브러리가 있으면 충돌 가능. Hilt는 KSP로 설정하거나 KAPT 사용으로 통일하세요.
* **Compose 컴파일러 호환성 에러**

  * Kotlin `2.0.21`과 Compose compiler 버전을 반드시 맞춥니다. BOM 사용 시에도 compiler extension 버전은 별도로 설정할 수 있음.
* **Network on main thread**

  * Retrofit `suspend` 함수는 Dispatcher.IO에서 호출되지만, ViewModel에서 `viewModelScope.launch`로 호출하면 기본 Dispatcher가 Main이므로 `withContext(Dispatchers.IO)` 혹은 `viewModelScope.launch(Dispatchers.IO)` 사용 권장.
* **Coil 이미지 깨짐/지연**

  * 네트워크 지연 시 placeholder 설정 권장. 캐시 정책 검토.

---

# 8. 수업용 체크리스트 (교사용)

* [ ] `libs.versions.toml`이 루트에 존재하고 주요 버전 지정됨
* [ ] 루트 `build.gradle.kts`에 Plugin alias `apply false` 선언했는가?
* [ ] 모듈 `build.gradle.kts`에 Compose BOM 선언 및 기본 라이브러리 포함되었는가?
* [ ] Phase별로 학생들이 빌드해보고 실행 결과를 확인하는 실습 계획(각 Phase별 30~60분) 준비되었는가?
* [ ] Hilt 사용 시 KSP/KAPT 중 한 가지로 통일했는가?

---

# 부록 — 전체 예시 파일 목록(학생에게 나눠줄 자료)

* `gradle/libs.versions.toml` (위 예시)
* `build.gradle.kts` (루트) — alias apply false 포함
* `app_18_fake_store/build.gradle.kts` (모듈 템플릿)
* `FakeStoreApplication.kt` (`@HiltAndroidApp`)
* `di/AppModule.kt`, `di/RepositoryModule.kt`
* `store/data/remote/FakeStoreApi.kt`, `store/data/remote/ProductDto.kt`
* `store/data/repository/ProductsRepositoryImpl.kt`
* `store/domain/model/Product.kt`, `store/domain/repository/ProductsRepository.kt`
* `products_screen/ProductsViewModel.kt`, `ProductsScreen.kt`, `ProductsState.kt`
* `MainActivity.kt` (Hilt 적용된 `@AndroidEntryPoint`)

---

원하시면:

* 위 파일들을 **실습용 깃 레포 구조로**(README 포함) 생성해 드리거나,
* 각 Phase 별로 **학생용 과제 템플릿(예: 실패 케이스 포함)** 을 만들어 드리겠습니다.
  어떤 포맷(깃 레포 / 압축 파일 / 개별 md 파일)으로 드릴까요?
