package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.nav.destination.AddEventDestination
import com.hfut.schedule.ui.nav.destination.CourseApiDetailDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.screen.home.calendar.common.numToChinese
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.CourseDetailOrigin
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableItem
import com.hfut.schedule.ui.screen.home.calendar.timetable.logic.TimeTableType
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController


data class TimeTableSquareWindow(
    val list : List<TimeTableItem>
) : FloatingWindow() {

    override val key = getSharedKey()

    override val title = text("方格详情")

    private fun getSharedKey() : String {
        return if(list.size == 1) {
            val item = list[0]
            val origin = CourseDetailOrigin.CALENDAR_JXGLSTU.t +  "${item.hashCode()}"
            when (item.type) {
                TimeTableType.COURSE -> CourseApiDetailDestination(item.name, origin,item.place).key
                TimeTableType.FOCUS -> AddEventDestination(item.detail.eventId,CourseDetailOrigin.CALENDAR_JXGLSTU.t).key
                TimeTableType.EXAM -> ExamDestination(origin).key
            }
        } else {
            "multi_${list.hashCode()}"
        }
    }

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val controller = LocalFloatingController.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.largeIncreased,
                key = key,
                contentStrategy = ContentStrategy.Layer(isFloating = true),
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = APP_HORIZONTAL_DP, horizontal = APP_HORIZONTAL_DP)
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    var innerPadding by remember { mutableStateOf(0.dp) }
                    val title = if(list.isNotEmpty()) {
                        val item = list[0]
                        item.detail.date.substringAfter("-") + " 周${numToChinese(item.dayOfWeek)}"
                    } else {
                        title.asString()
                    }
                    Box {
                        LazyColumn {
                            item { Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                            items(list.size,key = { it }) {index ->
                                val item = list[index]
                                val containerColor = when(item.type) {
                                    TimeTableType.COURSE -> MaterialTheme.colorScheme.primaryContainer
                                    TimeTableType.EXAM -> MaterialTheme.colorScheme.errorContainer
                                    TimeTableType.FOCUS -> MaterialTheme.colorScheme.primary
                                }
                                val contentColor = contentColorFor(containerColor)
                                CustomCard(color = containerColor, modifier = Modifier.padding(bottom = CARD_NORMAL_DP)) {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(item.name, color = contentColor)
                                        },
                                        overlineContent = {
                                            Text(item.detail.code ?: item.type.description, color = contentColor)
                                        },
                                        leadingContent = {
                                            Icon(painterResource(item.type.icon),null, tint = contentColor)
                                        },
                                        modifier = Modifier.clickable {
                                            item.detail.code?.let { ClipBoardHelper.copy(it,"已将课程代码复制到剪切板") }
                                        }
                                    )
                                    item.place?.let {
                                        TransplantListItem(
                                            headlineContent = {
                                                Text(it, color = contentColor)
                                            },
                                            overlineContent = {
                                                Text("地点", color = contentColor)
                                            },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.near_me),null, tint = contentColor)
                                            }
                                        )
                                    }
                                    Row {
                                        TransplantListItem(
                                            modifier = Modifier.weight(.5f),
                                            headlineContent = {
                                                ScrollText(item.startTime + "-" + item.endTime, color = contentColor)
                                            },
                                            overlineContent = {
                                                Text("时间", color = contentColor)
                                            },
                                            leadingContent = {
                                                Icon(
                                                    painterResource(R.drawable.schedule),
                                                    null,
                                                    tint = contentColor
                                                )
                                            }
                                        )
                                        item.detail.teacher?.let {
                                            TransplantListItem(
                                                modifier = Modifier.weight(.5f),
                                                headlineContent = {
                                                    ScrollText(it, color = contentColor)
                                                },
                                                overlineContent = {
                                                    Text("教师", color = contentColor)
                                                },
                                                leadingContent = {
                                                    Icon(
                                                        painterResource(R.drawable.person),
                                                        null,
                                                        tint = contentColor
                                                    )
                                                }
                                            )
                                        }
                                    }
                                    item.detail.classes?.let {
                                        TransplantListItem(
                                            headlineContent = {
                                                Text(it, color = contentColor)
                                            },
                                            overlineContent = {
                                                Text("班级", color = contentColor)
                                            },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.group),null, tint = contentColor)
                                            }
                                        )
                                    }
                                }
                            }
                            item { Spacer(Modifier.height(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                        }

                        AutoSizeText(
                            title,
                            innerPadding,
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(vertical = APP_HORIZONTAL_DP/2, horizontal = APP_HORIZONTAL_DP)
                        )
                        LiquidButton(
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(APP_HORIZONTAL_DP/2)
                                    .measureDpSize { _,h -> innerPadding = h }
                            ,
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

