package com.example.material_design.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

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

// 커스텀 폰트를 제외하고 시스템 기본 폰트를 사용하도록 설정합니다.
val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
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