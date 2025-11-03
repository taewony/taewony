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