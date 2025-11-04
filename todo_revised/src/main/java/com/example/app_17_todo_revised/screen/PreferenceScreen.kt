package com.example.app_17_todo_revised.screen

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_17_todo_revised.data.settings.SortOrder
import com.example.app_17_todo_revised.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    // 실제 앱에서는 ViewModelProvider.Factory를 통해 주입하는 것이 좋음
    viewModel: TodoViewModel = viewModel(),
    onNavigateBack: () -> Unit // 1. 뒤로 가기 액션을 위한 콜백 함수 추가
) {
    // ViewModel의 StateFlow를 구독하여 현재 정렬 순서를 가져옴
    val currentSortOrder by viewModel.sortOrder.collectAsState()

    Scaffold(
        topBar = {
            // 2. 뒤로 가기 버튼이 있는 TopAppBar 추가
            TopAppBar(
                title = { Text("앱 설정") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { // 3. 클릭 시 콜백 함수 실행
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        // 4. 메인 콘텐츠를 Scaffold 내부에 배치
        Box(modifier = Modifier.padding(innerPadding)) {
            PreferenceScreenContent(
                currentSortOrder = currentSortOrder,
                onSortOrderChange = { newSortOrder ->
                    viewModel.updateSortOrder(newSortOrder)
                }
            )
        }
    }
}

@Composable
fun PreferenceScreenContent(
    currentSortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit
) {
    val radioOptions = listOf(
        "시간 순" to SortOrder.TIME,
        "이름 순" to SortOrder.TASK_NAME
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Text("정렬 순서", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)

        radioOptions.forEach { (text, sortOrder) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (currentSortOrder == sortOrder),
                        onClick = { onSortOrderChange(sortOrder) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (currentSortOrder == sortOrder),
                    onClick = null // Row의 selectable에서 처리하므로 null
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

@Preview(showBackground = true)
@Composable
fun PreferenceScreenPreview() {
    // Preview에서는 현재 선택된 상태를 직접 지정하여 테스트
    PreferenceScreenContent(
        currentSortOrder = SortOrder.TIME,
        onSortOrderChange = {} // Preview에서는 동작하지 않으므로 비워둠
    )
}

