package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.datetime.getCelebration
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LocalAppControlCenter
import com.hfut.schedule.ui.component.screen.Party
import com.hfut.schedule.ui.destination.AgreementDestination
import com.hfut.schedule.ui.destination.HomeDestination
import com.hfut.schedule.ui.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.destination.UpdateSuccessfullyDestination
import com.hfut.schedule.ui.screen.util.ControlCenterScreen
import com.hfut.schedule.ui.screen.util.limitDrawerSwipeArea
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.ui.util.navigation.AppAnimationManager.CONTROL_CENTER_ANIMATION_SPEED
import com.hfut.schedule.ui.util.webview.isThemeDark
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.mirror.shader.scaleMirror
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.component.DefaultBackHandler
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.utils.LocalNavController
import com.xah.navigation.utils.rememberNavDependencies
import com.xah.transition.util.currentRouteWithoutArgs
import com.xah.uicommon.util.LogUtil
import com.xah.uicommon.util.safeDiv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val OFFSET_KEY = "OFFSET_DRAWERS"
suspend fun getDrawOpenOffset(drawerState : DrawerState) : Float = withContext(Dispatchers.IO) {
    drawerState.close()
    val currentValue = prefs.getFloat(OFFSET_KEY,0f)
    val newValue = drawerState.currentOffset
    if(currentValue == 0f || newValue != currentValue) {
        showToast("正在校准，请勿动稍后")
        SharedPrefs.saveFloat(OFFSET_KEY,newValue,0f)
        showToast("校准完成")
        return@withContext newValue
    } else {
        return@withContext currentValue
    }
}

suspend fun DrawerState.animationClose() = this.animateTo(DrawerValue.Closed, tween(CONTROL_CENTER_ANIMATION_SPEED,easing = FastOutSlowInEasing))
suspend fun DrawerState.animationOpen() = this.animateTo(DrawerValue.Open, spring(dampingRatio = 0.8f, stiffness = 125f))
// 比较版本号 前2位相同则不显示 否则显示
private fun haveImportantUpdate() : Boolean {
    try {
        val lastVersionName =  prefs.getString("versionName", "上版本") ?: return true
        val nowVersionName = AppVersion.getVersionName()

        if(lastVersionName == nowVersionName) {
            return false
        }

        if(lastVersionName == "上版本") {
            return true
        }

        val lastVersion = lastVersionName.split('.')
        if(lastVersion.size < 2) {
            return false
        }

        val nowVersion = nowVersionName.split('.')
        if(nowVersion.size < 2) {
            return false
        }

        return !(nowVersion[1] == lastVersion[1] && nowVersion[0] == lastVersion[0])
    } catch (e : Exception) {
        LogUtil.error(e)
        return false
    }
}

