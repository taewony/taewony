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