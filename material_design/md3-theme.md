아주 훌륭한 방향입니다 👏
말씀하신 대로 **Material Design 3(MD3)** 를 체계적으로 적용하려면,
기존의 `theme` 폴더를 단순한 색상/폰트 정의 수준에서 벗어나
**“역할 기반 디자인 시스템 파일”** 로 정비하는 게 좋습니다.

아래는 그에 맞춰 구성된 세 개의 파일 예제입니다.
(주석에는 *왜 그렇게 쓰는지*가 명확히 설명되어 있습니다.)

---

## 🎨 `theme/Color.kt`

```kotlin
package com.example.material_design.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 🎨 Color System 정비
 *
 * Material Design 3에서는 색상을 단순히 primary/secondary로 구분하지 않고,
 * "역할(Role) 기반"으로 사용합니다.
 * 예: surfaceContainerHigh, onPrimaryContainer 등
 *
 * 이렇게 역할 중심으로 정의하면 UI 전반의 일관성이 높아지고,
 * 다크 모드나 브랜드 리디자인에도 유연하게 대응할 수 있습니다.
 */

// ✅ Light Theme용 색상 정의
val LightColors = lightColorScheme(
    primary = Color(0xFF005AC1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E3FF),
    onPrimaryContainer = Color(0xFF001A41),

    secondary = Color(0xFF006687),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC3E8FF),
    onSecondaryContainer = Color(0xFF001E2A),

    background = Color(0xFFFDFBFF),
    onBackground = Color(0xFF1A1C1E),

    surface = Color(0xFFFDFBFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF43474E),

    // MD3의 "입체감 있는 영역" 표현
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F8FC),
    surfaceContainer = Color(0xFFF3F4F8),
    surfaceContainerHigh = Color(0xFFEDEEF2),
    surfaceContainerHighest = Color(0xFFE6E8EC),

    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C6CF)
)

// ✅ Dark Theme용 색상 정의
val DarkColors = darkColorScheme(
    primary = Color(0xFFAEC6FF),
    onPrimary = Color(0xFF002E6A),
    primaryContainer = Color(0xFF004397),
    onPrimaryContainer = Color(0xFFD6E3FF),

    secondary = Color(0xFF7ED0F9),
    onSecondary = Color(0xFF003547),
    secondaryContainer = Color(0xFF004D65),
    onSecondaryContainer = Color(0xFFC3E8FF),

    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE3E2E6),

    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C6CF),

    surfaceContainerLowest = Color(0xFF101113),
    surfaceContainerLow = Color(0xFF191C1E),
    surfaceContainer = Color(0xFF202326),
    surfaceContainerHigh = Color(0xFF292C30),
    surfaceContainerHighest = Color(0xFF323539),

    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E)
)

/**
 * UI 각 요소에서 다음과 같은 역할 기반 색상을 사용합니다:
 *
 * - AppTopBar → colorScheme.surfaceContainerHigh (살짝 돌출된 영역)
 * - InfoCard → colorScheme.surfaceVariant (보조적인 강조 영역)
 * - 버튼 강조 → colorScheme.primary
 * - 배경 → colorScheme.background
 */
```

---

## ✍️ `theme/Type.kt`

```kotlin
package com.example.material_design.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.material_design.R

/**
 * ✍️ Typography System 정비
 *
 * Material 3 Typography는 명확한 역할 기반 스타일 체계를 제공합니다.
 * 각 UI 구성 요소는 '위계(Hierarchy)'에 따라 적절한 스타일을 사용해야 합니다.
 *
 * 예시 규칙 (plan-org.md 기반):
 * - 큰 제목 (AppBar, Dialog 제목): headlineSmall
 * - 중간 제목 (Section 제목): titleMedium
 * - 본문 텍스트: bodyMedium
 * - 보조 설명/부제: bodySmall
 */

// 앱 공통 폰트 (Pretendard 예시)
val AppFontFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Light
    )
)

/**
 * 📘 예시 적용 규칙
 *
 * - InfoCard 제목 → typography.titleMedium
 * - InfoCard 내용 → typography.bodyMedium
 * - Drawer Header → typography.headlineSmall
 * - Drawer 메뉴 텍스트 → typography.bodyMedium
 */
```

---

## 📏 `theme/Spacing.kt`

```kotlin
package com.example.material_design.ui.theme

import androidx.compose.ui.unit.dp

/**
 * 📏 Spacing System 정비
 *
 * 앱 전체의 간격(Spacing)을 일정 규칙으로 관리하기 위한 객체입니다.
 * Modifier.padding(), Spacer, Arrangement.spacedBy() 등에 일관되게 사용합니다.
 *
 * 기본 단위는 4dp의 배수로 설정합니다.
 */

object AppSpacing {
    val xxs = 4.dp    // 아주 작은 간격 (아이콘 주변 등)
    val xs = 8.dp     // 작은 간격 (버튼 간, 텍스트 위아래)
    val sm = 12.dp    // 중간 간격 (리스트 아이템 내부 패딩)
    val md = 16.dp    // 표준 간격 (Card 내부, 화면 기본 여백)
    val lg = 24.dp    // 큰 간격 (섹션 구분, Drawer Header 등)
    val xl = 32.dp    // 매우 큰 간격 (전체 레이아웃 상단/하단)
}

/**
 * 📘 예시 적용
 *
 * - InfoCard 내부 여백: Modifier.padding(AppSpacing.md)
 * - LazyColumn 아이템 간 간격: Arrangement.spacedBy(AppSpacing.md)
 * - Drawer Header와 아이템 사이: Spacer(modifier = Modifier.height(AppSpacing.lg))
 * - AppTopBar 제목 좌우 여백: Modifier.padding(horizontal = AppSpacing.sm)
 */
```

---

## ✅ 정리

| 파일           | 역할                                                               | 적용 예                     |
| ------------ | ---------------------------------------------------------------- | ------------------------ |
| `Color.kt`   | MD3 역할 기반 색상 정의 (`surfaceContainerHigh`, `onPrimaryContainer` 등) | AppTopBar, InfoCard 배경   |
| `Type.kt`    | 타이포그래피 위계 정비 (`titleMedium`, `bodyMedium`)                       | Text style 일관성           |
| `Spacing.kt` | 앱 전역 여백 규칙                                                       | Modifier.padding, Spacer |

---

이 세 가지를 적용하면 이후 코드에서 다음처럼 일관되게 사용할 수 있습니다 👇

```kotlin
Box(
    modifier = Modifier
        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        .padding(AppSpacing.md)
) {
    Text(
        text = "공지사항",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
```

---

원하신다면 이 세 theme 파일을 실제 `ComposeLabTheme` (예: `Theme.kt`)에 통합 적용하는 예시도 만들어드릴 수 있습니다 —
즉, `MaterialTheme(colorScheme = LightColors, typography = AppTypography)` 식으로요.
그 부분까지 보여드릴까요?
