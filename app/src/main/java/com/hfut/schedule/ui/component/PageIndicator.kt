package com.hfut.schedule.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import kotlinx.coroutines.launch

@Composable
fun PageIndicator(
    pageState : PagerState,
    modifier: Modifier = Modifier,
    dotSize: Dp = CARD_NORMAL_DP*2,
    dotSpacing: Dp = CARD_NORMAL_DP*2,
    activeColor: Color = MaterialTheme.colorScheme.surface,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing)
    ) {
        repeat(pageState.pageCount) { index ->
            val color = if (index == pageState.currentPage) activeColor else inactiveColor
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(color = color, shape = CircleShape)
                    .clickable {
                        scope.launch {
                            pageState.scrollToPage(index)
                        }
                    }
            )
        }
    }
}