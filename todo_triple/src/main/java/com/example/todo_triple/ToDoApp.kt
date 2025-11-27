package com.example.todo_triple

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu // Import Menu icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch // Import launch for coroutine scope

// --- Data Layer ---
data class ToDoItem(val id: Int, val text: String)

// --- UI Layer ---
sealed class Screen(val route: String, val title: String) {
    object Main : Screen("main", "ToDo 리스트")
    object AddTodo : Screen("add", "ToDo 추가")
    // Add new screens for navigation drawer
    object BatteryStatus : Screen("battery", "배터리 상태")
    object Mp3Player : Screen("mp3_player", "MP3 플레이어")
    object Gallery : Screen("gallery", "이미지 갤러리")
}

@OptIn(ExperimentalMaterial3Api::class) // Add opt-in for ExperimentalMaterial3Api
@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    val todos = remember { mutableStateListOf<ToDoItem>() }
    val backstack = remember { mutableStateListOf<Screen>(Screen.Main) }
    val currentScreen = backstack.last() // Correctly observes changes in mutableStateListOf

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Drawer state
    val scope = rememberCoroutineScope() // Coroutine scope for drawer actions

    // The ModalNavigationDrawer wraps the entire main content
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Drawer header
                Text("앱 메뉴", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
                Divider()
                // Navigation items
                NavigationDrawerItem(
                    label = { Text(Screen.Main.title) },
                    selected = currentScreen == Screen.Main,
                    onClick = {
                        scope.launch { drawerState.close() }
                        // Clear backstack and navigate to Main
                        if (currentScreen != Screen.Main) { // Avoid adding duplicate to backstack if already on main
                            backstack.clear()
                            backstack.add(Screen.Main)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Screen.BatteryStatus.title) },
                    selected = currentScreen == Screen.BatteryStatus,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Screen.BatteryStatus) {
                            backstack.add(Screen.BatteryStatus)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Screen.Mp3Player.title) },
                    selected = currentScreen == Screen.Mp3Player,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Screen.Mp3Player) {
                            backstack.add(Screen.Mp3Player)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(Screen.Gallery.title) },
                    selected = currentScreen == Screen.Gallery,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (currentScreen != Screen.Gallery) {
                            backstack.add(Screen.Gallery)
                        }
                    }
                )
            }
        }
    ) {
        // Content of the screen (NavDisplay and its children)
        NavDisplay(
            currentScreen = currentScreen,
            onNavigate = { screen -> backstack.add(screen) },
            onBack = { backstack.removeLastOrNull() },
            todos = todos,
            onSaveTodo = { todoText ->
                todos.add(ToDoItem(id = todos.size + 1, text = todoText))
                backstack.removeLastOrNull()
            },
            // Pass drawer control to TopAppBar
            onOpenDrawer = { scope.launch { drawerState.open() } }
        )
    }
}

@Composable
fun NavDisplay(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    todos: List<ToDoItem>,
    onSaveTodo: (String) -> Unit,
    onOpenDrawer: () -> Unit // New parameter for opening drawer
) {
    when (currentScreen) {
        Screen.Main -> MainScreen(
            onAddClick = { onNavigate(Screen.AddTodo) },
            todos = todos,
            onNavigate = onNavigate,
            onOpenDrawer = onOpenDrawer // Pass to MainScreen
        )
        Screen.AddTodo -> AddScreen(
            onSave = onSaveTodo,
            onBack = onBack
        )
        // New screens for navigation drawer
        Screen.BatteryStatus -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                BatteryStatusRoute() // Assuming BatteryStatusRoute is the entry point
            }
        }
        Screen.Mp3Player -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                Mp3PlayerScreen() // Assuming Mp3PlayerScreen is the entry point
            }
        }
        Screen.Gallery -> SubScreenScaffold(
            title = currentScreen.title,
            onBack = onBack
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                GalleryScreen() // Assuming GalleryScreen is the entry point
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTopAppBar(title: String, navigationIcon: @Composable () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    todos: List<ToDoItem>,
    onNavigate: (Screen) -> Unit,
    onOpenDrawer: () -> Unit // New parameter for MainScreen
) {
    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "할일 목록",
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) { // Open drawer from MainScreen
                        Icon(Icons.Filled.Menu, contentDescription = "메뉴 열기")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add ToDo")
            }
        }
    ) { padding ->
        if (todos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "할 일이 없습니다. 아래 '+' 버튼을 눌러 새 할 일을 추가해보세요!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
                items(todos) { todo ->
                    Text(text = todo.text, modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(onSave: (String) -> Unit, onBack: () -> Unit) {
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "할일 추가",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("할 일을 입력하세요") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onSave(text)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장")
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
            BaseTopAppBar(
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