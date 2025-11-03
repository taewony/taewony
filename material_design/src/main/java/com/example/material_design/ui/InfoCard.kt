package com.example.material_design.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.material_design.ui.theme.AppSpacing
import com.example.material_design.ui.theme.ComposeLabTheme

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

/**
 * CardInfo 데이터를 표시하는 재사용 가능한 카드 컴포넌트.
 *
 * @param modifier 이 컴포저블에 적용할 Modifier.
 * @param cardInfo 카드에 표시할 데이터 (제목, 내용, 선택적 배경색).
 */
@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    cardInfo: CardInfo
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            // [테마 정비] 1. 색상 시스템 적용
            // 카드 배경색은 CardInfo에 지정된 색을 우선 사용합니다.
            // 지정된 색이 없다면, 일반 Surface보다 약간 더 높은 톤의 배경인 `surfaceContainerHigh`를 사용해 입체감을 줍니다.
            containerColor = cardInfo.color ?: MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    ) {
        // [테마 정비] 3. 간격(Spacing) 규칙 적용
        // 카드 내부의 전체적인 여백은 16.dp로 설정하여 충분한 공간을 확보합니다.
        Column(
            modifier = Modifier.padding(AppSpacing.md)
        ) {
            // [테마 정비] 2. 타이포그래피 시스템 적용
            // 카드의 제목은 `titleMedium` 스타일을 사용하여 명확하게 전달합니다.
            Text(
                text = cardInfo.title,
                style = MaterialTheme.typography.titleMedium,
                // 제목과 본문 사이의 간격은 4.dp로 설정합니다.
                modifier = Modifier.padding(bottom = AppSpacing.xxs)
            )
            // 카드의 본문은 `bodyMedium` 스타일을 사용하여 편안하게 읽을 수 있도록 합니다.
            Text(
                text = cardInfo.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, name = "Default InfoCard")
@Composable
fun InfoCardPreview() {
    ComposeLabTheme {
        InfoCard(
            modifier = Modifier.padding(16.dp),
            cardInfo = CardInfo(
                title = "Standard Card Title",
                content = "This is some example content for the card to demonstrate how it looks with default styling."
            )
        )
    }
}

@Preview(showBackground = true, name = "Colored InfoCard")
@Composable
fun InfoCardWithColorPreview() {
    ComposeLabTheme {
        InfoCard(
            modifier = Modifier.padding(16.dp),
            cardInfo = CardInfo(
                title = "Colored Card Title",
                content = "This card has a custom background color provided via the CardInfo data class.",
                color = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
