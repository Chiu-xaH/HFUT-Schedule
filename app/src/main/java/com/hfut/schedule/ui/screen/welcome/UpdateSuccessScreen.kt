package com.hfut.schedule.ui.screen.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.file.cleanCache
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.AnimatedTextCarousel
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.measureDpSize
import com.hfut.schedule.ui.util.navigateAndClear
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import com.xah.uicommon.style.color.ShimmerAngle
import com.xah.uicommon.style.color.shimmerEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun UpdateSuccessScreen(
    navController : NavHostController,
) {
    val context = LocalContext.current
    val route = remember { AppNavRoute.UpdateSuccess.route }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val oldVersion = prefs.getString("versionName","上版本")
    val scope = rememberCoroutineScope()

        CustomTransitionScaffold(
            route = route,
            
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
                        val targetRoute = remember { AppNavRoute.VersionInfo.route }

                        FilledTonalIconButton (
                            modifier = Modifier
                                .padding(end = APP_HORIZONTAL_DP)
                            ,
                            shape = MaterialTheme.shapes.extraLarge,
                            onClick = {
                                navController.navigateForTransition(
                                    AppNavRoute.VersionInfo,
                                    targetRoute,
                                    transplantBackground = true
                                )
                            }
                        ) {
                            Icon(
                                painterResource(AppNavRoute.VersionInfo.icon),
                                null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.iconElementShare(targetRoute)
                            )
                        }
                    },
                )
            },
            bottomBar = {
                val targetRoute = remember { AppNavRoute.Empty.withArgs(AppNavRoute.Home.route) }
                Button(
                    onClick = {
                        scope.launch {
                            val result = async { cleanCache(context ) }.await()
                            showToast("已清理缓存 $result MB")
                            async {
                                SharedPrefs.saveString(
                                    "versionName",
                                    AppVersion.getVersionName()
                                )
                            }.await()
                            navController.navigateAndClear(targetRoute)
                        }
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(APP_HORIZONTAL_DP)
                        .navigationBarsPadding()
                        .shimmerEffect(ShimmerAngle.START_TO_END, alpha = 0.25f)
                        .containerShare(targetRoute, MaterialTheme.shapes.extraLarge)

                ) {
                    Text(
                        "开始使用",
                        modifier = Modifier.padding(vertical = CARD_NORMAL_DP * 2),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
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
                ) {
                    AnimatedTextCarousel(welcomeTexts)
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
//    }
}




@Composable
private fun PhoneFrame(modifier: Modifier = Modifier,cornerRadius : Float,strokeWidth : Float) {
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
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth)
            )
            // 右上圆角
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(w - cornerRadius*2, 0f),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}



val welcomeTexts = listOf(
    "你好",
    "欢迎使用",
    "(｡･ω･)ﾉﾞ",
    "Hello",
    "Hola",
    "Bonjour",
    "٩(ˊωˋ*)و✧",
    "Ciao",
    "안녕하세요",
    "こんにちは",
    "Hallo",
    "Olá",
    "Привет",
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedWelcomeScreen() {
    var currentIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // 每3秒切换
            currentIndex = (currentIndex + 1) % welcomeTexts.size
        }
    }

    // 界面布局

    Column(modifier = Modifier
        .padding(horizontal = APP_HORIZONTAL_DP)) {
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedContent(
            targetState = welcomeTexts[currentIndex],
            transitionSpec = {
                fadeIn(animationSpec = tween(AppAnimationManager.ANIMATION_SPEED)) togetherWith(fadeOut(animationSpec = tween(AppAnimationManager.ANIMATION_SPEED)))
            }, label = ""
        ) { targetText ->
            Text(
                text = targetText,
                fontSize = 38.sp,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

