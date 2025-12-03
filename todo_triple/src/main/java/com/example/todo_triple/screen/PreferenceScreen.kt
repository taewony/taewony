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
import com.example.todo_triple.viewmodel.PreferenceViewModel

@OptIn(ExperimentalMaterial3Api::class)
// [앱 실행 흐름 1] @Composable 진입점
// PreferenceScreen Composable이 호출되면서 Compose 컴포지션이 시작됩니다.
// 사용자가 설정 화면으로 이동할 때 이 Composable이 화면에 표시됩니다.
@Composable
fun PreferenceScreen(
    onNavigateBack: () -> Unit,
    // 매개변수인 viewModel: PreferenceViewModel = viewModel()을 통해 ViewModel 인스턴스가
    // 생성되거나 기존 인스턴스가 획득됩니다. 이 ViewModel은 UI의 상태를 관리하고 비즈니스 로직을 처리합니다.
    viewModel: PreferenceViewModel = viewModel()
) {
    // [앱 실행 흐름 4-4] Compose UI 자동 재구성 (Recomposition)
    // viewModel.sortOrder StateFlow의 값이 변경되면, `collectAsState()`가 새로운 상태를 반환합니다.
    // Compose 런타임은 이 상태 변화를 감지하고, `currentSortOrder`를 사용하는 모든 Composable을
    // 자동으로 다시 그립니다(recomposition). 이 경우, 라디오 버튼의 선택 상태가 업데이트됩니다.
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
                // [앱 실행 흐름 4-1] 사용자 액션 처리 및 UI 상태 업데이트
                // 사용자가 UI에서 다른 정렬 옵션(라디오 버튼)을 선택하면,
                // 이 Row의 selectable Modifier에 설정된 onClick 람다가 실행됩니다.
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (currentSortOrder == sortOrder),
                            // viewModel.updateSortOrder(sortOrder)를 호출하여 사용자의 선택을 ViewModel에 알립니다.
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