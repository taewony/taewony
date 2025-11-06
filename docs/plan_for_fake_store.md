## **🚀 Fake Store 앱 개발 3단계 로드맵**

### **1단계: Hilt 의존성 주입 환경 구축 [✅ 완료]**

가장 간단한 형태로 Hilt가 프로젝트 전반에서 **의존성 주입(DI)**을 수행할 수 있는지 확인하고 환경을 안정화하는 단계입니다.

<details>
<summary>1단계 상세 내용 보기</summary>

#### **1-1. 의존성 설정 및 모듈 연결**

1.  **libs.versions.toml hilt 버전 카탈로그 설정:**
2.  **Gradle 설정:** 루트 모듈과 fake\_store 모듈의 build.gradle.kts에 Hilt 플러그인과 라이브러리(런타임, KSP 컴파일러)를 추가

#### **1-2. Hilt 진입점 (Entry Point) 구현**

1.  **Application 클래스:** app 모듈의 FakeStoreApplication.kt에 @HiltAndroidApp 어노테이션을 붙여 Hilt의 **루트 컴포넌트**를 정의하고, AndroidManifest.xml에 등록합니다.
2.  **Activity 진입점:** MainActivity.kt에 @AndroidEntryPoint 어노테이션을 추가하여, Hilt가 이 Activity에 의존성을 주입할 수 있도록 설정합니다.

#### **1-3. 가장 간단한 주입 실험 (Constructor Injection)**

(이하 1단계의 상세한 코드 예제 및 설명 생략...)

</details>

---

### **2단계: ViewModel과 In-Memory 데이터로 UI 구축 [✅ 완료]**

1단계에서 구축한 Hilt 환경 위에서, **실제 네트워크 통신 없이** 메모리상의 가짜 데이터(In-Memory)를 사용하여 UI와 핵심 로직을 완성하는 단계입니다. 이 단계를 통해 **관심사 분리(Separation of Concerns)** 원칙을 학습하고, 안정적인 앱 아키텍처의 기반을 다집니다.

#### **🎯 2단계의 학습 목표**

*   **아키텍처 분리**: 데이터(Data), 비즈니스 로직(Domain), UI(Presentation) 계층을 왜 분리해야 하는지 이해하고 직접 구현합니다.
*   **단일 진실 공급원 (SSOT)**: `ViewModel`과 `State` 클래스를 통해 UI 상태를 한 곳에서 관리하는 패턴을 학습합니다.
*   **DI 활용**: `Repository` 패턴에 의존성 주입을 적용하여 `ViewModel`이 구체적인 구현이 아닌 인터페이스에 의존하도록 만듭니다.

#### **🛠️ 2단계 주요 작업 상세 설명**

##### **1. Domain 계층: 앱의 핵심 규칙 정의**

*   **`Product.kt` (모델):** 앱의 핵심 데이터가 무엇인지 정의합니다. UI에 표시될 상품의 속성(id, title, price 등)을 담는 데이터 클래스입니다.
*   **`ProductsRepository.kt` (인터페이스):** 데이터와 관련된 행위의 **'규칙'**을 정의합니다. "상품 목록을 가져온다"(`getProducts`)는 기능이 필요하다는 것을 명시할 뿐, '어떻게' 가져올지는 신경 쓰지 않습니다. `ViewModel`은 오직 이 인터페이스에만 의존하게 됩니다.

##### **2. Data 계층: 규칙의 실제 구현**

*   **`ProductsRepositoryImpl.kt` (구현체):** `ProductsRepository` 인터페이스의 실제 구현부입니다. 2단계에서는 **네트워크 통신 대신**, 미리 만들어둔 가짜 상품 목록(`fakeProducts`)을 반환합니다. 일부러 `delay`를 주어 실제 데이터를 가져오는 것처럼 흉내 낼 수도 있습니다.
    > **💡 왜 가짜 데이터를 쓸까?**
    > 서버 API가 아직 준비되지 않았거나, 네트워크 연결이 불안정한 상황에서도 UI 개발을 독립적으로 진행할 수 있습니다. 이는 개발 효율을 높이고 테스트를 용이하게 합니다.

##### **3. DI 계층: 인터페이스와 구현체 연결**

