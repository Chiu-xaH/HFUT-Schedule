package com.hfut.schedule.ui.screen.login

import android.icu.number.IntegerWidth
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.cleanCache
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.UpdateContents
import com.hfut.schedule.ui.screen.home.cube.sub.update.VersionInfo
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.measureDpSize
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndClear
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
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
                                .iconElementShare(sharedTransitionScope, animatedContentScope = animatedContentScope, route = AppNavRoute.UseAgreement.route)
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun UpdateSuccessScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val oldVersion = prefs.getString("versionName","上版本")
    val scope = rememberCoroutineScope()

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

    var showBottomSheet_version by remember { mutableStateOf(false) }
    if (showBottomSheet_version) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_version = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_version
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("本版本新特性") {
                        FilledTonalButton(onClick = { showBottomSheetUpdate = true }) {
                            Text("历史更新日志")
                        }
                    }
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    VersionInfo()
                }
            }
        }
    }

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
                        Text(text = "新版本已就绪", modifier = Modifier.padding(start = 3.5.dp),)
                    }
                },
                actions = {
                    FilledTonalButton(
                        modifier = Modifier.padding(end = APP_HORIZONTAL_DP),
                        onClick = {
                            showBottomSheet_version = true
                        }
                    ) {
                        Text("本版本新特性")
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
                        async { SharedPrefs.saveString("versionName", AppVersion.getVersionName()) }.await()
                        navController.navigateAndClear(AppNavRoute.Home.route)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
            ) {
                Text(
                    "开始使用",
                    modifier = Modifier.padding(vertical = CARD_NORMAL_DP*2),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        },
        modifier = Modifier.hazeSource(hazeState)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            var rotated by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                rotated = true
            }

            val rotation by animateFloatAsState(
                targetValue = if (rotated) 180f else 0f,
                animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED*2),
                label = "rotationAnim"
            )
            val scale by animateFloatAsState(
                targetValue = if (rotated) 1f else 0f,
                animationSpec = tween(durationMillis = AppAnimationManager.ANIMATION_SPEED),
                label = "rotationAnim"
            )

            PhoneFrame(modifier = Modifier.fillMaxSize().padding(horizontal = APP_HORIZONTAL_DP*2-6.dp).padding(top = APP_HORIZONTAL_DP).align(Alignment.Center),50f,APP_HORIZONTAL_DP.value)

            ColumnVertical(
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Icon(
                    painterResource(R.drawable.cached),
                    null,
                    modifier = Modifier
                        .size(110.dp)
                        .rotate(rotation)
                        .scale(scale)
                    ,
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
