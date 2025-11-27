# 계획: ToDoApp에 안드로이드 주요 구성요소 기능 추가

이 문서는 ToDoApp에 내비게이션 드로어(Navigation Drawer)와 안드로이드의 주요 구성 요소를 활용하는 세 가지 새로운 화면을 추가하는 계획을 설명합니다.

## 목표

`ToDoApp`에 내비게이션 드로어를 통합하고, 아래 세 가지 새로운 기능 화면을 추가합니다:
1.  **배터리 상태 보기**: `BroadcastReceiver` 사용.
2.  **MP3 플레이어**: `Service` 사용.
3.  **이미지 갤러리**: `ContentProvider` 사용.

각 새로운 화면은 별도의 파일로 작성되며, Composable 미리보기(`@Preview`)를 포함해야 합니다.

---

### 1단계: 내비게이션 드로어 구현

1.  **`ToDoApp.kt`의 `MainScreen` 수정**:
    *   `Scaffold`를 `ModalNavigationDrawer`로 감쌉니다.
    *   `rememberDrawerState`를 사용하여 `drawerState`를 생성합니다.
    *   `rememberCoroutineScope`를 사용하여 드로어를 열고 닫을 수 있는 코루틴 스코프를 생성합니다.

2.  **`BaseTopAppBar` 업데이트**:
    *   `BaseTopAppBar`가 `navigationIcon` 컴포저블을 인자로 받도록 수정합니다.
    *   `MainScreen`에서 드로어를 열 수 있는 `Menu` 아이콘(햄버거 아이콘)을 가진 `IconButton`을 `navigationIcon`으로 제공합니다.

3.  **드로어 콘텐츠 생성**:
    *   `ModalDrawerSheet`를 사용하여 드로어의 콘텐츠를 구성합니다.
    *   시각적인 구분을 위해 헤더 섹션을 추가합니다.
    *   각 새로운 화면(배터리, MP3, 갤러리)으로 이동할 `NavigationDrawerItem`을 추가합니다. (실제 화면 연결은 5단계에서 진행합니다.)

---

### 2단계: 배터리 상태 화면

1.  **파일 생성**: `todo/src/main/java/com/example/todo_triple/BatteryStatusScreen.kt`.

2.  **`BroadcastReceiver` 구현**:
    *   `Intent.ACTION_BATTERY_CHANGED` 인텐트를 수신하는 `BroadcastReceiver`를 등록하는 `BatteryStatusRoute` 컴포저블 함수를 생성합니다.
    *   리시버는 인텐트에서 배터리 잔량, 충전 상태 등의 정보를 추출합니다.
    *   `DisposableEffect`를 사용하여 리시버의 생명주기를 관리합니다 (화면 진입 시 등록, 이탈 시 해제).

3.  **UI 컴포저블 생성**:
    *   배터리 상태를 파라미터로 받아 화면에 표시하는 `BatteryStatusScreen` 컴포저블을 생성합니다.
    *   아이콘과 텍스트를 사용하여 배터리 잔량과 충전 여부를 시각적으로 표현합니다.

4.  **미리보기 추가**:
    *   `BatteryStatusScreen`에 대한 `@Preview` 컴포저블을 샘플 데이터와 함께 작성합니다.

---

### 3단계: MP3 플레이어 화면

1.  **파일 생성**: `todo/src/main/java/com/example/todo_triple/MP3PlayerScreen.kt`.

2.  **샘플 MP3 파일 추가**:
    *   `res/raw` 디렉터리를 생성합니다.
    *   이 디렉터리에 `sample_music.mp3`와 같은 샘플 MP3 파일을 추가합니다.

3.  **`MusicPlayerService` 생성**:
    *   `android.app.Service`를 상속하는 `MusicPlayerService` 클래스를 위한 새 Kotlin 파일을 생성합니다.
    *   `MediaPlayer`를 사용하여 `res/raw` 폴더의 MP3 파일 재생을 처리합니다.
    *   재생/일시정지 액션을 처리하기 위해 `onStartCommand`를 구현합니다.
    *   UI가 서비스와 통신할 수 있도록 `Binder`를 구현합니다.

4.  **UI 컴포저블 생성**:
    *   `MP3PlayerScreen` 컴포저블을 생성합니다.
    *   `MusicPlayerService`에 바인딩하기 위한 서비스 연결 로직을 구현합니다.
    *   서비스 바인더의 메서드를 호출하는 재생/일시정지 버튼을 추가합니다.
    *   음악의 현재 상태(예: "재생 중", "일시정지됨")를 화면에 표시합니다.

5.  **미리보기 추가**:
    *   실행 중인 서비스 없이 UI 레이아웃만 보여주는 `MP3PlayerScreen`의 `@Preview`를 작성합니다.

6.  **`AndroidManifest.xml` 업데이트**:
    *   `MusicPlayerService`를 위한 `<service>`를 선언합니다.

---

### 4단계: 갤러리 화면

1.  **파일 생성**: `todo/src/main/java/com/example/todo_triple/GalleryScreen.kt`.

