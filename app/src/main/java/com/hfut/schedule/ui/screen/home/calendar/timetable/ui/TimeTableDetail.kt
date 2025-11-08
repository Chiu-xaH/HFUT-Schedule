package com.hfut.schedule.ui.screen.home.calendar.timetable.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.xah.uicommon.style.APP_HORIZONTAL_DP

@Composable
fun TimeTableDetail(
    bean : List<TimeTableItem>
) {
    if(bean.isEmpty()) {
        return
    }
    Column {
        HazeBottomSheetTopBar("详情", isPaddingStatusBar = false)
        LazyColumn {
            items(bean.size,key = { it }) { index ->
                val data = bean[index]
                with(data) {
                    CardListItem(
                        headlineContent = { Text(name) },
                        supportingContent = {
                            place?.let { Text(it) }
                        },
                        overlineContent = {
                            Text("周"+ numToChinese(dayOfWeek) + " " + startTime + " ~ "  +endTime )
                        },
                        trailingContent = {
                            Text(type.description)
                        },
                        leadingContent = {
                            Icon(painterResource(type.icon),null)
                        },
                        modifier = Modifier.clickable {
                            showToast("正在开发")
                        }
                    )
                }
            }
            item {
                Spacer(Modifier
                    .height(APP_HORIZONTAL_DP)
                    .navigationBarsPadding())
            }
        }
    }
}
