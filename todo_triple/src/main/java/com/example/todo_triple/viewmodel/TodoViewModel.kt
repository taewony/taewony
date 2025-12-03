package com.example.todo_triple.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_triple.data.settings.SettingsRepository
import com.example.todo_triple.data.settings.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

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

    // For now, we'll use a dummy list. This will be replaced by Room later.
    private val _todoItems = MutableStateFlow<List<com.example.todo_triple.ToDoItem>>(emptyList())
    val todoItems: StateFlow<List<com.example.todo_triple.ToDoItem>> = _todoItems.asStateFlow()
}