package com.example.triple_components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triple_components.ui.theme.TaewonyTheme
import kotlinx.coroutines.launch
import com.example.triple_components.BatteryStatusRoute // import문 추가
import com.example.triple_components.Mp3PlayerScreen
import com.example.triple_components.GalleryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaewonyTheme {
                AppContent()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Main : Screen("main", "Todo List", Icons.Filled.Menu)
    object AddTodo : Screen("add", "Add Todo", Icons.Filled.Add)
    object BatteryStatus : Screen("batteryStatus", "Battery Status", Icons.Default.Info)
    object Mp3Player : Screen("mp3Player", "MP3 Player", Icons.Default.Star)
    object Gallery : Screen("gallery", "Gallery", Icons.Default.Settings)
}

@Composable
fun AppContent() {
    val todos = remember { mutableStateListOf<String>() }
    val backstack = remember { mutableStateListOf<Screen>(Screen.Main) }
    val currentScreen = backstack.last()

    NavDisplay(
        currentScreen = currentScreen,
        onNavigate = { screen -> backstack.add(screen) },
        onBack = { backstack.removeLast() },
        todos = todos,
        onSaveTodo = { todo ->
            todos.add(todo)
            backstack.removeLast()
        }
    )
}

@Composable
fun NavDisplay(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    todos: List<String>,
    onSaveTodo: (String) -> Unit
) {
    when (currentScreen) {
        Screen.Main -> MainScreenContent(
            onAddClick = { onNavigate(Screen.AddTodo) },
            datas = todos,
            onNavigate = onNavigate
        )
        Screen.AddTodo -> AddScreenContent(
            onSave = onSaveTodo,
            onBack = onBack
        )
        Screen.BatteryStatus -> {
            SubScreenScaffold(title = currentScreen.title, onBack = onBack) { innerPadding ->
                BatteryStatusRoute(modifier = Modifier.padding(innerPadding))
            }
        }
        Screen.Mp3Player -> {
            SubScreenScaffold(title = currentScreen.title, onBack = onBack) { innerPadding ->
                Mp3PlayerScreen(modifier = Modifier.padding(innerPadding))
            }
        }
        Screen.Gallery -> {
            SubScreenScaffold(title = currentScreen.title, onBack = onBack) { innerPadding ->
                GalleryScreen(modifier = Modifier.padding(innerPadding))
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
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    onAddClick: () -> Unit,
    datas: List<String>,
    onNavigate: (Screen) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        Screen.BatteryStatus,
        Screen.Mp3Player,
        Screen.Gallery
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 0.7f)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Red),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Navigation Title",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                drawerItems.forEach { screen ->
                    NavigationDrawerItem(
                        label = { Text(screen.title) },
                        icon = { screen.icon?.let { Icon(imageVector = it, contentDescription = screen.title) } },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(screen)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Todo List") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Filled.Add, "Add new todo")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (datas.isEmpty()) {
                    Text(
                        text = "할 일이 없습니다. 새로운 할 일을 추가해보세요!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(datas) { todo ->
                            TodoItem(todo = todo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(todo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.todo),
            contentDescription = "Todo Icon",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = todo,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreenContent(onSave: (String) -> Unit, onBack: () -> Unit) {
    var todoText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Todo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Todo 등록",
                fontSize = 15.sp,
                color = Color.DarkGray
            )
            OutlinedTextField(
                value = todoText,
                onValueChange = { todoText = it },
                label = { Text("할 일을 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Button(
                onClick = {
                    if (todoText.isNotBlank()) {
                        onSave(todoText)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("저장")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TaewonyTheme {
        MainScreenContent(
            onAddClick = { },
            datas = remember { mutableStateListOf("Sample Todo 1", "Sample Todo 2") },
            onNavigate = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddScreenPreview() {
    TaewonyTheme {
        AddScreenContent({}, onBack = {})
    }
}
