package com.hfut.schedule.ui.activity.shower.home.function

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.guagua.UseCodeResponse
import com.hfut.schedule.logic.enums.ShowerScreen
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.reEmptyLiveDta
import com.hfut.schedule.ui.activity.home.cube.items.subitems.CirclePoint
import com.hfut.schedule.ui.activity.home.cube.items.subitems.KeyBoard
import com.hfut.schedule.ui.utils.components.ActiveTopBar
import com.hfut.schedule.ui.utils.components.appHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.largeCardColor
import com.hfut.schedule.ui.utils.components.LoadingUI
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.statusUI
import com.hfut.schedule.ui.utils.components.statusUI2
import com.hfut.schedule.ui.utils.navigateAndSave
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.RowHorizontal
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

//*******最新模范写法****
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UseCodeUI(vm: GuaGuaViewModel,hazeState: HazeState,navController: NavHostController) {
    var refresh by remember { mutableStateOf(true) }
    var loading by remember { mutableStateOf(true) }
    var useCode by remember { mutableStateOf("# # #") }
    var showButton by remember { mutableStateOf(true) }

    val scale = animateFloatAsState(
        targetValue = if (loading) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loading) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loading) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var passwordStatus by remember { mutableStateOf("请输入新的使用码") }
    val len = 5

    if (showBottomSheet) {
        password = ""
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
//            sheetState = sheetState,
        ) {
            if(password.length != len) {
                Column {
                    Spacer(Modifier.height(appHorizontalDp()*1.5f))
                    CirclePoint(text = passwordStatus, password = password, num = 5)
                    Spacer(modifier = Modifier.height(20.dp))
                    KeyBoard(
                        modifier = Modifier.padding(horizontal = appHorizontalDp()),
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
                //发送修改请求
                var loading by remember { mutableStateOf(true) }
                var msg by remember { mutableStateOf("") }
                CoroutineScope(Job()).launch {
                    async { reEmptyLiveDta(vm.reSetCodeResult) }.await()
                    async { vm.reSetUseCode(password) }.await()
                    async {
                        Handler(Looper.getMainLooper()).post{
                            vm.reSetCodeResult.observeForever { result ->
                                if (result != null) {
                                    Log.d("ss",result)
                                    if(result.contains("message")) {
                                        msg = Gson().fromJson(result,StatusMsgResponse::class.java).message
                                        loading = false
                                    }
                                }
                            }
                        }
                    }
                }
                Column (modifier = Modifier.height(300.dp)) {
                    ActiveTopBar("修改使用码")
                    if(!loading) {
                        if(msg.contains("成功")) {
                            statusUI2(Icons.Filled.Check,"成功修改为 $password")
                        } else if(msg == "密码错误")  {
                            statusUI(R.drawable.login,"您从未使用密码登录过 需要重新用密码登录")
                            Spacer(Modifier.height(10.dp))
                            RowHorizontal {
                                Button(
                                    onClick = {
                                        navController.navigateAndSave(ShowerScreen.LOGIN.name)
                                    }
                                ) {
                                    Text("去登录")
                                }
                            }
                        } else {
                            statusUI2(Icons.Filled.Close,msg)
                        }
                    } else {
                        LoadingUI()
                    }
                }
            }
        }
    }

    fun refresh() {
        showButton = false
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.userCode) }.await()
            async { vm.getUseCode() }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.userCode.observeForever { result ->
                        if (result != null) {
                            if(result.contains("成功")) {
                                useCode = Gson().fromJson(result,UseCodeResponse::class.java).data.randomCode
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    val switchAutoRefresh = prefs.getBoolean("SWITCHUSECODE",false)
    //预加载
    if(switchAutoRefresh && refresh) { refresh() }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = appHorizontalDp()),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = appHorizontalDp(), vertical = 5.dp).scale(scale2.value),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = largeCardColor())
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(!switchAutoRefresh) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showButton,
                    exit = fadeOut(tween(durationMillis = MyApplication.ANIMATION_SPEED)) + scaleOut(tween(durationMillis = MyApplication.ANIMATION_SPEED)),
                    modifier = Modifier.align(Alignment.Center).zIndex(1f)
                ) {
                    val blurSizeButton by animateDpAsState(
                        targetValue = if (!showButton) 4.dp else 0.dp, label = ""
                        ,animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
                    )
                    Button(
                        onClick = { refresh() },
                        modifier = Modifier.blur(blurSizeButton),
                        elevation = ButtonDefaults.elevatedButtonElevation()
                    ) {
                        Text("使用码")
                    }
                }
            }

            Column(modifier = Modifier.blur(blurSize).scale(scale.value)){
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
                            onClick = { refresh() },
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
