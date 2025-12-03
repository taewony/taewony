package com.example.todo_triple.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo_triple.data.settings.SortOrder
import com.example.todo_triple.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    onNavigateBack: () -> Unit,
    viewModel: TodoViewModel = viewModel()
) {
    val currentSortOrder by viewModel.sortOrder.collectAsState()

    Scaffold(
        topBar = {
            BaseTopAppBar(
                title = "앱 설정",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text(
                text = "정렬 순서",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            val radioOptions = listOf(
                "시간 순" to SortOrder.TIME,
                "이름 순" to SortOrder.TASK_NAME
            )
            radioOptions.forEach { (text, sortOrder) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (currentSortOrder == sortOrder),
                            onClick = { viewModel.updateSortOrder(sortOrder) }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (currentSortOrder == sortOrder),
                        onClick = null // onClick is handled by the Row's selectable modifier
                    )
                    Text(
                        text = text,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}