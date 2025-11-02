package com.example.material_design.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.material_design.ui.theme.ComposeLabTheme

/**
 * 앱에서 공통으로 사용할 하단 내비게이션 바.
 *
 * @param items 내비게이션 바에 표시할 아이템 목록. 각 아이템은 (라벨, 아이콘) 쌍으로 구성됩니다.
 * @param selectedIndex 현재 선택된 아이템의 인덱스.
 * @param onSelect 아이템이 선택되었을 때 호출될 콜백. 선택된 아이템의 인덱스를 전달받습니다.
 */
@Composable
fun AppBottomNav(
    items: List<Pair<String, ImageVector>>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                icon = { Icon(imageVector = item.second, contentDescription = item.first) },
                label = { Text(text = item.first) }
            )
        }
    }
}

@Preview(showBackground = true, name = "AppBottomNav Preview")
@Composable
fun AppBottomNavPreview() {
    ComposeLabTheme {
        // 1. Preview에서 사용할 샘플 데이터
        val navItems = listOf(
            "Home" to Icons.Default.Home,
            "Favorites" to Icons.Default.Favorite,
            "Profile" to Icons.Default.Person
        )
        // 2. 선택된 탭의 상태를 기억
        var selectedIndex by remember { mutableIntStateOf(0) }

        // 3. AppBottomNav 컴포넌트 호출
        AppBottomNav(
            items = navItems,
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it } // 탭 선택 시 상태 변경
        )
    }
}