*   **`RepositoryModule.kt`:** Hilt에게 "누군가 `ProductsRepository`(규칙)를 달라고 요청하면, `ProductsRepositoryImpl`(실제 구현) 인스턴스를 주면 돼!" 라고 알려주는 이정표 역할을 합니다. `@Binds` 어노테이션으로 간단하게 연결합니다.

##### **4. Presentation 계층: 사용자에게 보여주기**

*   **`ProductsViewState.kt`:** UI가 그려져야 할 모든 상태(로딩 중 여부, 상품 목록, 에러 메시지 등)를 담는 데이터 클래스입니다. UI의 **'설계도'**와 같습니다.
*   **`ProductsViewModel.kt`:** UI의 **'두뇌'**입니다. `ProductsRepository`를 통해 데이터를 가져와서 `ProductsViewState`를 최신 상태로 업데이트하는 역할을 합니다.
*   **`ProductsScreen.kt`:** 실제 UI 화면입니다. `ViewModel`에 있는 `State`를 관찰(`collectAsStateWithLifecycle`)하다가, 상태가 변경되면 화면을 다시 그립니다. 이 화면은 데이터를 어떻게 가져오는지 전혀 알지 못하며, 오직 `State`만 보고 화면을 그리는 역할만 합니다.
*   **`ProductCard.kt`:** 상품 카드 UI를 로컬 리소스(`R.drawable.img`)를 사용하도록 수정했습니다. 2단계에서는 외부 라이브러리(Coil) 의존성을 제거하여 순수하게 로컬 데이터로 UI를 구성하는 데 집중합니다.

#### **✨ `hilt-navigation-compose`는 왜 꼭 필요할까?**

2단계에서 추가한 유일한 의존성인 `hilt-navigation-compose`는 **Compose UI와 Hilt ViewModel을 연결하는 핵심적인 다리**입니다. 이게 왜 필수적인지 비유를 통해 알아봅시다.

*   `ProductsViewModel` = **특별 주문 제작 자동차** (엔진으로 `Repository`가 꼭 필요함)
*   **Hilt** = **자동차 공장** (엔진을 만들고 자동차에 장착할 수 있음)
*   `ProductsScreen` (UI) = **자동차 운전자**
*   `hiltViewModel()` 함수 = **공장에 보내는 '특별 주문서'**

운전자(`Screen`)는 `ViewModel`이라는 자동차가 필요합니다. 그런데 이 자동차는 공장(Hilt)에서만 만들 수 있는 특별한 엔진(`Repository`)을 장착해야 합니다.

만약 **주문서(`hiltViewModel()`)**가 없다면, 운전자는 자동차를 직접 만들려고 시도하겠지만 엔진을 구하지 못해 실패하고 맙니다. (앱 크래시 발생 💥)

`hiltViewModel()`라는 주문서를 사용하면, 운전자는 공장에 "엔진이 장착된 특별한 자동차를 만들어주세요!"라고 간단히 요청할 수 있습니다. 그러면 공장(Hilt)이 알아서 엔진(`Repository`)을 만들고, 자동차(`ViewModel`)에 장착하여 완제품을 운전자에게 전달해 줍니다.

**결론: `@HiltViewModel`로 만들어진 ViewModel은 생성자에 Hilt가 넣어줘야 하는 부품(의존성)이 있으므로, Compose UI에서는 반드시 `hiltViewModel()` 함수를 통해 생성해야만 합니다.**

| 🏆 **2단계 성공 기준** | 앱 실행 시, 1.5초의 로딩 인디케이터가 보인 후, 로컬 리소스(`img.png`) 이미지를 사용하는 2개의 샘플 상품이 화면에 성공적으로 표시되었습니다. |

---

### **3단계: Retrofit, Coil 통합 및 네트워크 통신 [🎯 진행 중]**

2단계에서 만든 안정적인 UI와 로직 위에, 실제 네트워크 통신 라이브러리인 **Retrofit**과 이미지 로딩 라이브러리 **Coil**을 통합하여 앱을 완성하는 마지막 단계입니다. 가짜 데이터 소스를 실제 원격 서버 데이터로 교체합니다.

#### **🎯 3단계의 학습 목표**

