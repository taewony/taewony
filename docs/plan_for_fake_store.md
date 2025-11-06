## **🚀 Fake Store 앱 개발 3단계 로드맵**

### **1단계: Hilt 의존성 주입 환경 구축 및 실험**

가장 간단한 형태로 Hilt가 프로젝트 전반에서 **의존성 주입(DI)**을 수행할 수 있는지 확인하고 환경을 안정화하는 단계

#### **1-1. 의존성 설정 및 모듈 연결**

1. **libs.versions.toml hilt 버전 카탈로그 설정:**   
2. **Gradle 설정:** 루트 모듈과 fake\_store 모듈의 build.gradle.kts에 Hilt 플러그인과 라이브러리(런타임, KSP 컴파일러)를 추가

#### **1-2. Hilt 진입점 (Entry Point) 구현**

1. **Application 클래스:** app 모듈의 FakeStoreApplication.kt에 @HiltAndroidApp 어노테이션을 붙여 Hilt의 **루트 컴포넌트**를 정의하고, AndroidManifest.xml에 등록합니다.  
2. **Activity 진입점:** MainActivity.kt에 @AndroidEntryPoint 어노테이션을 추가하여, Hilt가 이 Activity에 의존성을 주입할 수 있도록 설정합니다.

#### **1-3. 가장 간단한 주입 실험 (Constructor Injection)**

## **1-1-1 `libs.versions.toml` 수정 (Hilt 관련 추가)**

아래 내용을 `[versions]`, `[libraries]`, `[plugins]` 섹션에 추가하세요 👇

\[versions\]  
\# 기존 버전 밑에 추가  
hilt \= "2.53"

\[libraries\]  
\# Hilt 의존성  
hilt-android \= { group \= "com.google.dagger", name \= "hilt-android", version.ref \= "hilt" }  
hilt-compiler \= { group \= "com.google.dagger", name \= "hilt-compiler", version.ref \= "hilt" }

\[plugins\]  
\# 기존 plugins 밑에 추가  
hilt-android \= { id \= "com.google.dagger.hilt.android", version.ref \= "hilt" }

---

## **1-1-2 루트 `build.gradle.kts` 수정**

루트(프로젝트 수준) `build.gradle.kts` 파일에 Hilt 플러그인을 alias로 등록합니다.

plugins {  
    alias(libs.plugins.hilt.android) apply false  
    alias(libs.plugins.ksp) apply false  
}

💡 이 설정을 해야 모듈에서 `alias(libs.plugins.hilt.android)`를 사용할 수 있습니다.

---

## **1-1-3 모듈(`app/build.gradle.kts`) 설정**

`plugins` 블록과 `dependencies` 블록에 Hilt 추가:

plugins {  
    alias(libs.plugins.android.application)  
    alias(libs.plugins.kotlin.android)  
    alias(libs.plugins.kotlin.compose)  
    alias(libs.plugins.ksp)  
    alias(libs.plugins.hilt.android)  
}

android {  
    namespace \= "com.example.hilttest"  
    compileSdk \= 34

    defaultConfig {  
        applicationId \= "com.example.hilttest"  
        minSdk \= 24  
        targetSdk \= 34  
        versionCode \= 1  
        versionName \= "1.0"  
    }  
}

dependencies {  
    implementation(libs.hilt.android)  
    ksp(libs.hilt.compiler)  
}

---

## **1-2-2 Hilt Application 클래스 생성**

`Hilt`를 활성화하려면 `@HiltAndroidApp`으로 표시된 `Application` 서브클래스를 하나 만들어야 합니다.

**`FakeStoreApplication.kt`**

| package com.example.fake\_store import android.app.Application import dagger.hilt.android.HiltAndroidApp /\* Hilt를 활성화하려면 @HiltAndroidApp으로 표시된 Application 서브클래스를 하나 만들어야 합니다. 그리고 AndroidManifest.xml에 등록: \<application     android:name=".FakeStoreApplication"     android:label="@string/app\_name"     android:theme="@style/Theme.HiltTest"\> \</application\> \*/ @HiltAndroidApp class FakeStoreApplication : Application() { } |
| :---- |

---

## **1-3-1 가장 간단한 Hilt 의존성 테스트 코드**

이 코드는 **DI가 실제로 동작하는지** 가장 간단히 확인할 수 있는 예제입니다.

### **`MainActivity.kt`**

package com.example.hilttest

import android.os.Bundle  
import androidx.activity.ComponentActivity  
import androidx.activity.compose.setContent  
import androidx.compose.material3.Text  
import androidx.compose.material3.MaterialTheme  
import dagger.hilt.android.AndroidEntryPoint  
import javax.inject.Inject

// 간단한 의존성 클래스  
class Greeting **@Inject** constructor() {  
    fun message(): String \= "Hello from Hilt\!"  
}

**@AndroidEntryPoint**  
class MainActivity : ComponentActivity() {

