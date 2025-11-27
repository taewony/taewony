# Task: Create ToDo App Module

This document outlines the tasks to create a new ToDo application module.

## 1. Create `MainActivity.kt`

Create a `MainActivity.kt` file that displays "Hello Android".

**File Path:** `todo/src/main/java/com/example/todo/MainActivity.kt`

```kotlin
package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTheme {
                Greeting("Android")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoTheme {
        Greeting("Android")
    }
}
```

## 2. Create `ToDoApp.kt`

Create a `ToDoApp.kt` file that implements a ToDo application with the following features:
-   Korean input support.
-   In-memory data repository.
-   Navigation using `navigation3` (backstack-based).

**File Path:** `todo/src/main/java/com/example/todo/ToDoApp.kt`

```kotlin
package com.example.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// --- Data Layer ---
data class ToDoItem(val id: Int, val text: String)

// --- UI Layer ---
sealed class Screen(val route: String, val title: String) {
    object Main : Screen("main", "ToDo 리스트")
    object AddTodo : Screen("add", "ToDo 추가")
}

@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    val todos = remember { mutableStateListOf<ToDoItem>() }
    val backstack = remember { mutableStateListOf<Screen>(Screen.Main) }
    val currentScreen = backstack.last()

    NavDisplay(
        currentScreen = currentScreen,
        onNavigate = { screen -> backstack.add(screen) },
        onBack = { backstack.removeLastOrNull() },
        todos = todos,
        onSaveTodo = { todoText ->
            todos.add(ToDoItem(id = todos.size + 1, text = todoText))
            backstack.removeLastOrNull()
        }
    )
}

@Composable
fun NavDisplay(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    todos: List<ToDoItem>,
    onSaveTodo: (String) -> Unit
) {
    when (currentScreen) {
        Screen.Main -> MainScreen(
            onAddClick = { onNavigate(Screen.AddTodo) },
            todos = todos,
            onNavigate = onNavigate
        )
        Screen.AddTodo -> AddScreen(
            onSave = onSaveTodo,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAddClick: () -> Unit,
    todos: List<ToDoItem>,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ToDo 리스트",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add ToDo")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(todos) { todo ->
                Text(text = todo.text, modifier = Modifier.padding(vertical = 8.dp))
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
            TopAppBar(
                title = {
                    Text(
                        "ToDo App",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
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
```

Update `MainActivity.kt` to launch `ToDoApp`.

```kotlin
package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.todo.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoTheme {
                ToDoApp()
            }
        }
    }
}
```

## 3. `build.gradle.kts` dependencies

Ensure the `build.gradle.kts` for the new module includes the necessary dependencies.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.todo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.todo"
        minSdk = 32
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
}
```
