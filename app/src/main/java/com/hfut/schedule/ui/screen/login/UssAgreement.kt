package com.hfut.schedule.ui.screen.login

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.cleanCache
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.measureDpSize
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndClear
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.color.ShimmerAngle
import com.xah.uicommon.style.color.shimmerEffect
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val arguments = listOf(
    "本应用所使用权限为：网络、日历(用于向日历写入日程提醒)、存储(用于导入导出一些文件)、相机(用于扫码)、通知(用于提醒更新包已准备好)，均由用户自由决定授予",
    "本应用已在Github开源，F-Droid上架，无广告、恶意等行为",
    "本应用推荐但不限于合肥工业大学宣城校区在校生使用，不对未登录用户做强制要求",
    "本应用不代表学校官方，若因使用本应用而造成实际损失，概不负责",
    "本应用存在开发者自己的服务端，会收集一些不敏感的数据帮助改善使用体验，开发者承诺不会泄露数据，且用户可自由决定开启",
    "由于本应用为开源项目，欢迎其他开发者借鉴、指正或参与",
    "欢迎用户向开发者反馈、建议或寻求帮助，个人的测试范围有限，需要大家发现问题",
    "最后编辑于 2025-08-27 22:51 第6版"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
//@Preview
fun UseAgreementScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val context = LocalActivity.current
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        ),
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(text = "用户协议", modifier = Modifier.padding(start = 3.5.dp),)
                    }
                },
                actions = {
                    val height = MaterialTheme.typography.headlineMedium.lineHeight.value.dp
                    Row(modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                        Icon(
                            painterResource(R.drawable.partner_exchange),
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier =  Modifier
                                .size(height)
                                .iconElementShare(
                                    sharedTransitionScope,
                                    animatedContentScope = animatedContentScope,
                                    route = AppNavRoute.UseAgreement.route
                                )
                        )
                    }
                },
                navigationIcon = {
                    AnimatedWelcomeScreen()
                },
                modifier = Modifier.topBarBlur(hazeState, )
            )
        },
        bottomBar = {
            Column () {
                Box(Modifier.bottomBarBlur(hazeState)) {
                    Row(modifier = Modifier
                        .padding(APP_HORIZONTAL_DP)
                        .navigationBarsPadding(),horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                scope.launch {
                                    async {
                                        launch { SharedPrefs.saveString("versionName", AppVersion.getVersionName()) }
                                        launch { SharedPrefs.saveBoolean("canUse", default = false, save = true) }
                                    }.await()
                                    navController.navigateAndClear(AppNavRoute.Home.route)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            Text("同意")
                        }
                        Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP*2/3))
                        FilledTonalButton(
                            onClick = {
                                showToast("已关闭APP")
                                context?.finish()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                        ) {
                            SharedPrefs.saveBoolean("canUse", default = false, save = false)
                            Text("拒绝")
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            LazyColumn {
                item { InnerPaddingHeight(innerPadding,true) }
                items(arguments.size) { index ->
                    val item = arguments[index]
                    TransplantListItem(
                        headlineContent = { Text(item) },
                        leadingContent = { Text((index+1).toString()) }
                    )
                }
                item { InnerPaddingHeight(innerPadding,false) }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun UpdateSuccessScreen(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.UpdateSuccess.route }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val oldVersion = prefs.getString("versionName","上版本")
    val scope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        CustomTransitionScaffold(
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                LargeTopAppBar(
                    colors = topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "新版本已就绪",
                                modifier = Modifier.padding(start = 3.5.dp),
                            )
                        }
                    },
                    actions = {
                        val targetRoute = remember { AppNavRoute.InfoDetail.route }

                        FilledTonalIconButton (
                            modifier = Modifier
                                .padding(end = APP_HORIZONTAL_DP)
//                                .containerShare(
//                                    sharedTransitionScope,
//                                    animatedContentScope,
//                                    AppNavRoute.InfoDetail.route,
//                                    MaterialTheme.shapes.extraLarge
//                                )
                                ,
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = {
                                navController.navigateForTransition(
                                    AppNavRoute.InfoDetail,
                                    targetRoute,
                                    transplantBackground = false
                                )
                            }
                        ) {
                            Icon(
                                painterResource(AppNavRoute.InfoDetail.icon),
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.iconElementShare(
                                    sharedTransitionScope,
                                    animatedContentScope,
                                    targetRoute
                                )
                            )
                        }
                    },
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        scope.launch {
                            val result = async { cleanCache() }.await()
                            showToast("已清理缓存 $result MB")
                            async {
                                SharedPrefs.saveString(
                                    "versionName",
                                    AppVersion.getVersionName()
                                )
                            }.await()
                            navController.navigateAndClear(AppNavRoute.Home.route)
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(APP_HORIZONTAL_DP)
                        .navigationBarsPadding()
                        .shimmerEffect(ShimmerAngle.START_TO_END, alpha = 0.25f)
                        .containerShare(
                                    sharedTransitionScope,
                                    animatedContentScope,
                                    AppNavRoute.InfoDetail.route,
                                    MaterialTheme.shapes.extraLarge
                        )

                ) {
                    Text(
                        "开始使用",
                        modifier = Modifier.padding(vertical = CARD_NORMAL_DP * 2),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
//        containerColor = Color.Transparent,
            modifier = Modifier.hazeSource(hazeState)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(.5f),
                                MaterialTheme.colorScheme.surface
                            ),
                            center = Offset.Unspecified, // 默认居中
                            radius = 500f // 半径越大，过渡越柔和
                        )
                    )
            ) {
                var rotated by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    rotated = true
                }

                val rotation by animateFloatAsState(
                    targetValue = if (rotated) 180f else 0f,
                    animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED * 2),
                    label = "rotationAnim"
                )
                val scale by animateFloatAsState(
                    targetValue = if (rotated) 1f else 0f,
                    animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED),
                    label = "rotationAnim"
                )

                PhoneFrame(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = APP_HORIZONTAL_DP * 2 - 6.dp)
                        .padding(top = APP_HORIZONTAL_DP)
                        .align(Alignment.Center),
                    50f,
                    APP_HORIZONTAL_DP.value
                )

                ColumnVertical(
                    modifier = Modifier
                        .align(Alignment.Center)
//                    .animateContentSize()
                ) {
                    AnimatedTextCarousel(
                        listOf(
                            "你好",
                            "欢迎使用",
                            "(｡･ω･)ﾉﾞ",
                            "Hello",       // 英语
                            "Hola",        // 西班牙语
                            // 中文
                            "Bonjour", // 法语
                            "٩(ˊωˋ*)و✧",
                            "Ciao",        // 意大利语
                            "안녕하세요",    // 韩语
                            "こんにちは",   // 日语
                            "Hallo",       // 德语
                            "Olá",         // 葡萄牙语
                            "Привет",     // 俄语
                        )
                    )
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                    Icon(
                        painterResource(R.drawable.cached),
                        null,
                        modifier = Modifier
                            .size(110.dp)
                            .rotate(rotation)
                            .scale(scale),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$oldVersion → ${AppVersion.getVersionName()}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = APP_HORIZONTAL_DP)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun InfoDetailScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)

    var showBottomSheetUpdate by remember { mutableStateOf(false) }

    if(showBottomSheetUpdate) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheetUpdate = false },
            showBottomSheet = showBottomSheetUpdate,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("历史更新日志")
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    UpdateContents(vm)
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val route = remember { AppNavRoute.InfoDetail.route }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            roundShape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.InfoDetail.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.InfoDetail.icon)
                    },
                    actions = {
                        FilledTonalButton(onClick = { showBottomSheetUpdate = true }, modifier = Modifier.padding(end = APP_HORIZONTAL_DP)) {
                            Text("历史更新日志")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                VersionInfo()
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}



@Composable
fun PhoneFrame(modifier: Modifier = Modifier,cornerRadius : Float,strokeWidth : Float) {
    val color = MaterialTheme.colorScheme.secondaryContainer
    var width by remember { mutableStateOf(0.dp) }

    Box(
        modifier = modifier.measureDpSize { w,_ -> width = w },
        contentAlignment = Alignment.Center
    ) {
        val height = width*2

        Canvas(
            modifier = Modifier
                .size(width, height)
        ) {
            val w = size.width
            val h = size.height

            val gradient = Brush.verticalGradient(
                colors = listOf(color, Color.Transparent)
            )

            // 左边
            drawLine(
                brush = gradient,
                start = Offset(0f, cornerRadius),
                end = Offset(0f, h),
                strokeWidth = strokeWidth
            )
            // 右边
            drawLine(
                brush = gradient,
                start = Offset(w, cornerRadius),
                end = Offset(w, h),
                strokeWidth = strokeWidth
            )
            // 上边（横向渐变可选）
            drawLine(
                color = color,
                start = Offset(cornerRadius, 0f),
                end = Offset(w - cornerRadius, 0f),
                strokeWidth = strokeWidth
            )


            // 左上圆角
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            // 右上圆角
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(w - cornerRadius*2, 0f),
                size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    delayMillisPerChar: Int = 200 // 每个字延迟时间
) {
    Row(modifier) {
        text.forEachIndexed { index, char ->
            // 控制是否显示
            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(index * delayMillisPerChar.toLong())
                visible = true
            }

            AnimatedVisibility(visible = visible) {
                // 出现时从小到大
                var scale by remember { mutableStateOf(0.6f) }
                val animatedScale by animateFloatAsState(
                    targetValue = scale,
                    animationSpec = tween(durationMillis = 400, easing = overshootInterpolatorEasing(2f)),
                    label = "scaleAnim"
                )
                // 动画启动
                LaunchedEffect(visible) {
                    if (visible) scale = 1f
                }

                Text(
                    text = char.toString(),
                    modifier = Modifier.graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    },
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
// 自定义 Easing，Overshoot 效果更像“弹出”
private fun overshootInterpolatorEasing(tension: Float) = Easing { fraction ->
    (fraction - 1).let { it * it * ((tension + 1) * it + tension) + 1 }
}

enum class Phase { SHOW, HOLD, HIDE }

@Composable
fun AnimatedTextCarousel(
    texts: List<String>,
    modifier: Modifier = Modifier,
    delayMillisPerChar: Int = 100,     // 每个字出现/消失间隔
    holdMillis: Int = 1000,            // 每段停留时长
    textStyle: TextStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary)
) {
    if (texts.isEmpty()) return

    var currentIndex by remember { mutableStateOf(0) }
    var phase by remember { mutableStateOf(Phase.SHOW) }

    // 用不可变 List 存状态（也可以用 mutableStateListOf）
    var visibleFlags by remember { mutableStateOf(List(texts[0].length) { false }) }

    val currentText = texts[currentIndex]

    // 确保在 index 切换时，visibleFlags 至少被重置为正确长度（尽量减少越界窗口）
    LaunchedEffect(currentIndex) {
        visibleFlags = List(currentText.length) { false }
    }

    // 动画控制器
    LaunchedEffect(currentIndex, phase) {
        when (phase) {
            Phase.SHOW -> {
                // 再次保证重置（防止竞争）
                visibleFlags = List(currentText.length) { false }
                for (i in currentText.indices) {
                    delay(delayMillisPerChar.toLong())
                    visibleFlags = visibleFlags.toMutableList().also { it[i] = true }
                }
                phase = Phase.HOLD
            }
            Phase.HOLD -> {
                delay(holdMillis.toLong())
                phase = Phase.HIDE
            }
            Phase.HIDE -> {
                for (i in currentText.indices.reversed()) {
                    delay(delayMillisPerChar.toLong())
                    visibleFlags = visibleFlags.toMutableList().also { it[i] = false }
                }
                // 全部缩回后，停留一会儿
                delay(holdMillis.toLong())
                currentIndex = (currentIndex + 1) % texts.size
                phase = Phase.SHOW
            }
        }
    }

    // UI：在取 visibleFlags 时做安全取值，避免 OOB
    Row(modifier) {
        key(currentIndex) { // 可选：强制在 index 切换时重建子树，减少状态残留问题
            currentText.forEachIndexed { index, char ->
                val isVisible = visibleFlags.getOrNull(index) ?: false
                Box {
                    // 1) 占位文本，始终存在且不可见（占用真实字符宽度）
                    Text(
                        text = "",
                        style = textStyle
                    )

                    Row {
                        AnimatedVisibility(
                            visible = isVisible,
                        ) {
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isVisible) 1f else 0.6f,
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = overshootInterpolatorEasing(2f)
                                )
                            )

                            Text(
                                text = char.toString(),
                                modifier = Modifier.graphicsLayer {
                                    scaleX = animatedScale
                                    scaleY = animatedScale
                                },
                                style = textStyle
                            )
                        }
                    }
                }
            }
        }
    }
}



