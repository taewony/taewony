// Force re-evaluation to resolve cache issues
package com.example.material_design

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLabTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // State for scroll behavior
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

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
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        BaseScaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AppTopBar(
                    title = "Material Design App",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    scrollBehavior = scrollBehavior
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
                val tabs = listOf("Home", "Favorites", "Settings")
                TabsLayout(
                    modifier = modifier,
                    tabs = tabs,
                    pageContent = { page ->
                        when (page) {
                            0 -> { // Home Tab
                                val sampleCards = getSampleCardData()
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(sampleCards) { cardInfo ->
                                        InfoCard(cardInfo = cardInfo)
                                    }
                                }
                            }
                            else -> { // Other tabs
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Content for ${tabs[page]}")
                                 }
                            }
                        }
                    }
                )
            }
        )
    }
}

private fun getSampleCardData(): List<CardInfo> {
    return List(10) { i ->
        CardInfo(
            title = "Card Title ${i + 1}",
            content = "This is some content for card ${i + 1}. The content can be of variable length."
        )
    }
}