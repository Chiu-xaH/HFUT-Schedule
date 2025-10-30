package com.hfut.schedule.ui.style

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.xah.uicommon.style.APP_HORIZONTAL_DP


data class CalendarStyle(val showAll: Boolean) {
    val everyPadding = if (showAll) 1.dp else 1.75.dp
    val textSize = if (showAll) 12.sp else 14.sp
    val rowCount = if (showAll) 7 else 5
    val columnCount = 6

    val containerColor @Composable
    get() = MaterialTheme.colorScheme.surfaceContainer

    val containerCorner @Composable
    get() = MaterialTheme.shapes.extraSmall

    val calendarPaddingHorizontal = APP_HORIZONTAL_DP  - everyPadding * 2

    fun calendarPadding() = Modifier.padding(
        horizontal = calendarPaddingHorizontal,
        vertical = everyPadding
    )
}



