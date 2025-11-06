package com.example.app_18_fake_store.store.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_18_fake_store.util.EventBus
import kotlinx.coroutines.launch

fun ViewModel.sendEvent(event: Any) {
    viewModelScope.launch {
        EventBus.sendEvent(event)
    }
}