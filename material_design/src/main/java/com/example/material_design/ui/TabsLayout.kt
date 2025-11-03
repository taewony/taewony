package com.example.material_design.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material_design.ui.theme.AppSpacing
import com.example.material_design.ui.theme.ComposeLabTheme
import kotlinx.coroutines.launch

/**
 * TabRow와 HorizontalPager를 결합하여 재사용 가능한 탭 레이아웃을 만듭니다.
 *
 * @param modifier 이 컴포저블에 적용할 Modifier.
 * @param tabs 탭에 표시할 제목 목록.
 * @param pageContent 각 페이지에 표시할 콘텐츠를 제공하는 람다. 페이지 인덱스를 전달받습니다.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabsLayout(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    pageContent: @Composable (page: Int) -> Unit
) {
    // Pager의 상태를 기억하고 관리합니다.
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. 탭 행 (TabRow)
        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // 탭 클릭 시 해당 페이지로 부드럽게 스크롤
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. 페이지 콘텐츠 (HorizontalPager)
        HorizontalPager(
            state = pagerState,
        ) { page ->
            pageContent(page)
        }
    }
}

@Preview(showBackground = true, name = "TabsLayout Preview")
@Composable
fun TabsLayoutPreview() {
    ComposeLabTheme {
        TabsLayout(
            tabs = listOf("Music", "Movies", "Books"),
            pageContent = { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Content for page: ${page + 1}")
                }
            }
        )
    }
}
