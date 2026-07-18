package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.shortcut.AppShortcutManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.CONTROL_CENTER_ANIMATION_SPEED
import com.hfut.schedule.ui.model.choice.SharedContainerFilledStrategy
import com.hfut.schedule.ui.model.choice.SharedNavEffect
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.sharednav.common.manager.AnimationSpecManager
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.common.logic.state.NetworkUiState
import com.xah.container.model.TiltEffect
import com.xah.container.util.LocalSharedRegistry
import com.xah.navigation.anim.effect.PushTransitionEffect
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.component.rememberNavController
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.util.DefaultBackHandler
import com.xah.navigation.util.rememberNavDependencies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("NewApi")
@Composable
fun MainHost(
    networkVm : NetWorkViewModel,
    loginVm : LoginViewModel,
    login : Boolean,
    isSuccessActivity: Boolean,
    startDestination : NavDestination
) {
    val celebration = remember { getCelebration() }
    // 初始化网络请求
    if(!isSuccessActivity) {
        LaunchedEffect(Unit) {
            // 如果进入的是登陆界面 未登录做准备
            if(login) {
                //从服务器获取信息
                launch(Dispatchers.IO) {
                    launch { networkVm.getMyApi() }
                    launch { loginVm.getCookie() }
                    launch {  loginVm.getKey() }
                    launch {
                        loginVm.getTicket()
                        val cookie = (loginVm.webVpnTicket.state.value as? NetworkUiState.Success)?.data ?: return@launch
                        loginVm.putKey(cookie)
                        val status = (loginVm.status.state.value as? NetworkUiState.Success)?.data ?: return@launch
                        if(status) {
                            loginVm.getKeyWebVpn()
                        }
                    }
                }
            } else {
                launch(Dispatchers.IO) {
                    if(isUserBirthday()) {
                        showToast("祝您${getUserAge()}周岁🎈生日快乐🎂")
                    }
                }
            }
        }
    }

    val navigationController = rememberNavController(startDestination)
    val dependencies = rememberNavDependencies(networkVm,login,login,celebration,isSuccessActivity) {
        put(networkVm)
        put(loginVm)
        put(login,"login")
        put(celebration)
        put(isSuccessActivity,"isSuccessActivity")
    }

    val context = LocalContext.current
    val transitionLevels = remember { EffectLevel.entries }
    val transition by DataStoreManager.transitionLevel.collectAsState(initial = EffectLevel.NONE.levelNum)
    val useDoubleExtension by DataStoreManager.useDoubleExtension.collectAsState(initial = false)
    val corner by DataStoreManager.screenCorner.collectAsState(-1f)
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
    val enableNavSplashScreen by DataStoreManager.enableNavSplashScreen.collectAsState(initial = false)
    val enableContainerTilt by DataStoreManager.enableContainerTilt.collectAsState(initial = true)
    val enableContainerShare by DataStoreManager.enableContainerShare.collectAsState(initial = true)
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    val shortcutSort by DataStoreManager.shortcutSort.collectAsState(initial = null)
    val currentContainerFilledModeIndex by DataStoreManager.containerFilledStrategy.collectAsState(initial = SharedContainerFilledStrategy.DEFAULT.code)
    val defaultTransitionEffectIndex by DataStoreManager.defaultTransitionEffect.collectAsState(initial = SharedNavEffect.DEFAULT.code)
    val sharedNavSpeedRadio by DataStoreManager.sharedNavSpeedRadio.collectAsState(initial = 1f)
    val enableQuadraticCornerLerp by DataStoreManager.enableQuadraticCornerLerp.collectAsState(initial = false)

    // 动态ShortCut添加（长按图标菜单）
    LaunchedEffect(shortcutSort) {
        AppShortcutManager.init(context,shortcutSort)
    }

    LaunchedEffect(corner) {
        if(corner >= 0f) {
            ScreenCornerHelper.corner = corner.dp
        }
    }

    LaunchedEffect(transition) {
        navigationController.transitionLevel = transitionLevels.find { it.levelNum == transition } ?: EffectLevel.LOW
    }

    LaunchedEffect(motionBlur) {
        navigationController.enableBlur = motionBlur
    }

    LaunchedEffect(enableNavSplashScreen) {
        navigationController.enableSplashScreen = enableNavSplashScreen
    }

    LaunchedEffect(defaultTransitionEffectIndex) {
        var effect = SharedNavEffect.entries.find {
            it.code == defaultTransitionEffectIndex
        }?.effect
            ?: navigationController.sharedTransitionEffect
        if(effect is PushTransitionEffect) {
            effect = PushTransitionEffect()
        }
        navigationController.defaultTransitionEffect = effect
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Party(show = celebration.use && celebration.time != 0L, timeSecond = celebration.time*500)
        SharedNavHost(
            navController = navigationController,
            dependencies = dependencies,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            val registry = LocalSharedRegistry.current

            LaunchedEffect(currentContainerFilledModeIndex) {
                registry.enforceContainerFilledStrategy = SharedContainerFilledStrategy.entries.find {
                    it.code == currentContainerFilledModeIndex
                }?.strategy
            }

            LaunchedEffect(useDoubleExtension) {
                registry.extensionDouble = useDoubleExtension
            }

            LaunchedEffect(enableContainerShare) {
                registry.enabled = enableContainerShare
            }

            LaunchedEffect(enableQuadraticCornerLerp) {
                registry.enforceQuadraticCornerLerp = enableQuadraticCornerLerp
            }

            LaunchedEffect(enableLiquidGlass) {
                navigationController.enableShader = enableLiquidGlass
                registry.enableShader = enableLiquidGlass
            }

            LaunchedEffect(enableContainerTilt) {
                registry.tiltEffect = if(enableContainerTilt) {
                    TiltEffect.ROTATION
                } else {
                    TiltEffect.NONE
                }
            }

            LaunchedEffect(enablePredictive) {
                navigationController.enablePredictiveBack = enablePredictive
                registry.enablePredictiveBack = enablePredictive
            }

            LaunchedEffect(sharedNavSpeedRadio) {
                AnimationSpecManager.speedRadio = sharedNavSpeedRadio
            }
            // 系统返回手势控制
            DefaultBackHandler()
        }
    }
}

