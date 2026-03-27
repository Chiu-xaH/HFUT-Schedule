package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.CourseApiDetailDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.container.component.base.SharedContent
import com.xah.floating.model.Window
import com.xah.floating.util.LocalFloatingController


data class TimeTableSquareWindow(
    val list : List<TimeTableItem>
) : Window() {

    override val key = getSharedKey()

    private fun getSharedKey() : String {
        return if(list.size == 1) {
            val item = list[0]
            val origin = CourseDetailOrigin.CALENDAR_JXGLSTU.t + "@${item.hashCode()}"
            when (item.type) {
                TimeTableType.COURSE -> {
                    CourseApiDetailDestination(item.name, origin,item.place).key
                }
                TimeTableType.FOCUS -> {
                    AddEventDestination(item.id, origin).key
                }
                TimeTableType.EXAM -> {
                    ExamDestination(origin).key
                }
            }
        } else {
            "multi_${list.hashCode()}"
        }
    }

    @Composable
    override fun Content() {
        val controller = LocalFloatingController.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                key = key,
                isFullScreen = false,
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = APP_HORIZONTAL_DP, horizontal = APP_HORIZONTAL_DP)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.large
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LiquidButton(
                            modifier = Modifier.align(Alignment.TopEnd).padding(APP_HORIZONTAL_DP/2),
                            onClick = {
                                controller.pop()
                            },
                            backdrop = rememberLayerBackdrop(),
                            isCircle = true
                        ) {
                            Icon(painterResource(R.drawable.close),null)
                        }
                    }
                }
            }
        }
    }
}
