package com.hfut.schedule.ui.component.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.xah.uicommon.style.APP_HORIZONTAL_DP

data class CardBottomButton(
    val text : String,
    val show : Boolean = true,
    val clickable :  (() -> Unit)? = null
)

private val CARD_BOTTOM_BUTTON_SIZE = 14.sp

@Composable
fun ColumnScope.BottomTextButtonGroup(buttons : List<CardBottomButton>) {
    PaddingHorizontalDivider()
    LazyRow (modifier = Modifier.align(Alignment.End).padding(horizontal = APP_HORIZONTAL_DP)) {
        items(buttons.size,key = { it }) { index ->
            val bean = buttons[index]
            with(bean) {
                if(show) {
                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                    Text(
                        text = text,
                        color =
                            if(clickable == null)
                                MaterialTheme.colorScheme.onSurface
                            else {
                                if(text.contains("删除")) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            },
                        fontSize = CARD_BOTTOM_BUTTON_SIZE,
                        modifier = Modifier
                            .padding(vertical = APP_HORIZONTAL_DP - 5.dp)
                            .let {
                                clickable?.let { click ->
                                    it.clickable { click() }
                                } ?: it
                            }
                    )
                }
            }
        }
    }
}
