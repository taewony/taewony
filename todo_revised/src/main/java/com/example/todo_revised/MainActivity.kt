package com.example.todo_revised

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.todo_revised.screen.AddScreenContent
import com.example.todo_revised.screen.BatteryStatusScreen
import com.example.todo_revised.screen.GalleryScreen
import com.example.todo_revised.screen.MainScreenContent
import com.example.todo_revised.screen.Mp3PlayerScreen
import com.example.todo_revised.screen.PreferenceScreen
import com.example.todo_revised.ui.theme.ComposeLabTheme
import com.example.todo_revised.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLabTheme {
                AppContent()
            }
        }
    }
}

// 1. 라우팅을 위한 화면 Key 정의
sealed interface Route {
    val title: String
}
data object MainScreen : Route { override val title = "Todo Revised" }
data object AddScreen : Route { override val title = "Add Todo" }
data object BatteryStatusScreen : Route { override val title = "배터리 상태" }
data object GalleryScreen : Route { override val title = "갤러리" }
data object Mp3PlayerScreen : Route { override val title = "MP3 플레이어" }
data object PreferenceScreen : Route { override val title = "앱 설정" }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    val backStack = remember { mutableStateListOf<Route>(MainScreen) }
    val currentScreen = backStack.last()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current

    val todoViewModel: TodoViewModel = viewModel()
    val todoItems by todoViewModel.todoItems.collectAsState()
    val sortOrder by todoViewModel.sortOrder.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("배터리 상태") },
                    selected = currentScreen == BatteryStatusScreen,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        backStack.add(BatteryStatusScreen) 
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("갤러리") },
                    selected = currentScreen == GalleryScreen,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        backStack.add(GalleryScreen) 
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("MP3 플레이어") },
                    selected = currentScreen == Mp3PlayerScreen,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        backStack.add(Mp3PlayerScreen) 
                    }
                )
            }
        }
    ) { 
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        if (currentScreen == MainScreen) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menu")
                            }
                        } else {
                            IconButton(onClick = { backStack.removeLastOrNull() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    },
                    actions = {
                        if (currentScreen == MainScreen) {
                            IconButton(onClick = { backStack.add(PreferenceScreen) }) {
                                Icon(Icons.Default.Settings, "Settings")
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentScreen == MainScreen) {
                    FloatingActionButton(onClick = { backStack.add(AddScreen) }) {
                        Icon(Icons.Filled.Add, "Add new todo")
                    }
                }
            }
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = { 
                    if (backStack.size > 1) {
                        backStack.removeLastOrNull()
                    } else {
                        activity?.finish()
                    }
                },
                entryProvider = { route ->
                    when (route) {
                        is MainScreen -> NavEntry(route) {
                            MainScreenContent(
                                items = todoItems,
                                sortOrder = sortOrder,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is AddScreen -> NavEntry(route) {
                            AddScreenContent(
                                onSave = { item ->
                                    todoViewModel.addTodo(item.task)
                                    backStack.removeLastOrNull()
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is BatteryStatusScreen -> NavEntry(route) {
                            BatteryStatusScreen(modifier = Modifier.padding(innerPadding))
                        }
                        is GalleryScreen -> NavEntry(route) {
                            GalleryScreen(modifier = Modifier.padding(innerPadding))
                        }
                        is Mp3PlayerScreen -> NavEntry(route) {
                            Mp3PlayerScreen(modifier = Modifier.padding(innerPadding))
                        }
                        is PreferenceScreen -> NavEntry(route) {
                            PreferenceScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            )
        }
    }
}

/*
@Composable
fun AppContent() {
    // 상태 관리: 할 일 목록
    val todos = remember { mutableStateListOf<String>() }

    // Navigation3 Backstack
    val backStack = remember { mutableStateListOf<Any>(MainScreen) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is MainScreen -> NavEntry(key) {
                    MainScreenContent(
                        onAddClick = { backStack += AddScreen },
                        datas = todos
                    )
                }
                is AddScreen -> NavEntry(key) {
                    AddScreenContent(
                        onBack = { backStack.removeLastOrNull() },
                        onSave = { todo ->
                            todos.add(todo)
                            backStack.removeLastOrNull()
                        }
                    )
                }
                else -> error("Unknown key: $key")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeLabTheme {
        AppContent()
    }
}
*/