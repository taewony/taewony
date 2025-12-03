package com.example.todo_triple.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo_triple.data.settings.SettingsRepository
import com.example.todo_triple.data.settings.SortOrder
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// [앱 실행 흐름 2] ViewModel 생성 및 초기화
// PreferenceScreen에서 viewModel()이 호출될 때 이 ViewModel이 생성됩니다.
// 생성자에서 SettingsRepository 인스턴스를 초기화하며, 이 과정에서 DataStore도 준비됩니다.
// AndroidViewModel(application)을 상속받으므로, 생성자에서 Application 컨텍스트에 접근할 수 있습니다.
class PreferenceViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    // [앱 실행 흐름 3] DataStore Flow 구독 및 StateFlow 생성
    // ViewModel이 생성될 때, settingsRepository의 sortOrderFlow(DataStore의 데이터를 읽는 Flow)를 구독합니다.
    // .stateIn() 연산자는 이 Flow를 viewModelScope 내에서 StateFlow로 변환합니다.
    // 이 StateFlow는 UI가 구독할 수 있는 상태 홀더 역할을 하며, 마지막으로 발행된 값을 보유합니다.
    // SharingStarted.WhileSubscribed(5000)는 구독자가 있을 때만 Flow를 활성화하여 리소스를 효율적으로 사용합니다.
    val sortOrder: StateFlow<SortOrder> = settingsRepository.sortOrderFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SortOrder.TIME
        )

    // [앱 실행 흐름 4-2] ViewModel에서 데이터 업데이트 요청
    // UI로부터 호출된 이 함수는 viewModelScope에서 코루틴을 실행합니다.
    // settingsRepository.updateSortOrder(newSortOrder)를 호출하여
    // 데이터 계층(Repository)에 정렬 순서 변경을 요청합니다.
    fun updateSortOrder(newSortOrder: SortOrder) {
        viewModelScope.launch {
            settingsRepository.updateSortOrder(newSortOrder)
        }
    }
}
