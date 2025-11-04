package com.example.app_17_todo_revised

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.app_17_todo_revised.screen.AddScreenContent
import com.example.app_17_todo_revised.screen.BatteryStatusScreen
import com.example.app_17_todo_revised.screen.GalleryScreen
import com.example.app_17_todo_revised.screen.MainScreenContent
import com.example.app_17_todo_revised.screen.Mp3PlayerScreen
import com.example.app_17_todo_revised.screen.PreferenceScreen
import com.example.app_17_todo_revised.ui.theme.ComposeLabTheme
import com.example.app_17_todo_revised.viewmodel.TodoViewModel
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
sealed interface Route
data object MainScreen : Route
data object AddScreen : Route
data object BatteryStatusScreen : Route
data object GalleryScreen : Route
data object Mp3PlayerScreen : Route
data object PreferenceScreen : Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    // val todoItems = remember { mutableStateListOf<TodoItem>() } // 화면 나가면 지워짐
    val backStack = remember { mutableStateListOf<Route>(MainScreen) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // ✅ 1. 현재 Activity를 가져옵니다. LocalContext 캐스팅 대신 LocalActivity.current 사용하세요.
    val activity = LocalActivity.current

    // --- State ---TodoViewModel에서 모두 관리하도록 통합
    // 1. ViewModel 인스턴스를 가져옵니다. val viewModel: TodoViewModel = viewModel()
    // 2. ViewModel의 StateFlow를 구독하여 Compose State로 변환합니다.
    //    값이 변경되면 UI가 자동으로 업데이트됩니다.

    // 통합 이유: 화면에 필요한 모든 데이터와 로직을 하나의 ViewModel에서 관리하여
    //      데이터 흐름을 단순화하고 일관성을 유지합니다.
    val todoViewModel: TodoViewModel = viewModel()
    val todoItems by todoViewModel.todoItems.collectAsState()
    val sortOrder by todoViewModel.sortOrder.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                // 메뉴 #1
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("배터리 상태") },
                    selected = backStack.lastOrNull() == BatteryStatusScreen,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // 스택을 비우고 MainScreen 위에 새 화면을 올립니다.
                        backStack.clear()
                        backStack.add(MainScreen)
                        backStack.add(BatteryStatusScreen) // 화면 전환
                    }
                )
                // 메뉴 #2
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    label = { Text("갤러리") },
                    selected = backStack.lastOrNull() == GalleryScreen,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // 스택을 비우고 MainScreen 위에 새 화면을 올립니다.
                        backStack.clear()
                        backStack.add(MainScreen)
                        backStack.add(GalleryScreen) // 화면 전환
                    }
                )
                // 메뉴 #3
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("MP3 플레이어") },
                    selected = backStack.lastOrNull() == Mp3PlayerScreen,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // 스택을 비우고 MainScreen 위에 새 화면을 올립니다.
                        backStack.clear()
                        backStack.add(MainScreen)
                        backStack.add(Mp3PlayerScreen) // 화면 전환
                    }
                )

            }
        }
    ) {
        // --- 3. Scaffold를 이용한 화면 기본 구조 ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Todo Revised") },
					colors = TopAppBarDefaults.topAppBarColors(
						containerColor = MaterialTheme.colorScheme.primary,
						titleContentColor = MaterialTheme.colorScheme.onPrimary
					),
                    navigationIcon = { // 왼쪽 메뉴 아이콘
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = { // 오른쪽 설정 아이콘
                        IconButton(onClick = { backStack.add(PreferenceScreen) }) {
                            Icon(Icons.Default.Settings, "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { backStack.add(AddScreen) }) {
                    Icon(Icons.Filled.Add, "Add new todo")
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                // --- 4. NavDisplay를 이용한 화면 라우팅 ---
                NavDisplay(
                    backStack = backStack,
                    onBack = {                         // 스택에 화면이 하나만 남았을 경우 (MainScreen) 앱이 닫히지 않도록 방지
                        if (backStack.size > 1) {
                            backStack.removeLastOrNull()
                        } else {
                            // 스택에 화면이 1개만 남았다면 앱 종료
                            activity?.finish()
                        }
                    },
                    // 백 스택 키를 NavEntry로 변환하는 entryProvider
                    // entryProvider는 함수가 아니라 Named Parameter
                    entryProvider = { route ->
                        when (route) {
                            is MainScreen -> NavEntry(route) {
                                MainScreenContent(
                                    items = todoItems,
                                    sortOrder = sortOrder
                                )
                            }
                            is AddScreen -> NavEntry(route) {
                                AddScreenContent(
                                    onBack = { backStack.removeLastOrNull() },
                                    // 이유: UI는 "저장해줘" 라는 요청만 보내고, 실제 DB에 저장하는 복잡한 로직은
                                    //      ViewModel과 Repository가 담당하도록 역할을 분리합니다.
                                    onSave = {  item, ->
                                        // 이제 task 문자열만 ViewModel에 전달합니다.
                                        todoViewModel.addTodo(item.task)
                                        backStack.removeLastOrNull()
                                    }
                                )
                            }
                            is BatteryStatusScreen -> NavEntry(route) { BatteryStatusScreen() }
                            is GalleryScreen -> NavEntry(route) { GalleryScreen() }
                            is Mp3PlayerScreen -> NavEntry(route) { Mp3PlayerScreen() }
                            is PreferenceScreen -> NavEntry(route) {
                                PreferenceScreen(
                                    onNavigateBack = { backStack.removeLastOrNull() }
                                )
                            }
                        }
                    }
                )
            }
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