
package com.example.material_design.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.material_design.ui.theme.ComposeLabTheme

/**
 * 앱의 모든 화면에서 공통으로 사용할 기본 레이아웃 구조.
 * Scaffold를 사용하여 일관된 UI 틀(상단바, 하단바, FAB 등)을 제공합니다.
 *
 * @param topBar 상단 앱 바 Composable.
 * @param bottomBar 하단 네비게이션 바 Composable. (선택 사항)
 * @param fab 플로팅 액션 버튼 Composable. (선택 사항)
 * @param content 화면의 메인 콘텐츠 Composable.
 */
@Composable
fun BaseScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    fab: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit
) {
    // Scaffold는 Material Design의 기본적인 레이아웃 구조를 쉽게 만들게 도와줌
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = fab
    ) { innerPadding ->
        // Surface를 사용하여 배경색과 같은 기본 스타일을 적용
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Scaffold가 계산한 안전 영역(상단바 등)을 제외한 공간
            color = MaterialTheme.colorScheme.background
        ) {
            // content 람다에 Modifier를 전달하여 Surface 내부에 콘텐츠를 배치
            content(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true, name = "BaseScaffold Preview")
@Composable
fun BaseScaffoldPreview() {
    ComposeLabTheme {
        BaseScaffold(
            topBar = {
                AppTopBar(
                    title = "Scaffold Preview",
                    onMenuClick = null // No drawer in this simple preview
                )
            },
            content = { modifier ->
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "This is the main content area.")
                }
            }
        )
    }
}
