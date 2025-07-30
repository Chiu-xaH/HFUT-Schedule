package com.hfut.schedule.ui.screen.home.cube.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.cube.sub.AnimationSetting
import com.hfut.schedule.ui.style.ColumnVertical
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.state.TransitionState
import com.xah.transition.util.TransitionPredictiveBackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun UIScreen(innerPaddings : PaddingValues,navController : NavHostController) {
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController) {
        scale = it
    }
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings).scale(scale).alpha(scale)) {
        Spacer(modifier = Modifier.height(5.dp))

        val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
        val animationList = remember { DataStoreManager.AnimationSpeed.entries.sortedBy { it.speed } }

        val currentPureDark by DataStoreManager.pureDarkFlow.collectAsState(initial = false)
        val motionBlur by DataStoreManager.motionBlurFlow.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
        val transition by DataStoreManager.transitionFlow.collectAsState(initial = false)
        val currentColorModeIndex by DataStoreManager.colorModeFlow.collectAsState(initial = DataStoreManager.ColorMode.AUTO.code)
        val animationSpeed by DataStoreManager.animationSpeedType.collectAsState(initial = DataStoreManager.AnimationSpeed.NORMAL.code)

        LaunchedEffect(animationSpeed) {
            AppAnimationManager.updateAnimationSpeed()
        }
        LaunchedEffect(transition) {
            TransitionState.transitionBackgroundStyle.forceTransition = transition
        }
        val cor = rememberCoroutineScope()

        DividerTextExpandedWith("色彩") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "层级实时模糊") },
                    supportingContent = {
                        Text(text = "开启后将会转换部分层级渲染为实时模糊,此过程会加大性能压力" )
                    },
                    leadingContent = { Icon(painterResource(R.drawable.deblur), contentDescription = "Localized description",) },
                    trailingContent = {  Switch(checked = blur, onCheckedChange = { cor.launch {  DataStoreManager.saveHazeBlur(!blur) } } ) },
                    modifier = Modifier.clickable {
                        cor.launch {  DataStoreManager.saveHazeBlur(!blur) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "纯黑深色背景") },
                    supportingContent = { Text(text = "OLED屏使用此模式在深色模式时可获得不发光的纯黑背景") },
                    leadingContent = { Icon(painterResource(R.drawable.contrast), contentDescription = "Localized description",) },
                    trailingContent = {
                        Switch(checked = currentPureDark, onCheckedChange = { cor.launch { DataStoreManager.savePureDark(!currentPureDark) } })
                    },
                    modifier = Modifier.clickable {
                        cor.launch { DataStoreManager.savePureDark(!currentPureDark) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "深浅色") },
                    supportingContent = {
                        Row {
                            FilterChip(
                                onClick = {
                                    cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.LIGHT) }
                                },
                                label = { Text(text = "浅色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.LIGHT.code
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.DARK) }
                                },
                                label = { Text(text = "深色") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.DARK.code
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    cor.launch { DataStoreManager.saveColorMode(DataStoreManager.ColorMode.AUTO) }
                                },
                                label = { Text(text = "跟随系统") }, selected = currentColorModeIndex == DataStoreManager.ColorMode.AUTO.code
                            )
                        }
                    },
                    leadingContent = { Icon(painterResource(
                        when(currentColorModeIndex) {
                            DataStoreManager.ColorMode.DARK.code -> R.drawable.dark_mode
                            DataStoreManager.ColorMode.LIGHT.code -> R.drawable.light_mode
                            else -> R.drawable.routine
                        }
                    ), contentDescription = "Localized description",) },
                )
            }

        }

        DividerTextExpandedWith("动效") {
//            StyleCardListItem(
//                headlineContent = { Text("新转场动效下的新顶栏 提示") },
//                leadingContent = {
//                    Icon(painterResource(R.drawable.info),null)
//                },
//                supportingContent = {
//                    ColumnVertical {
//                        Text("标题左侧按钮始终为返回上一级界面，即使图标不为叉号")
//                        TopAppBar(
//                            title = { Text("标题") },
//                            navigationIcon = {
//                                TopBarTopIconTip(R.drawable.article)
//                            },
//                            actions = {
//                                Row {
//                                    IconButton(onClick = {
//                                        showToast("搜索")
//                                    }) {
//                                        Icon(painter = painterResource(id = R.drawable.search), contentDescription = "")
//                                    }
//                                }
//                            }
//                        )
//                    }
//                }
//            )
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "全局动画速率") },
                    leadingContent = { Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",) },
                )
                RowHorizontal {
                    for(i in animationList.indices) {
                        val item = animationList[i]
                        FilterChip(
                            onClick = {
                                cor.launch { DataStoreManager.saveAnimationSpeed(item) }
                            },
                            label = {
                                ColumnVertical {
                                    Spacer(Modifier.height(CARD_NORMAL_DP))
                                    Text(text = item.title)
                                    Text(text = "${item.speed}ms")
                                    Spacer(Modifier.height(CARD_NORMAL_DP))
                                }
                            },
                            selected = animationSpeed == item.code
                        )
                        if(i != animationList.size-1) {
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                    }
                }
                val speed = animationList.find { it.code == animationSpeed }?.speed

                speed?.let {
                    if(it == 0) return@let
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    LoopingRectangleCenteredTrail2(it)
                    Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text(text = "运动模糊") },
                        supportingContent = {
                            if(AppVersion.CAN_MOTION_BLUR) {
                                Text(text = "部分UI的运动会伴随实时模糊效果,此过程会加大动画过程的压力")
                            } else {
                                Text(text = "需为 Android 12+")
                            }
                        },
                        leadingContent = { Icon(painterResource(R.drawable.motion_mode), contentDescription = "Localized description",) },
                        trailingContent = {  Switch(checked = motionBlur, onCheckedChange = { cor.launch { DataStoreManager.saveMotionBlur(!motionBlur) } },enabled = AppVersion.CAN_MOTION_BLUR) },
                        modifier = Modifier.clickable {
                            if(AppVersion.CAN_MOTION_BLUR) {
                                cor.launch { DataStoreManager.saveMotionBlur(!motionBlur) }
                            }
                        }
                    )
                    PaddingHorizontalDivider()

                    TransplantListItem(
                        headlineContent = { Text(text = "增强转场动画") },
                        supportingContent = {
                            Text(text = "转场动画伴随较高强度的模糊、缩放、压暗、回弹等效果，可能会在某些设备上掉帧")
                        },
                        leadingContent = { Icon(painterResource(R.drawable.transition_fade), contentDescription = "Localized description",) },
                        trailingContent = {  Switch(checked = transition, onCheckedChange = { cor.launch { DataStoreManager.saveTransition(!transition) } }) },
                        modifier = Modifier.clickable { cor.launch { DataStoreManager.saveTransition(!transition) } }
                    )
                    PaddingHorizontalDivider()

                    TransplantListItem(
                        headlineContent = { Text(text = "底栏转场动画") },
                        supportingContent = {
                            Text("自定义底栏切换页面进行转场的动画")
                        },
                        leadingContent = { Icon(painterResource(R.drawable.animation), contentDescription = "Localized description",) },
                    )
                    AnimationSetting(it)
                }
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
//        DividerTextExpandedWith("新转场动画下的界面引导") {
//            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                // 下拉刷新
                // 左上角返回
                // 右上角操作区
                // 底栏切换
                // 顶栏TabRow滑动