2.  **권한 요청**:
    *   `AndroidManifest.xml`에 `READ_MEDIA_IMAGES` 권한을 추가합니다.
    *   `rememberLauncherForActivityResult`를 사용하여 권한 요청 로직을 구현합니다.

3.  **`ContentProvider` 쿼리 구현**:
    *   `GalleryViewModel` 또는 컴포저블 내에서 `ContentResolver`를 사용하여 `MediaStore.Images.Media.EXTERNAL_CONTENT_URI`를 쿼리합니다.
    *   가져온 이미지 URI를 상태 리스트에 로드합니다.

4.  **UI 컴포저블 생성**:
    *   `GalleryScreen` 컴포저블을 생성합니다.
    *   `LazyVerticalGrid`를 사용하여 `ContentProvider`에서 로드한 이미지를 격자 형태로 표시합니다.
    *   Coil 또는 Glide 라이브러리를 사용하여 이미지 URI로부터 비동기적으로 이미지를 로드하고 표시합니다.

5.  **미리보기 추가**:
    *   자리 표시자(placeholder) 이미지를 격자로 표시하는 `GalleryScreen`의 `@Preview`를 작성합니다.

---

### 5단계: 최종 통합

1.  **`Screen` Sealed Class 업데이트**:
    *   `ToDoApp.kt`에서 `BatteryStatus`, `MP3Player`, `Gallery` 객체를 `Screen` sealed class에 추가합니다.

2.  **`NavDisplay` 업데이트**:
    *   새로운 화면들에 대한 `when` 분기를 추가합니다. 각 분기는 해당 화면의 컴포저블(예: `BatteryStatusRoute`, `MP3PlayerScreen`)을 렌더링합니다.
    *   각 새로운 화면을 `TopAppBar`에 뒤로가기 화살표를 제공하는 `SubScreenScaffold`와 같은 공통 레이아웃으로 감쌉니다.

3.  **드로어 아이템 연결**:
    *   `ModalNavigationDrawer`의 콘텐츠에서 각 `NavigationDrawerItem`의 `onClick` 이벤트가 `onNavigate` 콜백을 사용하여 해당 화면으로 이동하도록 설정합니다.
---

## 소스코드 설명 (MP3 플레이어)

### MusicPlayerService.kt : 음악 재생의 '엔진'

이 파일은 MP3 플레이어의 핵심 로직을 담당하는 **백그라운드 서비스**입니다.

-   **역할**: UI(화면)와 독립적으로 음악을 재생, 일시정지, 정지하는 모든 작업을 처리합니다.
-   **`MediaPlayer` 관리**: 실제 음악을 재생하는 `MediaPlayer` 객체를 이 서비스 내에서만 생성하고 관리합니다.
-   **상태 관리 및 통신**: 현재 재생 상태(`isPlaying`), 재생 중인 곡 정보(`currentTrack`)를 `StateFlow`를 통해 외부에 알립니다. UI는 이 데이터를 구독하여 화면을 갱신합니다.
-   **Foreground Service**: 앱이 백그라운드에 있을 때도 안드로이드 시스템이 서비스를 강제로 종료하지 않도록 `Foreground Service`로 동작하며, 사용자에게 알림을 표시합니다.

> 비유: `MusicPlayerService`는 자동차의 엔진과 같습니다. 운전자가 차에서 내려도 시동이 걸려있는 한 엔진은 계속 작동하는 것처럼, 사용자가 앱 화면을 벗어나도 서비스는 음악을 계속 재생합니다.

### Mp3Player.kt (Mp3PlayerScreen) : 음악 제어를 위한 '리모컨'

이 파일은 사용자가 보는 **UI 화면**을 담당합니다.

-   **역할**: 사용자에게 재생/일시정지 버튼과 같은 컨트롤을 보여주고, 사용자 입력을 `MusicPlayerService`에 전달합니다.
-   **서비스에 연결(Bind)**: 화면이 생성될 때 `MusicPlayerService`에 연결하여 통신할 수 있는 '리모컨' 객체(Binder)를 얻습니다. `DisposableEffect`를 사용해 화면이 사라질 때 연결을 안전하게 해제합니다.
-   **명령 전달**: 사용자가 '재생' 버튼을 누르면, 화면은 서비스에 "음악을 재생하라"는 명령을 보냅니다.
-   **상태 구독 및 UI 업데이트**: `MusicPlayerService`가 보내주는 재생 상태(`StateFlow`)를 `collectAsState`로 구독하여, 음악이 재생되면 '일시정지' 아이콘으로 버튼 모양을 바꾸는 등 UI를 자동으로 업데이트합니다.

> 이처럼 **역할을 분리**하면, 복잡한 생명주기를 가진 UI와 핵심 기능을 분리하여 안정적이고 테스트하기 쉬운 코드를 만들 수 있습니다.
---

## 소스코드 설명 (갤러리 화면)

### GalleryScreen.kt : ContentProvider와 권한 요청의 실전 예제

