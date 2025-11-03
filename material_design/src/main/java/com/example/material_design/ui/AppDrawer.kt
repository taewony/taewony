package com.example.material_design.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material_design.ui.theme.AppSpacing
import com.example.material_design.ui.theme.ComposeLabTheme
import kotlinx.coroutines.launch

/**
 * 앱에서 공통으로 사용할 Navigation Drawer의 콘텐츠.
 * 헤더와 메뉴 아이템 목록을 표시합니다.
 *
 * @param drawerState Drawer의 열림/닫힘 상태를 제어. 아이템 클릭 시 서랍을 닫기 위해 필요.
 * @param onItemSelected 아이템 클릭 시 호출될 람다. 클릭된 아이템의 이름을 전달.
 * @param items 서랍에 표시할 아이템 목록 (라벨, 아이콘).
 */
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    onItemSelected: (String) -> Unit,
    items: List<Pair<String, ImageVector>> = listOf(
        "공유하기" to Icons.Default.Share,
        "도움말" to Icons.AutoMirrored.Filled.Help,
        "검색하기" to Icons.Default.Search,
        "새 항목 추가" to Icons.Default.AddCircle
    )
) {
    val coroutineScope = rememberCoroutineScope()
    // 서랍 내에서 선택된 아이템을 추적하기 위한 상태
    var selectedItem by remember { mutableStateOf<String?>(null) }

    ModalDrawerSheet(
        // 서랍의 최대 너비를 320.dp로 제한하여 태블릿 등 큰 화면에서도 적절한 크기를 유지합니다.
        modifier = Modifier.widthIn(max = 320.dp)
    ) {
        // --- 1. Drawer Header ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                // [테마 정비] 1. 색상 시스템 적용
                // 헤더의 배경은 브랜드 색상(primary)보다 덜 강조되는 `primaryContainer`를 사용합니다.
                // 이는 넓은 영역에 사용하기에 더 적합한 색상입니다.
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ) {
            // [테마 정비] 2. 타이포그래피 시스템 적용
            // 헤더의 제목은 `headlineSmall` 스타일을 사용하여 서랍의 제목임을 명확히 합니다.
            Text(
                text = "OpenKnights",
                // `primaryContainer` 배경 위에는 `onPrimaryContainer` 색상의 텍스트를 사용해야 합니다.
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                // [테마 정비] 3. 간격(Spacing) 규칙 적용
                // 헤더 텍스트의 시작 부분에 24.dp의 여백을 주어 시각적 안정감을 더합니다.
                modifier = Modifier.padding(start = AppSpacing.lg),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // 헤더와 아이템 목록 사이의 간격은 AppSpacing.md로 설정합니다.
        Spacer(modifier = Modifier.height(AppSpacing.md))

        // --- 2. Drawer 아이템 목록 ---
        // 아이템 좌우에 AppSpacing.sm의 여백을 주어 정렬합니다.
        Column(modifier = Modifier.padding(horizontal = AppSpacing.sm)) {
            items.forEach { (title, icon) ->
                NavigationDrawerItem(
                    label = { Text(title, style = MaterialTheme.typography.bodyMedium) },
                    icon = { Icon(imageVector = icon, contentDescription = title) },
                    // 현재 선택된 아이템을 시각적으로 표시합니다.
                    selected = selectedItem == title,
                    onClick = {
                        selectedItem = title
                        onItemSelected(title)
                        // 아이템 클릭 후 자동으로 서랍을 닫습니다.
                        coroutineScope.launch { drawerState.close() }
                    },
                    // 아이템 자체의 기본 패딩을 유지하여 일관성을 확보합니다.
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    ComposeLabTheme {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        AppDrawer(
            drawerState = drawerState,
            onItemSelected = {}
        )
    }
}
