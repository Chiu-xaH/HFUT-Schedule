package com.hfut.schedule.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.hfut.schedule.ui.model.choice.SharedContainerFilledStrategy
import com.hfut.schedule.ui.model.choice.SharedNavEffect
import com.hfut.schedule.ui.model.choice.SharedNavTilt
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.manager.AnimationSpecManager
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
import kotlinx.coroutines.launch

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
    val enableContainerTilt by DataStoreManager.enableContainerTilt.collectAsState(initial = SharedNavTilt.ROTATION.code)
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
                registry.tiltEffect = SharedNavTilt.entries.find { it.code == enableContainerTilt }?.effect ?: TiltEffect.ROTATION
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