package com.example.material_design.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.material_design.ui.theme.ComposeLabTheme

/**
 * 앱에서 공통으로 사용할 확장된 플로팅 액션 버튼(Extended FAB).
 *
 * @param icon FAB에 표시될 아이콘.
 * @param text FAB에 표시될 텍스트.
 * @param onClick 버튼이 클릭되었을 때 호출될 람다.
 */
@Composable
fun AppFab(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = text) },
        text = { Text(text = text) }
    )
}

@Preview(showBackground = true, name = "AppFab Preview")
@Composable
fun AppFabPreview() {
    ComposeLabTheme {
        AppFab(
            icon = Icons.Default.Add,
            text = "New Item",
            onClick = {}
        )
    }
}
