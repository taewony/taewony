package com.example.todo_revised.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo_revised.data.local.TodoItem
import com.example.todo_revised.ui.theme.ComposeLabTheme


@Composable
fun AddScreenContent(onSave: (item: TodoItem) -> Unit, modifier: Modifier = Modifier) {
    var todoText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
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
            onValueChange = { todoText = it },  // 입력값 필터링 없이 그대로 저장
            label = { Text("할 일을 입력하세요") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false, // 한글 조합 입력 깨짐 방지
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Button(
            onClick = {
                if (todoText.isNotBlank()) {
                    onSave(TodoItem(task = todoText))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddScreenPreview() {
    ComposeLabTheme {
        AddScreenContent(onSave = {})
    }
}

