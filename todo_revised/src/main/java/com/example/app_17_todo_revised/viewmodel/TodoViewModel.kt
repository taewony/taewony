package com.example.app_17_todo_revised.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_17_todo_revised.data.local.AppDatabase
import com.example.app_17_todo_revised.data.local.TodoItem
import com.example.app_17_todo_revised.data.settings.SettingsRepository
import com.example.app_17_todo_revised.data.settings.SortOrder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    // --- ✅ 1. PreferenceViewModel에서 가져온 코드 ---
    private val todoDao = AppDatabase.getDatabase(application).todoDao()
    private val settingsRepository = SettingsRepository(application)
    val sortOrder: StateFlow<SortOrder> = settingsRepository.sortOrderFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortOrder.TIME
        )

    fun updateSortOrder(newSortOrder: SortOrder) {
        viewModelScope.launch {
            settingsRepository.updateSortOrder(newSortOrder)
        }
    }

    // ✅ 2. todoItems가 sortOrder의 변경을 감지하여 쿼리를 바꾸도록 수정합니다.
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todoItems: StateFlow<List<TodoItem>> = settingsRepository.sortOrderFlow
        .flatMapLatest { sortOrder ->
            // sortOrderFlow에서 새로운 값이 올 때마다,
            // 아래 when 문에 따라 적절한 DB 쿼리(Flow)로 전환합니다.
            when (sortOrder) {
                SortOrder.TIME -> todoDao.getTodosSortedByTime()
                SortOrder.TASK_NAME -> todoDao.getTodosSortedByName()
            }
        }.stateIn( // DB에서 가져온 Flow를 UI가 사용할 StateFlow로 변환
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTodo(task: String) = viewModelScope.launch {
        if (task.isNotBlank()) {
            todoDao.insert(TodoItem(task = task))
        }
    }

    fun updateTodo(item: TodoItem) = viewModelScope.launch {
        todoDao.update(item)
    }

    fun deleteTodo(item: TodoItem) = viewModelScope.launch {
        todoDao.delete(item)
    }
}
