package com.hfut.schedule.ui.screen.shower.home.function

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.ShowerScreen
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.component.text.BottomTip
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.ErrorUI
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.status.StatusUI
import com.hfut.schedule.ui.component.status.StatusUI2
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.largeCardColor
import com.hfut.schedule.ui.screen.home.cube.sub.CirclePoint
import com.hfut.schedule.ui.screen.home.cube.sub.KeyBoard
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.ui.style.special.coverBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.navigateForBottomBar
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

//*******最新模范写法****
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseCodeUI(vm: GuaGuaViewModel, hazeState: HazeState, navController: NavHostController) {
    val uiState by vm.useCodeResult.state.collectAsState()
    var successLoad = uiState is UiState.Success
    var useCode by remember { mutableStateOf("# # #") }
    var showButton = uiState !is UiState.Success

    val scale = animateFloatAsState(
        targetValue = if (!successLoad) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (!successLoad) 0.97f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(AppAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
        ) { ReSetUseCodeUI(vm,navController) }
    }

    val scope = rememberCoroutineScope()
    val refreshNetwork: suspend () -> Unit = {
        showButton = false
        vm.useCodeResult.clear()
        vm.getUseCode()
    }
    val switchAutoRefresh = remember { prefs.getBoolean("SWITCHUSECODE",false) }
    //预加载
    LaunchedEffect(Unit) {
        if(switchAutoRefresh) { refreshNetwork() }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val response = (uiState as UiState.Success).data
            response.let {
                useCode = it
            }
        }
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = APP_HORIZONTAL_DP),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = APP_HORIZONTAL_DP, vertical = 5.dp)
            .scale(scale2.value),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = largeCardColor())
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(!switchAutoRefresh) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn(tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)) + scaleIn(tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)),
                    exit = fadeOut(tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)) + scaleOut(tween(durationMillis = AppAnimationManager.ANIMATION_SPEED)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .zIndex(1f)
                ) {
//                    val blurSizeButton by animateDpAsState(
//                        targetValue = if (!showButton) 4.dp else 0.dp, label = ""
//                        ,animationSpec = tween(MyAnimationManager.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
//                    )
                    Button(
                        onClick = { scope.launch { refreshNetwork() } },
//                        modifier = appBlur(showButton,4.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation()
                    ) {
                        Text("使用码")
                    }
                }
            }

            Column(modifier = Modifier.coverBlur(!successLoad).scale(scale.value)){
                TransplantListItem(
                    headlineContent = {
                        Text(
                            text = "*****$useCode",
                            fontSize = 28.sp,
                            letterSpacing = 7.sp,
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { scope.launch { refreshNetwork() } },
                        ) {
                            Icon(painterResource(R.drawable.rotate_right), null)
                        }
                    }
                )
                Row {
                    TransplantListItem(
                        headlineContent = { Text("花洒输入预设5位+3位") },
                        trailingContent = {
                            Button(
                                onClick = {
                                    showBottomSheet = true
                                }
                            ) {
                                Text(text = "重置")
                            }
                        },
                    )
                }
            }
        }
    }
    BottomTip("使用使用码即使余额<￥5也可洗浴")
}


@Composable
fun ReSetUseCodeUI(vm: GuaGuaViewModel,navController: NavHostController) {
    var password by remember { mutableStateOf("") }
    var passwordStatus by remember { mutableStateOf("请输入新的使用码") }
    val len = remember { 5 }

    if(password.length != len) {
        Column {
            Spacer(Modifier.height(APP_HORIZONTAL_DP*1.5f))
            CirclePoint(text = passwordStatus, password = password, num = 5)
            Spacer(modifier = Modifier.height(20.dp))
            KeyBoard(
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                onKeyClick = { num ->
                    if (password.length < len) {
                        password += num.toString()
                    }
                },
                onBackspaceClick = {
                    if (password.isNotEmpty()) {
                        password = password.dropLast(1)
                    }
                }
            )
        }
    } else {
        val uiState by vm.reSetCodeResult.state.collectAsState()
        val refreshNetwork: suspend () -> Unit = {
            vm.reSetCodeResult.clear()
            vm.reSetUseCode(password)
        }
        LaunchedEffect(Unit) {
            refreshNetwork()
        }

        Column (modifier = Modifier.height(350.dp)) {
            HazeBottomSheetTopBar("修改使用码", isPaddingStatusBar = false)
            CommonNetworkScreen(uiState,isFullScreen = false, onReload = refreshNetwork) {
                val msg = (uiState as UiState.Success).data
                if(msg.contains("成功") == true) {
                    StatusUI2(Icons.Filled.Check,"成功修改为 $password")
                } else if(msg == "密码错误")  {
                    StatusUI(R.drawable.login,"您从未使用密码登录过 需要重新用密码登录")
                    Spacer(Modifier.height(10.dp))
                    RowHorizontal {
                        Button(
                            onClick = {
                                navController.navigateForBottomBar(ShowerScreen.LOGIN.name)
                            }
                        ) {
                            Text("去登录")
                        }
                    }
                } else {
                    ErrorUI(msg)
                }
            }
        }
    }
}