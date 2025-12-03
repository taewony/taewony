package com.example.todo_triple

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_triple.screen.AddScreen
import com.example.todo_triple.screen.MainScreen
import com.example.todo_triple.viewmodel.TodoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch // Import launch for coroutine scope
import com.example.todo_triple.screen.PreferenceScreen

// --- Data Layer ---
data class ToDoItem(val id: Int, val text: String)

// --- UI Layer ---
sealed interface Route {
    val title: String
    data object Main : Route { override val title = "ToDo 리스트" }
    data object AddTodo : Route { override val title = "ToDo 추가" }
    data object Preference : Route { override val title = "앱 설정" }
    data object BatteryStatus : Route { override val title = "배터리 상태" }
    data object Mp3Player : Route { override val title = "MP3 플레이어" }
    data object Gallery : Route { override val title = "이미지 갤러리" }
}

@OptIn(ExperimentalMaterial3Api::class) // Add opt-in for ExperimentalMaterial3Api
@Composable
fun AppContent(modifier: Modifier = Modifier, viewModel: TodoViewModel = viewModel()) {
    val todos by viewModel.todoItems.collectAsState()
    val backstack = remember { mutableStateListOf<Route>(Route.Main) }
    val currentScreen = backstack.last()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("앱 메뉴", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                Divider()
                NavigationDrawerItem(
                    label = { Text(Route.Main.title) },
                    selected = currentScreen == Route.Main,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Route.Main) {
                            backstack.clear()
                            backstack.add(Route.Main)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Route.Preference.title) },
                    selected = currentScreen == Route.Preference,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Route.Preference) {
                            backstack.add(Route.Preference)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Route.BatteryStatus.title) },
                    selected = currentScreen == Route.BatteryStatus,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Route.BatteryStatus) {
                            backstack.add(Route.BatteryStatus)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Route.Mp3Player.title) },
                    selected = currentScreen == Route.Mp3Player,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Route.Mp3Player) {
                            backstack.add(Route.Mp3Player)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Route.Gallery.title) },
                    selected = currentScreen == Route.Gallery,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Route.Gallery) {
                            backstack.add(Route.Gallery)
                        }
                    }
                )
            }
        }
    ) {
        NavDisplay(
            currentScreen = currentScreen,
            onNavigate = { screen -> backstack.add(screen) },
            onBack = { backstack.removeLastOrNull() },
            todos = todos,
            onSaveTodo = { todoText ->
                // todos.add(ToDoItem(id = todos.size + 1, text = todoText))
                backstack.removeLastOrNull()
            },
            onOpenDrawer = { scope.launch { drawerState.open() } }
        )
    }
}

@Composable
fun NavDisplay(
    currentScreen: Route,
    onNavigate: (Route) -> Unit,
    onBack: () -> Unit,
    todos: List<ToDoItem>,
    onSaveTodo: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {
    when (currentScreen) {
        is Route.Main -> MainScreen(
            onAddClick = { onNavigate(Route.AddTodo) },
            todos = todos,
            onNavigate = onNavigate,
            onOpenDrawer = onOpenDrawer
        )
        is Route.AddTodo -> AddScreen(
            onSave = onSaveTodo,
            onBack = onBack
        )
        is Route.Preference -> PreferenceScreen(
            onNavigateBack = onBack
        )
        is Route.BatteryStatus -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                BatteryStatusRoute()
            }
        }
        is Route.Mp3Player -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                Mp3PlayerScreen()
            }
        }
        is Route.Gallery -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                GalleryScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            com.example.todo_triple.screen.BaseTopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        },
        content = content
    )
}