private fun firstPage(
    startRoute : String?
) : NavDestination {
    return if(prefs.getBoolean("canUse",false)) {
        if(startRoute != null) {
            ScanQrCodeDestination
        } else {
            if(!haveImportantUpdate()) {
                HomeDestination
            } else {
                UpdateSuccessfullyDestination
            }
        }
    } else {
        AgreementDestination
    }
}

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
    val navController = rememberNavController()
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

    val configuration = LocalConfiguration.current
    var screenWidth by remember { mutableIntStateOf(0) }
    val drawerState =  rememberDrawerState(DrawerValue.Closed)
    var maxOffset by rememberSaveable { mutableFloatStateOf(prefs.getFloat(OFFSET_KEY,0f)) }
    val enableControlCenterGesture by DataStoreManager.enableControlCenterGesture.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentRouteWithoutArgs()
    val disabledGesture = currentRoute == AppNavRoute.WebView.route || currentRoute == AppNavRoute.Agreement.route

    val enableCameraDynamicRecord by DataStoreManager.enableCameraDynamicRecord.collectAsState(initial = false)
    val disabledBlur = if(enableCameraDynamicRecord) {
        false
    } else {
        currentRoute == AppNavRoute.ScanQrCode.route
    }

    val enableGesture = enableControlCenterGesture && !disabledGesture
    var containerColor by remember { mutableStateOf<Color?>(null) }
    val enableLiquidGlass by DataStoreManager.enableLiquidGlass.collectAsState(initial = AppVersion.CAN_SHADER)

    LaunchedEffect(configuration,enableControlCenterGesture) {
        if(enableControlCenterGesture) {
            snapshotFlow { configuration.screenWidthDp }
                .collect {
                    screenWidth = it
                    // 你可以在这里更新 maxOffset
                    maxOffset = getDrawOpenOffset(drawerState)
                }
        }
    }
    val motionBlur by DataStoreManager.enableMotionBlur.collectAsState(initial = AppVersion.CAN_MOTION_BLUR)
    val blurDp by remember {
        derivedStateOf {
            if (maxOffset == 0f) {
                0.dp // 未校准前不模糊
            } else {
                val fraction = 1 - (drawerState.currentOffset safeDiv maxOffset).coerceIn(0f, 1f)
                (fraction * 42.5).dp//42.5 0.85f 0.4f
            }
        }
    }
    val scale by remember {
        derivedStateOf {
            if (maxOffset == 0f) {
                1f
            } else {
                val fraction =  (drawerState.currentOffset safeDiv maxOffset).coerceIn(0f, 1f)
                (0.85f) * (1 - fraction) + fraction
            }
        }
    }

    // 返回拦截
    if (enableControlCenterGesture) {
        val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        val callback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    scope.launch { drawerState.animationClose() }
                }
            }
        }
        DisposableEffect(drawerState.currentOffset) {
            if(drawerState.currentOffset != maxOffset) {
                dispatcher?.addCallback(callback)
            }
            onDispose {
                callback.remove()
            }
        }
    }
    val backgroundColor = if(isThemeDark()) {
        Color.White.copy(MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
    } else {
        Color.Black.copy(MyApplication.CONTROL_CENTER_BACKGROUND_MASK_ALPHA)
    }.let {
        if(motionBlur && !disabledBlur) {
            it
        } else {
            it.compositeOver(MaterialTheme.colorScheme.surface)
        }
    }

    val dependencies = rememberNavDependencies(networkVm,uiVm,login,login,celebration,isSuccessActivity) {
        put(networkVm)
        put(uiVm)
        put(loginVm)
        put(login,"login")
        put(celebration)
        put(isSuccessActivity,"isSuccessActivity")
    }

    ModalNavigationDrawer (
        scrimColor = backgroundColor,
        drawerState = drawerState,
        gesturesEnabled = enableGesture,
        drawerContent = {
            ControlCenterScreen(navController) {
                scope.launch {
                    drawerState.animationClose()
                }
            }
        },
    ) {
        CompositionLocalProvider(
            LocalAppControlCenter provides drawerState
        ) {
            Box(modifier = Modifier.fillMaxSize()
                .let {
                    if(enableGesture) it.limitDrawerSwipeArea(allowedArea = with(LocalDensity.current) { Rect(0f,0f, screenWidth.dp.toPx(),150.dp.toPx()) })
                    else it
                }
            ) {
                Party(show = celebration.use && celebration.time != 0L, timeSecond = celebration.time*500)
                // 磁钉体系
                SharedNavHost(
                    dependencies = dependencies,
                    startDestination = firstPage(startRoute),
                    modifier = Modifier
                        // 启动台背景
                        .background(
                            if(enableLiquidGlass) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                if(disabledGesture) {
                                    // 网页
                                    containerColor ?: MaterialTheme.colorScheme.surface
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            }
                        )
                        // 启动台模糊
                        .let {
                            if(motionBlur && enableControlCenterGesture && !disabledBlur)
                                it.blur(blurDp)
                            else it
                        }
                        // 启动台缩放
                        .let {
                            // 转场动画时必须关闭 否则打开动画会闪烁
                            if(enableLiquidGlass) {
                                it.scaleMirror(scale)
                            } else {
                                it.let {
                                    if(enableControlCenterGesture) {
                                        it.scale(scale)
                                    } else
                                        it
                                }
                            }
                        }
                ) {
                    val navController = LocalNavController.current
                    val transitionLevels = remember { EffectLevel.entries }
                    val transition by DataStoreManager.transitionLevel.collectAsState(initial = EffectLevel.NO_BLUR.levelNum)

                    LaunchedEffect(transition) {
                        navController.transitionLevel = transitionLevels.find { it.levelNum == transition } ?: EffectLevel.NO_BLUR
                    }

                    DefaultBackHandler()
                }
            }
        }
    }
}
