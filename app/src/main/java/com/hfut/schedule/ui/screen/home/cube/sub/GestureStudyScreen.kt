package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.divider.ScrollHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.login.isAnonymity
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.InnerPaddingHeight
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.xah.transition.state.TransitionState
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.util.allRouteStack
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureStudyScreen(
    hazeState: HazeState,
    innerPadding : PaddingValues,
    navController: NavHostController
) {
    val icon = @Composable {
        val speed = TransitionState.curveStyle.speedMs
        var show by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            show = true
            delay(speed*1L)
            delay(1000L)
            show = false
        }

        IconButton(onClick = { showToast("返回") }) {
            Box() {
                AnimatedVisibility(
                    visible = show,
                    enter = DefaultTransitionStyle.centerAllAnimation.enter,
                    exit = DefaultTransitionStyle.centerAllAnimation.exit
                ) {
                    Icon(painterResource(R.drawable.settings), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                AnimatedVisibility(
                    visible = !show,
                    enter = DefaultTransitionStyle.centerAllAnimation.enter,
                    exit = DefaultTransitionStyle.centerAllAnimation.exit
                ) {
                    Icon(painterResource(com.xah.transition.R.drawable.arrow_back), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
    var scale by remember { mutableFloatStateOf(1f) }
    var scale2 by remember { mutableFloatStateOf(1f) }

    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        val state = rememberLazyListState()
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    Column {
                        HazeBottomSheetTopBar("标题")
                        ScrollHorizontalDivider(state)
                    }
                },
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LazyColumn(state = state) {
                        items(30) {
                            StyleCardListItem(
                                headlineContent = { Text("内容") },
                                overlineContent = { Text("描述")},
                                leadingContent = { Text((it+1).toString())},
                            )
                        }
                        item {
                            Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
                        }
                    }
                }
            }
        }
    }
    var showBottomSheetHalf by remember { mutableStateOf(false) }
    if (showBottomSheetHalf) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheetHalf = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheetHalf,
            autoShape = false,
        ) {
            Column {
                HazeBottomSheetTopBar("标题", isPaddingStatusBar = false)
                for(i in 0..3) {
                    StyleCardListItem(
                        headlineContent = { Text("内容") },
                        overlineContent = { Text("描述")},
                        leadingContent = { Text((i+1).toString())},
                    )
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
            }
        }
    }

    PredictiveBackHandler(navController.allRouteStack().size > 1) { progress: Flow<BackEventCompat> ->
        // code for gesture back started
        try {
            progress.collect { backEvent ->
                // code for progress
                scale = 1f - (0.075f * backEvent.progress)
                scale2 = 1f - (1f * backEvent.progress)
            }
            // code for completion
            scale = 0f
            navController.popBackStack()
        } catch (e: CancellationException) {
            // code for cancellation
            scale = 1f
        }
    }
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("顶栏及启动台") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text("标题(标准大小)") },
                    navigationIcon = {
                        icon()
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            FilledTonalIconButton(
                                onClick = {

                                }
                            ) {
                                Icon(painterResource(R.drawable.search), null)
                            }
                            FilledTonalButton(
                                onClick = {

                                }
                            ) {
                                Text("设置")
                            }
                        }
                    }
                )
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("标题的左侧图标") },
                    supportingContent = {
                        Text("无论其图标为什么形态，单击操作均为返回；\n在网页界面单击返回上一个网页，长按直接关闭网页")
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("标题的右侧图标") },
                    supportingContent = {
                        Text("单击执行相应的操作")
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("唤出启动台") },
                    supportingContent = {
                        Text("在标栏整个任意区域向右侧轻扫滑动，跟手唤出；\n网页界面不支持手势唤出，需要在右侧按钮点击唤出；\n部分界面未支持启动台，无法手势唤出")
                    }
                )
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                MediumTopAppBar(
                    modifier = Modifier.topBarBlur(hazeState),
                    colors = topBarTransplantColor(),
                    title = { Text("标题(大)") },
                    navigationIcon = {
                        icon()
                    },
                    actions = {
                        Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
                            FilledTonalIconButton(
                                onClick = {

                                }
                            ) {
                                Icon(painterResource(R.drawable.search), null)
                            }
                            FilledTonalButton(
                                onClick = {

                                }
                            ) {
                                Text("设置")
                            }
                        }
                    }
                )
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("折叠") },
                    supportingContent = {
                        Text("向上滑动(向下滚动)主体内容，顶栏将自动折叠；\n向下滑动(向上滚动)主体内容，顶栏将自动展开")
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("唤出启动台") },
                    supportingContent = {
                        Text("在标栏的标题上侧区域向右侧轻扫滑动，跟手唤出")
                    }
                )
            }
        }
        DividerTextExpandedWith("小标题") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                DividerTextExpandedWith("小标题 1") {
                    StyleCardListItem(
                        headlineContent = { Text("内容1")},
                        leadingContent = {
                            Icon(painterResource(R.drawable.article),null)
                        }
                    )
                }
                DividerTextExpandedWith("小标题 2") {
                    StyleCardListItem(
                        headlineContent = { Text("内容2")},
                        leadingContent = {
                            Icon(painterResource(R.drawable.article),null)
                        }
                    )
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                }
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("小标题") },
                    supportingContent = {
                        Text("单击收起或展开其下属内容")
                    }
                )
            }

        }
        DividerTextExpandedWith("预测式返回") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                RowHorizontal(modifier = Modifier.fillMaxWidth().padding(vertical = APP_HORIZONTAL_DP)) {
                    Box(modifier = Modifier
                        .size(100.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.medium
                        )
                    ) {
                        Box(modifier = Modifier
                            .scale(scale2)
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.shapes.medium
                            )
                        ) {
                            Text(formatDecimal(scale.toDouble(),2), modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                    Box(modifier = Modifier
                        .size(100.dp)
                        .background(
                            Color(0xff1877fe),
                            MaterialTheme.shapes.medium
                        )
                        .blur(((1-scale2)* 10).dp)
                        .scale(scale2)
                    ) {
                        Image(
                            painterResource(R.drawable.alipay_icon),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(1f).align(Alignment.Center).clip(MaterialTheme.shapes.medium),
                        )
                    }
                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                }
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("现在你可以作出返回手势，但不要松手") },
                    supportingContent = {
                        Text("1.未松手时，将根据手指向内滑动的程度，屏幕内容由原大小变小；\n2.当松手后将会执行返回操作\n3.反向滑动(手指在返回手势未松手时，欲撤销返回)，屏幕内容会变为原大小")
                    }
                )
            }

        }
        DividerTextExpandedWith("上推窗口") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("全屏上推窗口") },
                    supportingContent = {
                        Text("点击打开上推窗口，占满全屏")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.pie_chart),null)
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("半屏上推窗口") },
                    supportingContent = {
                        Text("点击打开上推窗口，仅占下侧，适用于即开即走的场景")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.pie_chart),null)
                    },
                    modifier = Modifier.clickable {
                        showBottomSheetHalf = true
                    }
                )
            }
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("全屏上推窗口的收起") },
                    supportingContent = {
                        Text("展开窗口后，在任意区域内向下轻扫滑动或使用返回手势")
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("半屏上推窗口的收起") },
                    supportingContent = {
                        Text("展开窗口后，在窗口区域内向下轻扫滑动、使用返回手势或点击屏幕上部阴影的部分")
                    }
                )
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}