package com.example.app_17_todo_revised.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 인스턴스 생성
// dataStore라는 이름을 프로퍼티 이름으로 정해야 하고, DataStore<Preferences> 타입을 명시해야 합니다.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
enum class SortOrder { TIME, TASK_NAME }

class SettingsRepository(private val context: Context) {
    private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")

    // 정렬 순서 값을 Flow로 노출
    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data
        .map { preferences ->
            // 저장된 값이 없으면, TIME을 기본값으로 사용
            SortOrder.valueOf(preferences[SORT_ORDER_KEY] ?: SortOrder.TIME.name)
        }

    // 정렬 순서 값을 저장하는 함수
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = sortOrder.name
        }
    }
}