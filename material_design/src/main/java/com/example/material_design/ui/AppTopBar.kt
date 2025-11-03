package com.example.material_design.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material_design.ui.theme.AppSpacing
import com.example.material_design.ui.theme.ComposeLabTheme

/**
 * 앱에서 공통으로 사용하는 상단 앱 바.
 * 메뉴 버튼, 로고, 제목을 한 줄에 표시하며 스크롤 동작을 지원합니다.
 *
 * @param title 앱 바에 표시될 제목.
 * @param scrollBehavior 스크롤 시 앱 바의 동작을 제어 (예: 축소, 사라짐).
 * @param onMenuClick 메뉴 아이콘 클릭 시 실행될 람다 함수.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    ),
    onMenuClick: (() -> Unit)? = null
) {
    MediumTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "App Logo"
                    // [테마 정비] 아이콘 색상은 TopAppBarDefaults에서 지정하므로 tint를 직접 설정하지 않습니다.
                )
                // [테마 정비] 3. 간격 규칙 적용
                // 로고와 제목 사이의 간격은 AppSpacing.sm으로 설정합니다.
                Spacer(modifier = Modifier.width(AppSpacing.sm))
                // [테마 정비] 2. 타이포그래피 시스템 적용
                // 상단 바의 제목은 `headlineSmall` 스타일을 사용하여 명확하게 표시합니다.
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                    // [테마 정비] 텍스트 색상은 TopAppBarDefaults에서 지정하므로 color를 직접 설정하지 않습니다.
                )
            }
        },
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                        // [테마 정비] 아이콘 색상은 TopAppBarDefaults에서 지정하므로 tint를 직접 설정하지 않습니다.
                    )
                }
            }
        },
        // [테마 정비] 1. 색상 시스템 적용
        // TopAppBar의 색상을 역할에 따라 명확하게 정의합니다.
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            // 기본 상태의 배경색은 브랜드 색상인 `primary`로 설정합니다.
            containerColor = MaterialTheme.colorScheme.primary,
            // 스크롤되어 상단 바가 축소되었을 때의 배경색은 `primaryContainer`로 변경하여 시각적 차이를 줍니다.
            scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            // 제목과 아이콘 등 내용물의 색상은 배경색(`primary`)과 대비가 잘 되는 `onPrimary`로 설정합니다.
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun MediumTopAppBarPreview() {
    ComposeLabTheme {
        AppTopBar(
            title = "OpenKnights Store",
            onMenuClick = {}
        )
    }
}
