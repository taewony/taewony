
package com.example.material_design.ui

import androidx.compose.ui.graphics.Color

/**
 * 카드 UI에 필요한 데이터를 담는 데이터 클래스.
 *
 * @param title 카드의 제목.
 * @param content 카드의 내용.
 * @param color 카드의 배경색. (선택 사항)
 */
data class CardInfo(
    val title: String,
    val content: String,
    val color: Color? = null
)
