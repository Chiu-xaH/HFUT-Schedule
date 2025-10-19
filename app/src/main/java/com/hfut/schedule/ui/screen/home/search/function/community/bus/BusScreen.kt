package com.hfut.schedule.ui.screen.home.search.function.community.bus

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

private enum class BusDate(val type : String,val description: String) {
    WORK_DAY("0","周一至周五"),
    SATURDAY("1","周六"),
    SUNDAY("2","周日")
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Bus.route }
    val dates = remember { BusDate.entries }
    var startInput by remember { mutableStateOf("") }
    var endInput by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()

    CustomTransitionScaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        route = route,
        navHostController = navController,
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.Bus.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(
                            navController,
                            route,
                            AppNavRoute.Bus.icon
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = startInput,
                        onValueChange = {
                            startInput = it
                        },
                        label = { Text("起点") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = endInput,
                        onValueChange = {
                            endInput = it
                        },
                        label = { Text("终点") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp-CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val uiState by vm.busResponse.state.collectAsState()
            val refreshNetwork : suspend () -> Unit = m@ {
                if(uiState is UiState.Success) {
                    return@m
                }
                prefs.getString("TOKEN","")?.let {
                    vm.busResponse.clear()
                    vm.getBus(it)
                }
            }
            LaunchedEffect(Unit) {
                refreshNetwork()
            }
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as UiState.Success).data.filter {
                    it.from.contains(startInput) && it.to.contains(endInput)
                }
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size, key = { it }) { index ->
                        val item = list[index]
                        with(item) {
                            val date = dates.find { it.type == type }?.description ?: type
                            val pointList = stop.split("  ").filter { it.isNotEmpty() && it.isNotBlank() }.toMutableList()
                            // 向首部添加from 尾部添加to
                            pointList.add(0, from)
                            pointList.add(to)
                            // 用线路图显示
                            CustomCard(color = cardNormalColor()) {
                                TransplantListItem(
                                    headlineContent = {
                                        Text("$date $time")
                                    },
                                    supportingContent = {
                                        Text("上车地点 : $place")
                                    },
                                    trailingContent = {
                                        Text(count.toString() + "辆")
                                    },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.schedule),null)
                                    },
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = APP_HORIZONTAL_DP)
                                        .padding(bottom = APP_HORIZONTAL_DP),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // 第一行：横线贯穿全宽，圆点平均分布
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box {
                                            val transition = rememberInfiniteTransition(label = "scroll")
                                            var barWidthPx by remember { mutableStateOf(0f) }

                                            val offsetX by transition.animateFloat(
                                                initialValue = -1f,
                                                targetValue = 2f, // 扫完整条 + 额外一点
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Restart
                                                ),
                                                label = "offsetX"
                                            )



                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(10.dp)
                                                    .onSizeChanged { barWidthPx = it.width.toFloat() }
                                                    .background(
//                                                            Brush.linearGradient(
//                                                                colors = listOf(
//                                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
//                                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
//                                                                ),
//                                                            ),
                                                        Brush.linearGradient(
                                                            colors = listOf(
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
                                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                                            ),
                                                            start = Offset(barWidthPx * offsetX, 0f),
                                                            // 0.5f为渐变宽度
                                                            end = Offset(barWidthPx * (offsetX + 0.5f), 0f) // 始终保持横跨一段宽度
                                                        ),
                                                        shape = CircleShape
                                                    ),
                                            )
//                                                Box(
//                                                    Modifier
//                                                        .fillMaxWidth()
//                                                        .height(10.dp)
//                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = .5f), shape = CircleShape)
//                                                )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                pointList.forEachIndexed { index, _ ->
                                                    val isPort = index == 0 || index == pointList.size-1
                                                    // 圆点
                                                    Box(
                                                        Modifier
                                                            .size(10.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary)
                                                    )
                                                    if (index != pointList.lastIndex) {
                                                        Box(Modifier
                                                            .weight(1f)
                                                            .background(Color.Transparent))
                                                    }
                                                }
                                            }

                                        }
                                    }


                                    // 第二行：竖排文字，平均分布到圆点下方
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        pointList.forEachIndexed { index,point ->
                                            val isPort = index == 0 || index == pointList.size-1
                                            Text(
                                                text = point.toCharArray().joinToString("\n"),
//                                                    fontWeight = if(isPort) FontWeight.Bold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.labelMedium,
                                                lineHeight = 16.sp,
                                                modifier = Modifier.padding(top = CARD_NORMAL_DP*2)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
//    }
}