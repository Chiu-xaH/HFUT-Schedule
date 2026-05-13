package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.uniapp.ClassroomOccupiedCause
import com.hfut.schedule.logic.model.uniapp.UniAppEmptyClassroomLesson
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.screen.home.search.function.school.classroom.getClassroomSquareContainerColor
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.container.util.NoneRoundShape
import com.xah.floating.util.LocalFloatingController

data class ClassroomSquareWindow(
    val bean : UniAppEmptyClassroomLesson,
    val room : String
) : FloatingWindow() {

    override val key = "classroom_square_${bean.hashCode()}_$room"

    override val title = text(room)


    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val floatingController = LocalFloatingController.current
        val occupyList = remember { ClassroomOccupiedCause.entries }
        val cause = occupyList.find { it.activityType == bean.activityType }

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
                    shape = NoneRoundShape
                ) {
                    var innerPadding by remember { mutableStateOf(0.dp) }
                    Box {
                        LazyColumn {
                            item { Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                            item {
                                val containerColor = getClassroomSquareContainerColor(bean)
                                val contentColor = contentColorFor(containerColor)

                                CustomCard(
                                    color = containerColor,
                                    modifier = Modifier
                                        .padding(bottom = CARD_NORMAL_DP)
                                        .clip(MaterialTheme.shapes.medium)
                                ) {
                                    CompositionLocalProvider(
                                        LocalContentColor provides contentColor
                                    ) {
                                        bean.teacherName?.let { teacherName ->
                                            TransplantListItem(
                                                headlineContent = {
                                                    Text(teacherName, color = contentColor)
                                                },
                                                overlineContent = {
                                                    Text("教师",color = contentColor)
                                                },
                                                leadingContent = {
                                                    Icon(painterResource(R.drawable.person),null, tint = contentColor)
                                                }
                                            )
                                        }
                                        TransplantListItem(
                                            headlineContent = {
                                                Text("${bean.date} ${bean.startTimeString}~${bean.endTimeString}",color = contentColor)
                                            },
                                            overlineContent = {
                                                Text("时间",color = contentColor)
                                            },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.schedule),null, tint = contentColor)
                                            }
                                        )
                                        TransplantListItem(
                                            headlineContent = {
                                                Text(bean.activityName,color = contentColor)
                                            },
                                            overlineContent = {
                                                Text("类型: ${cause?.description ?: bean.activityType}",color = contentColor)
                                            },
                                            leadingContent = {
                                                Icon(painterResource(
                                                    when(cause) {
                                                        ClassroomOccupiedCause.BORROWED -> {
                                                            R.drawable.groups
                                                        }
                                                        ClassroomOccupiedCause.EXAM -> {
                                                            R.drawable.draw
                                                        }
                                                        ClassroomOccupiedCause.IN_LESSON -> {
                                                            R.drawable.calendar
                                                        }
                                                        null -> {
                                                            R.drawable.category
                                                        }
                                                    }
                                                ),null, tint = contentColor)
                                            }
                                        )
                                    }
                                }
                            }
                            item { Spacer(Modifier.height(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                        }

                        AutoSizeText(
                            title.asString(),
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
                                floatingController.pop()
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
