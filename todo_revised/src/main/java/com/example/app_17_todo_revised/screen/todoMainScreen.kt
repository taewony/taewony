package com.example.app_17_todo_revised.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app_17_todo_revised.data.local.TodoItem
import com.example.app_17_todo_revised.data.settings.SortOrder
import com.example.app_17_todo_revised.ui.theme.ComposeLabTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TodoItem(item: TodoItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = item.task, // task 내용 표시
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "생성: ${formatTime(item.createdAt)}", // 생성 시각 표시
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    items: List<TodoItem>,
    sortOrder: SortOrder // ✅ sortOrder 파라미터 추가
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
	) {
		TodoListSection(items = items, sortOrder = sortOrder)
	}
}

@Composable
fun TodoListSection(
    items: List<TodoItem>,
    sortOrder: SortOrder // 1. 정렬 순서를 받는 파라미터 추가
) {
    // 2. 정렬 순서에 따라 리스트를 정렬
    val sortedItems = when (sortOrder) {
        SortOrder.TIME -> items.sortedByDescending { it.createdAt }
        SortOrder.TASK_NAME -> items.sortedBy { it.task }
    }
    if (items.isEmpty()) {
        Text(
            text = "할 일이 없습니다. 새로운 할 일을 추가해보세요!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedItems, key = { it.id }) { item -> // 고유 id를 key로 사용
                TodoItem(item = item)
            }
        }
    }
}
// 시간 포맷을 위한 헬퍼 함수
private fun formatTime(timeInMillis: Long): String {
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return format.format(timeInMillis)
}
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ComposeLabTheme {
        val sampleItems = listOf(
            TodoItem(task = "Compose 공부하기"),
            TodoItem(task = "운동하기", createdAt = System.currentTimeMillis() - 1000 * 60 * 60)
        )
        MainScreenContent(
            items = sampleItems,
            sortOrder = SortOrder.TIME // ✅ Preview를 위한 기본 정렬값 전달
        )
    }
}