    // Hilt가 주입하는 의존성  
    **@Inject** lateinit var greeting: Greeting

    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)  
        setContent {  
            MaterialTheme {  
                Text(text \= greeting.message())  
            }  
        }  
    }  
}

## **1-3-2 동작 확인**

빌드하고 앱을 실행하면 UI 화면에 다음 문구가 보이면 성공입니다 👇

Hello from Hilt\!

즉, Hilt가 `Greeting` 인스턴스를 자동 생성해 `MainActivity`에 주입하고 있습니다.

---

## **\[참고\] Hilt 동작 원리 요약**

| 요소 | 역할 |
| ----- | ----- |
| `@HiltAndroidApp` | Application 클래스에 DI 루트 생성 |
| `@AndroidEntryPoint` | Activity/Fragment/ViewModel 등 주입 가능한 진입점 |
| `@Inject` | 주입 대상 선언 |
| `hilt-android` / `hilt-compiler` | Hilt 핵심 및 코드 생성 도구 |
| `hilt-navigation-compose` | Compose Navigation \+ Hilt 연동 시 사용 |

---

---

### **2단계: In-Memory 데이터 기반의 UI 구성 (UI / ViewModel) 🖼️**

Hilt 환경이 안정화되면, 네트워크 통신 없이 \*\*가짜 데이터(In-Memory)\*\*를 사용하여 UI와 ViewModel을 완성합니다. DI가 Repository 패턴에 어떻게 적용되는지 학습합니다.

#### **2-1. Domain 및 Presentation 계층 정의 (In-Memory)**

1. **도메인 모델:** Product.kt와 ProductsRepository.kt (인터페이스)를 정의합니다.  
2. **더미 Repository:** ProductsRepositoryImpl.kt에서 **네트워크 통신 대신** List\<Product\> 형태의 **더미 데이터**를 반환하도록 구현하고, @Inject constructor()를 사용하여 **Hilt 주입이 가능하게** 설정합니다.  
3. **Hilt 모듈:** AppModule.kt에 @Binds 또는 @Provides를 사용하여 ProductsRepositoryImpl을 ProductsRepository 인터페이스에 연결합니다 (Hilt를 Repository 패턴에 적용).

#### **2-2. ViewModel 및 UI 구현**

1. **ViewModel:** ProductsViewModel.kt를 만들고 **Hilt의 ProductsRepository를 주입**받도록 @HiltViewModel 및 @Inject constructor()를 적용합니다. 이 ViewModel이 더미 데이터를 로딩하여 ProductsState.kt로 UI 상태를 관리하게 합니다.  
2. **UI 컴포넌트:** ProductsScreen.kt를 구현하고, ProductsViewModel을 사용하여 더미 데이터를 LazyColumn으로 화면에 표시합니다. (네트워크 로딩/에러 처리 UI 컴포넌트도 미리 구성합니다.)

| 🏆 **성공 기준** | 화면에 더미 데이터(Product 목록)가 성공적으로 표시되고, ViewModel이 Repository를 Hilt를 통해 주입받아 작동하는지 확인. |

---

### **3단계: Retrofit, Coil 통합 및 네트워크 통신 (Network Integration) 🌐**

안정된 Hilt 및 UI 환경 위에, 실제 네트워크 통신 라이브러리인 Retrofit과 이미지 로딩 라이브러리 Coil을 통합하여 앱을 완성합니다.

#### **3-1. 네트워크 라이브러리 설정**

1. **Dependency 추가:** fake\_store/build.gradle.kts에 Retrofit, Converter (JSON 파싱), OkHttp, Coil 라이브러리를 추가합니다.  
2. **권한 설정:** AndroidManifest.xml에 INTERNET 권한을 추가합니다.

#### **3-2. Hilt 네트워크 모듈 구성**

1. **API 정의:** FakeStoreApi.kt에 Retrofit 인터페이스를 정의합니다.  
2. **Network Module:** AppModule.kt에 @Provides를 사용하여 **Singleton**으로 Retrofit 인스턴스, OkHttpClient 인스턴스, 그리고 FakeStoreApi 인스턴스를 생성하고 Hilt에 제공하도록 구현합니다.

#### **3-3. 네트워크 로직 대체 및 UI 업데이트**

1. **Repository 수정:** 2단계에서 작성한 ProductsRepositoryImpl.kt를 수정하여 **더미 데이터 대신** FakeStoreApi를 사용하여 실제 네트워크 통신을 수행하도록 변경합니다.  
2. **UI 변경:** ProductsScreen.kt에서 Coil의 AsyncImage 컴포저블을 사용하여 네트워크에서 받은 **이미지 URL**을 로딩하여 상품 이미지를 화면에 표시합니다.

| 🏆 **성공 기준** | 앱이 실행될 때 실제 네트워크 통신을 통해 상품 목록을 가져와 **이미지**와 함께 화면에 표시되는지 확인. |

