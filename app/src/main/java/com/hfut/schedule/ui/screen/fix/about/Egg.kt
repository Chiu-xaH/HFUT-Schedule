package com.hfut.schedule.ui.screen.fix.about

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.style.padding.InnerPaddingHeight

import com.hfut.schedule.ui.style.align.RowHorizontal
import kotlin.math.roundToInt

@Composable
fun Egg() {
    val period = .25f

    var dampingRatio by remember { mutableFloatStateOf(Spring.DampingRatioMediumBouncy) }
    var stiffness by remember { mutableFloatStateOf(Spring.StiffnessHigh) }
    var num by remember { mutableIntStateOf(1) }
    Box() {
        Party()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HazeBottomSheetTopBar("彩蛋") {
                    Row  {
                        FilledTonalIconButton (
                            onClick = {
                                num++
                            }
                        ) {
                            Icon(painterResource(R.drawable.add),null)
                        }
                        if(num >= 2) {
                            FilledTonalIconButton (
                                onClick = {
                                    num--

                                }
                            ) {
                                Icon(painterResource(R.drawable.close),null)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column(modifier = Modifier.padding(APP_HORIZONTAL_DP)) {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                dampingRatio += period
                            },
                            modifier = Modifier
                                .weight(.5f)
                                .fillMaxWidth()
                        ) {
                            Text("阻尼++")
                        }
                        if(dampingRatio >= period) {
                            Spacer(Modifier.width(APP_HORIZONTAL_DP))
                            Button(
                                onClick = {
                                    dampingRatio -= period
                                },
                                modifier = Modifier
                                    .weight(.5f)
                                    .fillMaxWidth()
                            ) {
                                Text("阻尼--")
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                stiffness += period
                            },
                            modifier = Modifier
                                .weight(.5f)
                                .fillMaxWidth()
                        ) {
                            Text("刚度++")
                        }
                        if(stiffness >= period) {
                            Spacer(Modifier.width(APP_HORIZONTAL_DP))
                            Button(
                                onClick = {
                                    stiffness -= period
                                },
                                modifier = Modifier
                                    .weight(.5f)
                                    .fillMaxWidth()
                            ) {
                                Text("刚度--")
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                InnerPaddingHeight(innerPadding,true)
                for(i in 0 until num) {
                    RowHorizontal {
                        DraggableImage(R.drawable.hfut,dampingRatio, stiffness)
                        Spacer(Modifier.width(10.dp))
                        DraggableImage(R.drawable.hfut_dynamic,dampingRatio, stiffness)
                    }
                    Spacer(Modifier.height(10.dp))
                }
                for(i in 0 until num) {
                    RowHorizontal {
                        DraggableImage(R.drawable.buliding,dampingRatio, stiffness)
                    }
                    RowHorizontal {
                        DraggableImage(R.drawable.buliding_dynamic,dampingRatio, stiffness)
                    }
                }
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }

}

@Composable
private fun DraggableImage(painterResId: Int,dampingRatio : Float = Spring.DampingRatioMediumBouncy,stiffness : Float = Spring.StiffnessHigh) {
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    // 动画，控制偏移量（拖动效果）
    val springOffset by animateOffsetAsState(
        targetValue = offset , // 回弹的偏移加到拖动偏移
        animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness),
        label = ""
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(springOffset.x.roundToInt(), springOffset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    offset = Offset(offset.x + dragAmount.x, offset.y + dragAmount.y)
                }
            }
    ) {
        Image(
            painter = painterResource(id = painterResId),
            contentDescription = null,
        )
    }
}
