package com.example.todo_triple.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// [앱 실행 흐름 2-1] Repository 생성 및 DataStore 초기화
// `preferencesDataStore` 위임(delegate)을 통해 앱의 "settings"라는 이름의
// DataStore 파일과 연결되는 DataStore<Preferences> 인스턴스가 생성됩니다.
// 이 인스턴스는 앱 컨텍스트 범위에서 싱글톤으로 관리됩니다.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// ViewModel이 생성될 때 이 Repository가 함께 생성됩니다.
class SettingsRepository(private val context: Context) {
    private val SORT_ORDER_KEY = stringPreferencesKey("sort_order")

    // [앱 실행 흐름 3-1] DataStore에서 데이터 읽기 Flow 생성
    // dataStore.data는 Preferences 객체를 발행(emit)하는 Flow<Preferences>입니다.
    // 이 Flow는 DataStore 파일의 데이터가 변경될 때마다 최신 값을 자동으로 발행합니다.
    // .map 연산자를 통해 Preferences 객체에서 "sort_order" 키에 해당하는 값을 읽어
    // SortOrder Enum 타입으로 변환합니다. 저장된 값이 없으면 기본값으로 SortOrder.TIME을 사용합니다.
    val sortOrderFlow: Flow<SortOrder> = context.dataStore.data
        .map { preferences ->
            SortOrder.valueOf(preferences[SORT_ORDER_KEY] ?: SortOrder.TIME.name)
        }

    // [앱 실행 흐름 4-3] DataStore에 새로운 값 저장
    // dataStore.edit { ... } 함수는 트랜잭션 내에서 안전하게 DataStore를 수정합니다.
    // 람다 블록 안에서 preferences[SORT_ORDER_KEY] = sortOrder.name 코드를 통해
    // "sort_order" 키의 값을 새로운 정렬 순서의 이름(문자열)으로 덮어씁니다.
    // 이 작업이 완료되면, 이 DataStore를 구독하고 있던 sortOrderFlow가 자동으로 새 값을 발행하게 됩니다.
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        context.dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = sortOrder.name
        }
    }
}