package com.example.todo_triple.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todo_triple.ToDoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    // For now, we'll use a dummy list. This will be replaced by Room later.
    private val _todoItems = MutableStateFlow<List<ToDoItem>>(emptyList())
    val todoItems: StateFlow<List<ToDoItem>> = _todoItems.asStateFlow()
}