package com.example.app_17_todo_revised.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_17_todo_revised.data.settings.SettingsRepository
import com.example.app_17_todo_revised.data.settings.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * SettingsRepository를 사용하여 UI와 데이터를 연결하는 ViewModel
 *
 * ### 주요 특징
 * - **Clean Architecture / MVVM 패턴**
 *   - ViewModel + Repository 구조를 사용해 **UI 로직**과 **데이터 접근(DataStore)**을 분리
 *   - 테스트 용이성과 코드 재사용성이 향상됨
 *
 * - **AndroidViewModel**
 *   - Application Context를 안전하게 전달받아, Repository에 Context가 필요한 경우 활용
 *   - UI(Context)를 직접 참조하지 않고, DataStore 접근은 Repository에서 처리
 *
 * - **데이터 흐름**
 *   - Repository → Flow → ViewModel(StateFlow) → Compose UI
 *   - Compose UI는 collectAsState()를 통해 상태(StateFlow)를 자동으로 구독하고 화면을 갱신
 */
class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    // DataStore 접근 로직을 Repository로 위임
    private val settingsRepository = SettingsRepository(application)

    // --- UI에서 바로 사용할 수 있는 StateFlow (현재 정렬 상태) ---
    private val _sortOrder = MutableStateFlow(SortOrder.TIME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    init {
        // Repository에서 제공하는 Flow를 구독하여 _sortOrder 값을 계속 업데이트
        viewModelScope.launch {
            settingsRepository.sortOrderFlow
                .collect { order ->
                    _sortOrder.value = order
                }
        }
    }

    /**
     * UI에서 정렬 순서를 변경할 때 호출
     * - DataStore에 저장 → Flow를 통해 자동 반영 → UI 즉시 갱신
     */
    fun updateSortOrder(order: SortOrder) {
        viewModelScope.launch {
            settingsRepository.updateSortOrder(order)
        }
    }
}

/* SettingsRepository를 사용하여 UI와 데이터를 연결할 ViewModel을 만듭니다.
 현대적 패턴 Clean Architecture / MVVM → (ViewModel + Repository)
 테스트 용이, 책임 분리, Hilt DI 및 Navigation3와 자연스러운 결합
 DataStore 접근 로직은 Repository에서 처리. ViewModel은 Context를 몰라도 됨 → SettingsRepository가 대신 관리
 코드 설명:
    - settingsRepository.softOrderFlow → StateFlow 변환:
      Repository에서 Flow로 제공되는 DataStore 값을 collect하여 _sortOrder StateFlow에 반영
      Compose UI에서는 collectAsState()로 쉽게 바인딩 가능
    - updateSortOrder(): 정렬 순서 변경 시 DataStore에 바로 저장 → 변경 즉시 Flow를 통해 UI가 업데이트됨
    - Context는 사용하지 않음: ViewModel은 DataStore나 Context에 직접 접근하지 않고 Repository를 통해서만 접근

class PreferenceViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    // --- UI에서 바로 사용하기 위한 StateFlow ---
    private val _sortOrder = MutableStateFlow(SortOrder.TIME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    init {
        // Repository에서 Flow를 구독하여 StateFlow로 업데이트
        viewModelScope.launch {
            settingsRepository.sortOrderFlow
                .collect { order ->
                    _sortOrder.value = order
                }
        }
    }

    // --- UI에서 정렬 순서를 변경할 때 호출하는 메서드 ---
    fun updateSortOrder(order: SortOrder) {
        viewModelScope.launch {
            settingsRepository.updateSortOrder(order)
        }
    }
}
*/
