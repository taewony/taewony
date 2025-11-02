package com.example.material_design.ui.previews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material_design.ui.AppBottomNav
import com.example.material_design.ui.AppDrawer
import com.example.material_design.ui.AppFab
import com.example.material_design.ui.AppTopBar
import com.example.material_design.ui.BaseScaffold
import com.example.material_design.ui.CardInfo
import com.example.material_design.ui.InfoCard
import com.example.material_design.ui.TabsLayout
import com.example.material_design.ui.theme.ComposeLabTheme
import kotlinx.coroutines.launch

/**
 * BaseScaffold 안에 카드 목록이 표시되는 통합 Preview.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, name = "Scaffold with Cards")
@Composable
fun PreviewWithCards() {
    ComposeLabTheme {
        BaseScaffold(
            topBar = {
                AppTopBar(
                    title = "Cards Preview",
                    onMenuClick = null
                )
            },
            content = { modifier ->
                val sampleCards = getSampleCardData()
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sampleCards) { cardInfo ->
                        InfoCard(cardInfo = cardInfo)
                    }
                }
            }
        )
    }
}

/**
 * InfoCard를 위한 샘플 데이터 목록을 생성하는 private 함수.
 */
private fun getSampleCardData(): List<CardInfo> {
    return List(10) { i ->
        CardInfo(
            title = "Card Title ${i + 1}",
            content = "This is some content for card ${i + 1}. The content can be of variable length."
        )
    }
}

/**
 * BaseScaffold, TabsLayout, AppBottomNav를 결합한 통합 Preview.
 */
@OptIn(ExperimentalMaterial3Api::class) // <-- Add this line
@Preview(showSystemUi = true, name = "Scaffold with Tabs & BottomNav")
@Composable
fun PreviewWithTabs() {
    ComposeLabTheme {
        // State for BottomNav
        val navItems = listOf(
            "Home" to Icons.Default.Home,
            "Favorites" to Icons.Default.Favorite,
            "Profile" to Icons.Default.Person
        )
        var selectedIndex by remember { mutableIntStateOf(0) }

        BaseScaffold(
            topBar = {
                AppTopBar(
                    title = "Tabs & BottomNav",
                    onMenuClick = null
                )
            },
            bottomBar = {
                AppBottomNav(
                    items = navItems,
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it }
                )
            },
            content = { modifier ->
                val tabs = listOf("Music", "Movies", "Books")
                TabsLayout(
                    modifier = modifier,
                    tabs = tabs,
                    pageContent = { page ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Content for ${tabs[page]}")
                        }
                    }
                )
            }
        )
    }
}

/**
 * 모든 컴포넌트(Drawer, FAB 포함)를 결합한 최종 앱 형태의 통합 Preview.
 */
@OptIn(ExperimentalMaterial3Api::class) // <-- Add this line
@Preview(showSystemUi = true, name = "Full App Preview")
@Composable
fun PreviewWithDrawerAndFab() {
    ComposeLabTheme {
        // State for Drawer
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // State for BottomNav
        val navItems = listOf(
            "Home" to Icons.Default.Home,
            "Favorites" to Icons.Default.Favorite,
            "Profile" to Icons.Default.Person
        )
        var selectedIndex by remember { mutableIntStateOf(0) }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    drawerState = drawerState,
                    onItemSelected = {
                        // Handle item selection, e.g., close drawer
                        scope.launch { drawerState.close() }
                    }
                )
            }
        ) {
            BaseScaffold(
                topBar = {
                    AppTopBar(
                        title = "Full App Preview",
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                },
                bottomBar = {
                    AppBottomNav(
                        items = navItems,
                        selectedIndex = selectedIndex,
                        onSelect = { selectedIndex = it }
                    )
                },
                fab = {
                    AppFab(
                        icon = Icons.Default.Add,
                        text = "Add",
                        onClick = { /* FAB click action */ }
                    )
                },
                content = { modifier ->
                    val tabs = listOf("Music", "Movies", "Books")
                    TabsLayout(
                        modifier = modifier,
                        tabs = tabs,
                        pageContent = { page ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Content for ${tabs[page]}")
                            }
                        }
                    )
                }
            )
        }
    }
}
