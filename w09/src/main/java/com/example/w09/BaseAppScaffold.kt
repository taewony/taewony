package com.example.w09

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

/**
 * BaseAppScaffold:
 * CenterAlignedTopAppBar + Scaffold 기본 구조를 제공하는 공통 컴포저블.
 *
 * 기본 색상은 MaterialTheme.colorScheme.primaryContainer/onPrimaryContainer을 사용.
 * topBar 제목과 본문 내용을 각각 title, content 슬롯으로 주입.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseAppScaffold(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = navigationIcon ?: {},
                actions = actions ?: {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        content = content
    )
}
