package com.hfut.schedule.ui.screen.shower.login

import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.guagua.GuaGuaLogin
import com.hfut.schedule.logic.model.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.CryptoUtil
import com.hfut.schedule.network.helper.GsonInstance
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.ui.nav.destination.GuaGuaDestination
import com.hfut.schedule.ui.screen.shower.cube.EditLoginCode
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerLogin(vm : NetWorkViewModel) {
    val context = LocalActivity.current
    var show by remember { mutableStateOf(false) }
    val Savedusername = prefs.getString("PHONENUM", "")
    var username by remember { mutableStateOf(Savedusername ?: "") }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = {
                    Text(
                        text = "呱呱物联-登录", modifier = Modifier.padding(start = 10.dp)
                    )
                },
                actions = {
                    Row {
                        TextButton (onClick = {
                            show = !show
                        }) {
                            Text("备用登录方式")
                        }
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon  = {
                    TopBarNavigationIcon()
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = show,
                enter = AppAnimationManager.toBottomAnimation.enter,
                exit = AppAnimationManager.toBottomAnimation.exit,
                modifier = Modifier.padding(APP_HORIZONTAL_DP).navigationBarsPadding().bottomBarBlur(hazeState)
            ) {
                Row(
//                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    EditLoginCode(true,{
                        saveString("PHONENUM",username)
                        CoroutineScope(Job()).launch {
                            async { vm.getGuaGuaUserInfo() }.await()
                            launch {
                                Handler(Looper.getMainLooper()).post {
                                    vm.guaGuaUserInfo.observeForever { result ->
                                        if (result?.contains("成功") == true) {
                                            saveString("GuaGuaPersonInfo",result)
                                            val launchMode = if(navController.containsDestination(GuaGuaDestination)) {
                                                LaunchMode.PopToExisting(reuse = false)
                                            } else {
                                                LaunchMode.Replace()
                                            }
                                            navController.push(GuaGuaDestination, launchMode = launchMode, effect = JumpTransitionEffectWallpaper())
                                        } else if(result?.contains("error") == true) {
                                            showToast("登陆失败")
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            GuaGuaLoginUI(vm,username) {
                username = it
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuaGuaLoginUI(vm : NetWorkViewModel,username : String,onUsername : (String) -> Unit) {
    val navController = LocalNavController.current
    var hidden by rememberSaveable { mutableStateOf(true) }

    val Savedpassword = prefs.getString("GuaGuaPsk","")

    var inputAES by remember { mutableStateOf(Savedpassword ?: "") }

    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    val showTip by remember { mutableStateOf("请新用户先前往微信小程序-呱呱物联注册") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                value = username,
                onValueChange = onUsername,
                label = { Text("手机号" ) },
                singleLine = true,
                // placeholder = { Text("请输入正确格式")},
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
                leadingIcon = { Icon( painterResource(R.drawable.call), contentDescription = "Localized description") },

                trailingIcon = {
                    IconButton(onClick = {
                        onUsername("")
                        inputAES = ""
                    }) { Icon(painter = painterResource(R.drawable.close), contentDescription = "description") }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                value = inputAES,
                onValueChange = { inputAES = it },
                label = { Text("密码") },
                singleLine = true,
                colors = textFiledTransplant(),
                supportingText = { Text(showTip)},
                visualTransformation = if (hidden) PasswordVisualTransformation()
                else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(painterResource(R.drawable.key), contentDescription = "Localized description") },
                trailingIcon = {
                    IconButton(onClick = { hidden = !hidden }) {
                        val icon =
                            if (hidden) painterResource(R.drawable.visibility_off)
                            else painterResource(R.drawable.visibility)
                        val description =
                            if (hidden) "展示密码"
                            else "隐藏密码"
                        Icon(painter = icon, contentDescription = description)
                    }
                },
                shape = MaterialTheme.shapes.medium
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch { loginGuaGuaClick(username,inputAES,vm,navController) }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("登录")
        }
    }
}


suspend fun loginGuaGuaClick(
    phoneNumber: String,
    psk: String,
    vm: NetWorkViewModel,
    navHostController: NavigationController
) : Unit = withContext(Dispatchers.IO) {
    // 存储信息
    saveString("PHONENUM", phoneNumber)
    saveString("GuaGuaPsk", psk)
    val inputPSK = CryptoUtil.md5HashForGuaGua(psk).uppercase(Locale.getDefault())

    // 启动登录
    vm.loginGuaGuaResp.clear()
    vm.loginGuaGua(phoneNumber, inputPSK)

    onListenStateHolder(vm.loginGuaGuaResp) { data ->
        data.message.let{ msg ->
            if(msg.contains("成功")) {
                val launchMode = if(navHostController.containsDestination(GuaGuaDestination)) {
                    LaunchMode.PopToExisting(reuse = false)
                } else {
                    LaunchMode.Replace()
                }
                navHostController.push(GuaGuaDestination, launchMode = launchMode, effect = JumpTransitionEffectWallpaper())
                showToast("登录成功")
            } else {
                showToast(data.message)
            }
        }
    }
    // 主线程监听 StateFlow
//    withContext(Dispatchers.Main) {
//        // 只收集第一次流
//        val state = vm.loginResult.state.first { it !is SimpleUiState.Loading }
//        when (state) {
//            is SimpleUiState.Success -> {
//                val data = state.data
//
//            }
//            is SimpleUiState.Error -> {
//                showToast("错误 " + state.exception?.message)
//            }
//            else -> {}
//        }
//    }
}


fun getGuaGuaPersonInfo() : GuaGuaLogin? = try {
    val json = prefs.getString("GuaGuaPersonInfo",null)
    GsonInstance.fromJson(json, GuaGuaLoginResponse::class.java).data
} catch (_:Exception) {
    null
}