package com.hfut.schedule.ui.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.utils.style.RowHorizontal
import kotlinx.coroutines.launch

@Composable
fun CustomTabRow(pagerState: PagerState, titles: List<String>) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.background(Color.Transparent).padding(horizontal = 10.dp)) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent,
            contentColor = TabRowDefaults.primaryContentColor,
            indicator = {},
            divider = {  }) {
            titles.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
                    text = {
                        Text(
                            text = title,
                            style = TextStyle(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        )
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .background(
                            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                            shape = RoundedCornerShape(appHorizontalDp())
                        ),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}