이 파일은 안드로이드의 `ContentProvider`를 사용하여 기기에 저장된 이미지들을 불러와 화면에 보여주는 갤러리 UI를 구현합니다.

-   **권한 관리 (Permission Handling)**:
    -   `rememberLauncherForActivityResult`를 사용해 사용자에게 `READ_MEDIA_IMAGES` (미디어 파일 읽기) 권한을 요청합니다.
    -   사용자가 권한을 부여했는지(`hasPermission`) 상태를 관리하여, 권한이 있을 때만 이미지 로직을 실행하고 없을 때는 권한 요청 버튼을 보여줍니다.
    -   안드로이드 버전에 따라 필요한 권한을 분기 처리하여 하위 호환성을 확보합니다.

-   **데이터 로딩 (Data Loading)**:
    -   권한이 부여되면 `LaunchedEffect`를 통해 이미지 로딩을 시작합니다.
    -   `ContentResolver`를 사용하여 안드로이드 시스템의 미디어 데이터베이스(`MediaStore`)에 접근합니다. 이것이 바로 **`ContentProvider`**를 사용하는 방식입니다.
    -   `withContext(Dispatchers.IO)`를 사용해 이미지 URI 목록을 가져오는 작업을 **백그라운드 스레드**에서 처리하여 UI 멈춤 현상을 방지합니다.

-   **UI 구성 (UI Composition)**:
    -   `LazyVerticalGrid`를 사용하여 많은 수의 이미지를 효율적으로 그립니다. 화면에 보이는 부분만 렌더링하여 성능을 최적화합니다.
    -   이미지 로딩 라이브러리인 **Coil**의 `AsyncImage`를 사용해, URI로부터 이미지를 비동기적으로 로드하고 화면에 표시합니다.

> `GalleryScreen`은 사용자의 민감한 데이터에 접근하기 위한 **권한 요청**, 다른 앱(미디어 스토리지)의 데이터에 안전하게 접근하는 **ContentProvider**, 그리고 대량의 데이터를 효율적으로 보여주는 **Lazy UI**까지, 현대 안드로이드 앱 개발의 핵심적인 요소들을 한번에 학습할 수 있는 좋은 예제입니다.
---

## 소스코드 설명 (배터리 상태 화면)

### BatteryStatus.kt : ViewModel과 BroadcastReceiver의 결합

이 파일은 안드로이드 시스템의 배터리 상태 변경 이벤트를 받아 실시간으로 UI에 표시하는 방법을 보여줍니다. **ViewModel**, **BroadcastReceiver**, 그리고 **Stateless UI**라는 세 가지 핵심 파트로 구성됩니다.

-   **1. `BatteryViewModel` : 상태 관리자**
    -   **역할**: 배터리 잔량, 충전 상태 등의 UI 관련 데이터를 보관하고 관리합니다. 화면 회전과 같은 상황에서도 데이터가 안전하게 유지됩니다.
    -   **`StateFlow`**: `StateFlow`를 사용해 배터리 상태를 저장합니다. UI는 이 `StateFlow`를 구독하여 데이터 변경을 감지하고 화면을 자동으로 새로 그립니다.

-   **2. `BatteryStatusRoute` : 데이터와 UI의 연결고리**
    -   **역할**: 데이터 로직(ViewModel, BroadcastReceiver)과 UI(`BatteryStatusScreen`)를 연결하는 진입점 역할을 합니다.
    -   **`BroadcastReceiver`**: 안드로이드 시스템이 "배터리 상태가 변경되었다"고 보내는 방송(`Intent.ACTION_BATTERY_CHANGED`)을 수신하는 핵심 컴포넌트입니다.
    -   **`DisposableEffect`**: 메모리 누수를 방지하기 위해, 화면이 보일 때 `BroadcastReceiver`를 시스템에 등록하고, 화면이 사라질 때 자동으로 등록을 해제하는 안전장치 역할을 합니다.
    -   **`collectAsState`**: ViewModel의 `StateFlow` 데이터를 구독하여, 데이터가 변경될 때마다 UI가 자동으로 업데이트되도록 합니다.

-   **3. `BatteryStatusScreen` : 순수한 UI 컴포넌트**
    -   **역할**: 오직 화면을 그리는 책임만 집니다. 외부에서 데이터를 전달받아 그대로 보여주는 **Stateless Composable**입니다.
    -   **장점**: 자체적으로 상태나 로직을 갖지 않으므로, 테스트하기 쉽고 어떤 상황에서든 재사용할 수 있습니다. `@Preview`를 통해 쉽게 UI를 확인할 수 있는 것도 이 덕분입니다.

> 이 코드는 안드로이드 시스템 이벤트(**BroadcastReceiver**)를 감지하여 -> **ViewModel**의 상태를 업데이트하고 -> **Jetpack Compose**의 `StateFlow` 구독을 통해 UI를 선언적으로(declaratively) 갱신하는 현대적인 안드로이드 앱의 데이터 흐름을 명확하게 보여주는 예제입니다.