*   **네트워크 통신**: Retrofit을 사용하여 원격 API로부터 데이터를 가져오는 방법을 학습합니다.
*   **DTO와 Domain Model 분리**: API 응답 객체(DTO)와 앱의 핵심 데이터 모델(Domain Model)을 분리하는 이유를 이해하고 매핑을 구현합니다.
*   **Hilt 모듈 활용**: `@Provides` 어노테이션을 사용하여 Retrofit 같은 외부 라이브러리 객체를 Hilt에 제공하는 방법을 학습합니다.
*   **비동기 이미지 로딩**: Coil을 사용하여 URL로부터 이미지를 비동기적으로 로드하고 화면에 표시합니다.

#### **🛠️ 3단계 주요 작업 상세 설명**

##### **1. 의존성 및 권한 설정 (완료)**

*   `build.gradle.kts`에 `retrofit`, `converter-gson`, `coil` 의존성 추가가 완료되었습니다.
*   `AndroidManifest.xml`에 인터넷 사용을 위한 `<uses-permission android:name="android.permission.INTERNET" />` 권한 추가가 완료되었습니다.

##### **2. Data 계층: 원격 데이터 소스 정의**

*   **`ProductDto.kt` (Data Transfer Object) 생성**: 
    *   API가 반환하는 JSON 데이터 구조와 정확히 일치하는 데이터 클래스입니다. API 응답을 담는 그릇 역할을 합니다.
    *   **왜 Domain Model(`Product.kt`)과 분리할까요?** API 명세는 언제든 바뀔 수 있습니다. 만약 API의 필드 이름이 바뀌더라도, DTO만 수정하고 Domain 모델은 그대로 유지할 수 있어 앱의 핵심 로직을 안전하게 보호할 수 있습니다.

*   **`ProductsApi.kt` (API 인터페이스) 생성**:
    *   Retrofit을 위한 인터페이스입니다. `@GET("products")`처럼 어떤 HTTP 메소드로 어떤 API 경로를 호출할지, 반환 타입은 무엇인지(`List<ProductDto>`)를 정의합니다.

##### **3. DI 계층: 네트워크 모듈 구성**

*   **`AppModule.kt` 생성 및 수정**:
    *   Hilt에게 Retrofit 객체를 어떻게 만드는지 알려주는 **'생성 설명서'**입니다.
    *   `@Provides` 어노테이션을 사용하여 `Retrofit` 인스턴스와 `ProductsApi` 인스턴스를 생성하는 함수를 정의합니다.
    *   `@Singleton`을 붙여 앱 전체에서 Retrofit 객체가 단 하나만 생성되도록 하여 자원을 절약합니다.

    ```kotlin
    // di/AppModule.kt 예시
    @InstallIn(SingletonComponent::class)
    @Module
    object AppModule {

        @Singleton
        @Provides
        fun provideProductsApi(): ProductsApi {
            return Retrofit.Builder()
                .baseUrl("https://fakestoreapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProductsApi::class.java)
        }
    }
    ```

##### **4. Data 계층: Repository 로직 변경**

*   **`ProductsRepositoryImpl.kt` 수정**:
    *   2단계의 가짜 데이터를 반환하던 로직을 실제 네트워크 통신 로직으로 교체하는 **핵심적인 변경**이 일어납니다.
    *   생성자에서 `ProductsApi`를 주입받도록 주석을 해제합니다.
    *   `getProducts()` 함수 내에서 `productsApi.getProducts()`를 호출하여 서버로부터 `List<ProductDto>`를 가져옵니다.
    *   가져온 DTO 리스트를 `map`을 통해 앱의 Domain 모델인 `List<Product>`로 변환하여 반환합니다. 이 과정을 통해 Presentation 계층은 데이터 소스가 로컬인지 원격인지 전혀 알 필요가 없게 됩니다.

##### **5. Presentation 계층: 이미지 로딩 방식 변경**

*   **`ProductCard.kt` 수정**:
    *   2단계에서 로컬 이미지를 표시하기 위해 사용했던 `Image(painter = painterResource(...))` 코드를 다시 `AsyncImage`로 변경합니다.
    *   Coil의 `AsyncImage`를 사용하여 `model` 인자에 서버에서 받은 상품의 이미지 URL(`product.image`)을 전달합니다. Coil이 자동으로 해당 URL의 이미지를 다운로드하여 화면에 표시해 줍니다.

| 🏆 **3단계 성공 기준** | 앱 실행 시, 로딩 인디케이터가 잠시 나타난 후, Fake Store API 서버로부터 받아온 **실제 상품 목록과 이미지가** 화면에 성공적으로 표시되어야 합니다. |

