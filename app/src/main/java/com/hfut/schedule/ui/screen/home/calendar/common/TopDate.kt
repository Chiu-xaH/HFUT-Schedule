package com.hfut.schedule.ui.screen.home.calendar.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.style.CalendarStyle
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.shader.smallStyle
import com.xah.mirror.util.ShaderState
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import java.time.LocalDate

@Composable
fun ScheduleTopDate(
    showAll: Boolean,
    today : LocalDate,
    shaderState: ShaderState,
) {
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
    val todayDate = DateTimeManager.Date_yyyy_MM_dd
    val customBackgroundAlpha by DataStoreManager.customCalendarSquareAlpha.collectAsState(initial = 1f)
    val style = CalendarStyle(showAll)
    val size = style.rowCount
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    Column(modifier = Modifier.background(Color.Transparent)) {
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*0))
        LazyVerticalGrid(columns = GridCells.Fixed(size),modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-if (showAll) 1.75.dp else 2.5.dp)){
            items(size) { item ->

                val date = mondayOfCurrentWeek.plusDays(item.toLong()).toString() //YYYY-MM-DD 与考试对比
                val isToday = date == todayDate

                val fontSize = if (showAll) 12f else 14f

                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(end = if(item ==size-1) 0.dp else style.everyPadding)
                        .clip(CircleShape)
                        .glassLayer(
                            shaderState,
                            smallStyle.copy(
                                blur = 2.dp,
                                overlayColor = MaterialTheme.colorScheme.surface.copy(customBackgroundAlpha)
                            ),
                            enableLiquidGlass
                        )
                    ,
                    color = Color.Transparent
                ) {
                    Text(
                        textDecoration = if (isToday) TextDecoration.Underline else TextDecoration.None,
                        text = date.substringAfter("-"),
                        textAlign = TextAlign.Center,
                        fontSize = fontSize.sp,
                        modifier = Modifier.padding(horizontal = CARD_NORMAL_DP)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2))
    }
}

@Composable
fun ScheduleTopDate(
    showAll: Boolean,
    today : LocalDate,
) {
    val mondayOfCurrentWeek = today.minusDays(today.dayOfWeek.value - 1L)
    val todayDate = DateTimeManager.Date_yyyy_MM_dd

    Column(modifier = Modifier.background(Color.Transparent)) {
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*0))
        LazyVerticalGrid(columns = GridCells.Fixed(if(showAll)7 else 5),modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-if (showAll) 1.75.dp else 2.5.dp)){
            items(if(showAll)7 else 5) { item ->

                val date = mondayOfCurrentWeek.plusDays(item.toLong()).toString() //YYYY-MM-DD 与考试对比
                val isToday = date == todayDate

                var animated by remember { mutableStateOf(false) }
                val fontSize = if (showAll) 12f else 14f
                LaunchedEffect(isToday) {
                    if (isToday) {
                        animated = true
                    }
                }
                val scale by animateFloatAsState(
                    targetValue = if (isToday && animated) 1.25f else 1f,
                    animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED),
                    label = "scaleAnimation",
                    finishedListener = { if (isToday) animated = false }
                )

                Box(
                    modifier = Modifier
                        .scale(scale), // 👈 缩放围绕中心
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.substringAfter("-"),
                        textAlign = TextAlign.Center,
                        textDecoration = if (isToday) TextDecoration.Underline else TextDecoration.None,
                        fontSize = fontSize.sp,
                        style = TextStyle(
                            shadow = Shadow(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
                                offset = Offset(0f, 0f),
                                blurRadius = 10f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(CARD_NORMAL_DP*2))
    }
}