suspend fun getDrawOpenOffset(drawerState : DrawerState) : Float = withContext(Dispatchers.IO) {
    drawerState.close()
    val currentValue = DataStoreManager.drawerOffset.first()
    val newValue = drawerState.currentOffset
    if(currentValue == 0f || newValue != currentValue) {
        showToast("正在校准，请勿动稍后")
        DataStoreManager.saveDrawerOffset(newValue)
        showToast("校准完成")
        return@withContext newValue
    } else {
        return@withContext currentValue
    }
}

suspend fun DrawerState.animationClose() = this.animateTo(DrawerValue.Closed, tween(CONTROL_CENTER_ANIMATION_SPEED,easing = FastOutSlowInEasing))
suspend fun DrawerState.animationOpen() = this.animateTo(DrawerValue.Open, spring(dampingRatio = 0.8f, stiffness = 125f))

/* TODO 待重写启动台
@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("NewApi")
@Composable
fun MainHost(
    networkVm : NetWorkViewModel,
    loginVm : LoginViewModel,
    uiVm : UIViewModel,
    login : Boolean,
    isSuccessActivity: Boolean,
    startRoute : String? = null
) {
    val celebration = remember { getCelebration() }
    // 初始化网络请求
    if(!isSuccessActivity) {
        LaunchedEffect(Unit) {
            // 如果进入的是登陆界面 未登录做准备
            if(login) {
                //从服务器获取信息
                launch(Dispatchers.IO) {
                    launch { networkVm.getMyApi() }
                    launch { loginVm.getCookie() }
                    launch {  loginVm.getKey() }
                    launch {
                        loginVm.getTicket()
                        val cookie = (loginVm.webVpnTicket.state.value as? UiState.Success)?.data ?: return@launch
                        loginVm.putKey(cookie)
                        val status = (loginVm.status.state.value as? UiState.Success)?.data ?: return@launch
                        if(status) {
                            loginVm.getKeyWebVpn()
                        }
                    }
                }
            } else {
                launch(Dispatchers.IO) {
                    if(isUserBirthday()) {
                        showToast("祝您${getUserAge()}周岁🎈生日快乐🎂")
                    }
                }
            }
        }
    }

    val scope = rememberCoroutineScope()
    val navigationController = rememberNavController(firstPage(startRoute))
    val dependencies = rememberNavDependencies(networkVm,uiVm,login,login,celebration,isSuccessActivity) {
        put(networkVm)
        put(uiVm)
        put(loginVm)
        put(login,"login")
        put(celebration)
        put(isSuccessActivity,"isSuccessActivity")
    }
//    val configuration = LocalConfiguration.current
//    var screenWidth by remember { mutableIntStateOf(0) }
//    val drawerState =  rememberDrawerState(DrawerValue.Closed)
//    var maxOffset by rememberSaveable { mutableFloatStateOf(prefs.getFloat(OFFSET_KEY,0f)) }
//    val enableControlCenterGesture by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
//    val currentRoute = navigationController.current()?.destination
//    val disabledGesture = currentRoute is WebViewDestination || currentRoute is AgreementDestination

//    val enableCameraDynamicRecord by DataStoreManager.enableCameraDynamicRecord.collectAsState(initial = false)
//    val disabledBlur = if(enableCameraDynamicRecord) {
//        false
//    } else {
//        currentRoute is ScanQrCodeDestination
//    }

//    val enableGesture = enableControlCenterGesture && !disabledGesture
//    var containerColor by remember { mutableStateOf<Color?>(null) }
//    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

//    LaunchedEffect(configuration,enableControlCenterGesture) {
//        if(enableControlCenterGesture) {
//            snapshotFlow { configuration.screenWidthDp }
//                .collect {
//                    screenWidth = it
    // 你可以在这里更新 maxOffset
//                    maxOffset = getDrawOpenOffset(drawerState)
//                }
//        }
//    }
//    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
//    val blurDp by remember {
//        derivedStateOf {
//            if (maxOffset == 0f) {
//                0.dp // 未校准前不模糊
//            } else {
//                val fraction = 1 - (drawerState.currentOffset safeDiv maxOffset).coerceIn(0f, 1f)
//                (fraction * 42.5).dp//42.5 0.85f 0.4f
//            }
//        }
//    }
//    val scale by remember {
//        derivedStateOf {
//            if (maxOffset == 0f) {
//                1f
//            } else {
//                val fraction =  (drawerState.currentOffset safeDiv maxOffset).coerceIn(0f, 1f)
//                (0.85f) * (1 - fraction) + fraction
//            }
//        }
//    }

    // 返回拦截
//    if (enableControlCenterGesture) {
//        val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
//        val callback = remember {
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    scope.launch { drawerState.animationClose() }
//                }
//            }
//        }
//        DisposableEffect(drawerState.currentOffset) {
//            if(drawerState.currentOffset != maxOffset) {
//                dispatcher?.addCallback(callback)
//            }
//            onDispose {
//                callback.remove()
//            }
//        }
//    }
//    val backgroundColor = if(isThemeDark()) {
//        Color.White.copy(MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
//    } else {
//        Color.Black.copy(MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
//    }.let {
//        if(motionBlur && !disabledBlur) {
//            it
//        } else {
//            it.compositeOver(MaterialTheme.colorScheme.surface)
//        }
//    }



//    ModalNavigationDrawer (
//        scrimColor = backgroundColor,
//        drawerState = drawerState,
//        gesturesEnabled = enableGesture,
//        drawerContent = {
//            ControlCenterScreen(navigationController) {
//                scope.launch {
//                    drawerState.animationClose()
//                }
//            }
//        },
//    ) {
//        CompositionLocalProvider(
//            LocalAppControlCenter provides drawerState
//        ) {
    Box(modifier = Modifier.fillMaxSize()
//                .let {
//                    if(enableGesture) it.limitDrawerSwipeArea(allowedArea = with(LocalDensity.current) { Rect(0f,0f, screenWidth.dp.toPx(),150.dp.toPx()) })
//                    else it
//                }
    ) {
        Party(show = celebration.use && celebration.time != 0L, timeSecond = celebration.time*500)
        // 磁钉体系
        SharedNavHost(
            navController = navigationController,
            dependencies = dependencies,
//                    startDestination = firstPage(startRoute),
            modifier = Modifier
                // 启动台背景
                .background(
//                            if(enableLiquidGlass) {
//                                MaterialTheme.colorScheme.surface
//                            } else {
//                                if(disabledGesture) {
                    // 网页
//                                    containerColor ?: MaterialTheme.colorScheme.surface
//                                } else {
                    MaterialTheme.colorScheme.surface
//                                }
//                            }
                )
            // 启动台模糊
//                        .let {
//                            if(motionBlur && enableControlCenterGesture && !disabledBlur)
//                                it.blur(blurDp)
//                            else it
//                        }
            // 启动台缩放
//                        .let {
            // 转场动画时必须关闭 否则打开动画会闪烁
//                            if(enableLiquidGlass) {
//                                it.scaleMirror(scale)
//                            } else {
//                                it.let {
//                                    if(enableControlCenterGesture) {
//                                        it.scale(scale)
//                                    } else
//                                        it
//                                }
//                            }
//                        }
        ) {
            val navController = LocalNavController.current
            val registry = LocalSharedRegistry.current
            val transitionLevels = remember { EffectLevel.entries }
            val transition by DataStoreManager.transitionLevel.collectAsState(initial = EffectLevel.NONE.levelNum)
            val useDoubleExtension by DataStoreManager.useDoubleExtension.collectAsState(initial = false)
            val corner by DataStoreManager.screenCorner.collectAsState(-1f)
            val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
            val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)
            val enableNavSplashScreen by DataStoreManager.enableNavSplashScreen.collectAsState(initial = false)

            LaunchedEffect(corner) {
                if(corner >= 0f) {
                    ScreenCornerHelper.corner = corner.dp
                }
            }

            LaunchedEffect(transition) {
                navController.transitionLevel = transitionLevels.find { it.levelNum == transition } ?: EffectLevel.NO_BLUR
            }

            LaunchedEffect(useDoubleExtension) {
                registry.extensionDouble = useDoubleExtension
            }

            LaunchedEffect(enableLiquidGlass) {
                navController.enableShader = enableLiquidGlass
                registry.enableShader = enableLiquidGlass
            }

            LaunchedEffect(motionBlur) {
                navController.enableBlur = motionBlur
            }

            LaunchedEffect(enableNavSplashScreen) {
                navController.enableSplashScreen = enableNavSplashScreen
            }

            DefaultBackHandler()
        }
    }
//        }
//    }
}
 */