//            }
//        }
        InnerPaddingHeight(innerPaddings,false)
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
private fun LoopingRectangleCenteredTrail2(animationSpeed: Int) {
    if(animationSpeed == 0) return
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp - APP_HORIZONTAL_DP*4
    val boxSize = 65.dp
    val delta = (screenWidth - boxSize) / 2  // 偏移范围：中心左右最多偏移量

    val offsetX = remember { Animatable(-delta.value) } // 从左边开始（相对中心）

    val trailList = remember { mutableStateListOf<Float>() }
    val maxTrailCount = 10

    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.primaryContainer

    // 居中颜色插值（基于正向偏移百分比）
//    val containColor = MaterialTheme.colorScheme.primary
//        remember(offsetX.value) {
//        val fraction = ((offsetX.value + delta.value) / (2 * delta.value)).coerceIn(0f, 1f)
//        lerp(container, primary, fraction)
//    }

    LaunchedEffect(animationSpeed) {
        delay(AppAnimationManager.ANIMATION_SPEED*1L)
        while (true) {
            offsetX.animateTo(
                targetValue = delta.value,
                animationSpec = tween(animationSpeed, easing = FastOutSlowInEasing)
            )
            delay(animationSpeed.toLong())

            offsetX.animateTo(
                targetValue = -delta.value,
                animationSpec = tween(animationSpeed, easing = FastOutSlowInEasing)
            )
            delay(animationSpeed.toLong())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            trailList.add(0, offsetX.value)
            if (trailList.size > maxTrailCount) {
                trailList.removeLast()
            }
            delay(15L)
        }
    }

    // 容器为中心对称坐标系
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(boxSize+APP_HORIZONTAL_DP),
        contentAlignment = Alignment.Center
    ) {
        // 拖影
        trailList.forEachIndexed { index, trail ->
            val alpha = ((maxTrailCount - index).toFloat() / maxTrailCount).coerceIn(0f, 1f)
            val sizeFactor = 1f - (index * 0.015f)

            Box(
                modifier = Modifier
                    .offset { IntOffset(trail.dp.roundToPx(), 0) }
                    .size(boxSize
//                            * sizeFactor
                    )
                    .graphicsLayer {
                        this.alpha = alpha * 0.4f
                    }
                    .clip(MaterialTheme.shapes.medium)
                    .background(lerp(container, primary, alpha))
            )
        }

        // 主体
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.dp.roundToPx(), 0) }
                .size(boxSize)
                .clip(MaterialTheme.shapes.medium)
                .background(primary)
        )
    }
}
