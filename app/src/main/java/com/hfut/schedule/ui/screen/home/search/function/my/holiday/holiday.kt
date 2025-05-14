package com.hfut.schedule.ui.screen.home.search.function.my.holiday

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.screen.home.getHolidays
import com.hfut.schedule.ui.style.HazeBottomSheet
import dev.chrisbanes.haze.HazeState

@Composable
fun Holiday(hazeState : HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "法定假日") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.beach_access),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
                showBottomSheet = true
        }
    )

    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("${DateTimeUtils.Date_yyyy}年 国家法定假日")
                },
            ) { innerPadding ->
                Column (
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    HolidayUI()
                }
            }
        }
    }
}


@Composable
fun HolidayUI() {
    val list by remember { mutableStateOf(getHolidays()) }
    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
        items(list.size) { index->
            val item = list[index]
            val isOffDay = item.isOffDay
            SmallCard(
                modifier = Modifier.padding(horizontal = cardNormalDp(), vertical = cardNormalDp()),
                color = if(!isOffDay) MaterialTheme.colorScheme.errorContainer else null
            ) {
                TransplantListItem(
                    headlineContent = { ScrollText(item.name) },
                    overlineContent = { Text(item.date) },
                    trailingContent = { Text( if(isOffDay) "休" else "班" ) },
                )
            }
        }
    }
    BottomTip("数据来源:国务院")
}