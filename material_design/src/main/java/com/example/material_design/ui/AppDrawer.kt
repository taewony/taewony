
package com.example.material_design.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 앱에서 공통으로 사용할 Navigation Drawer의 콘텐츠.
 * 헤더와 메뉴 아이템 목록을 표시합니다.
 *
 * @param drawerState Drawer의 열림/닫힘 상태를 제어. 아이템 클릭 시 서랍을 닫기 위해 필요.
 * @param onItemSelected 아이템 클릭 시 호출될 람다. 클릭된 아이템의 이름을 전달.
 */
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    onItemSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Drawer에 표시할 아이템 목록 (이름과 아이콘)
    val drawerItems = listOf(
        "공유하기" to Icons.Default.Share,
        "도움말" to Icons.AutoMirrored.Filled.Help,
        "검색하기" to Icons.Default.Search,
        "새 항목 추가" to Icons.Default.AddCircle
    )

    ModalDrawerSheet(
        modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.75f) // 너비 조정
    ) {
        // 1. Drawer Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "OpenKnights",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(start = 24.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Drawer 아이템 목록
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            drawerItems.forEach { (title, icon) ->
                NavigationDrawerItem(
                    label = { Text(title) },
                    icon = { Icon(imageVector = icon, contentDescription = title) },
                    selected = false, // 선택 상태를 고정하지 않음
                    onClick = {
                        onItemSelected(title)
                        // 아이템 클릭 후 자동으로 서랍을 닫음
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}
