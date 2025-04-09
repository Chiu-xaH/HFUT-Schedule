package com.hfut.schedule.ui.activity.shower.login

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.logic.beans.guagua.GuaGuaLogin
import com.hfut.schedule.logic.beans.guagua.GuaGuaLoginResponse
import com.hfut.schedule.logic.beans.guagua.GuaguaLoginMsg
import com.hfut.schedule.logic.enums.ShowerScreen
import com.hfut.schedule.logic.utils.parse.Encrypt
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter.loginGuaGua
import com.hfut.schedule.logic.utils.Starter.startGuaGua
import com.hfut.schedule.ui.activity.shower.function.EditLoginCode

import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.showToast
import com.hfut.schedule.ui.utils.navigateAndClear
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerLogin(vm : GuaGuaViewModel,netVm : NetWorkViewModel,navHostController: NavHostController) {

    val context = LocalActivity.current


    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "登录  ",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            //style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.logout), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon  = {
                    Column(modifier = Modifier
                        .padding(horizontal = 23.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "呱呱物联",
                            fontSize = 38.sp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            GuaGuaLoginUI(vm,netVm,navHostController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaLoginUI(vm : GuaGuaViewModel,netVm : NetWorkViewModel,navHostController: NavHostController) {

    var hidden by rememberSaveable { mutableStateOf(true) }

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val Savedusername = prefs.getString("PHONENUM", "")
    val Savedpassword = prefs.getString("GuaGuaPsk","")

    var username by remember { mutableStateOf(Savedusername ?: "") }
    var inputAES by remember { mutableStateOf(Savedpassword ?: "") }


    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    var showTip by remember { mutableStateOf("请新用户先前往微信小程序-呱呱物联注册") }
    Column(modifier = Modifier.fillMaxWidth()) {
        val interactionSource = remember { MutableInteractionSource() }
        val interactionSource2 = remember { MutableInteractionSource() } // 创建一个
        val isPressed by interactionSource.collectIsPressedAsState()
        val isPressed2 by interactionSource2.collectIsPressedAsState()

        val scale = animateFloatAsState(
            targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "" // 使用弹簧动画
        )

        val scale2 = animateFloatAsState(
            targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "" // 使用弹簧动画
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                value = username,
                onValueChange = { username = it },
                label = { Text("手机号" ) },
                singleLine = true,
                // placeholder = { Text("请输入正确格式")},
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
                leadingIcon = { Icon( painterResource(R.drawable.call), contentDescription = "Localized description") },

                trailingIcon = {
                    IconButton(onClick = {
                        username = ""
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


        Spacer(modifier = Modifier.height(30.dp))

        Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center){

            Button(
                onClick = {
                    LoginGuaGuaClick(username,inputAES,vm,navHostController)
                }, modifier = Modifier.scale(scale.value),
                interactionSource = interactionSource

            ) { Text("登录") }
        }
        Spacer(modifier = Modifier.height(30.dp))
        DividerTextExpandedWith(text = "备用登录方式") {
            Row(
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                EditLoginCode(true,{
                    saveString("PHONENUM",username)
                    CoroutineScope(Job()).launch {
                        async { netVm.getGuaGuaUserInfo() }.await()
                        async {
                            Handler(Looper.getMainLooper()).post {
                                netVm.guaguaUserInfo.observeForever { result ->
                                    if (result?.contains("成功") == true) {
                                        saveString("GuaGuaPersonInfo",result)
                                        navHostController.navigateAndClear(ShowerScreen.HOME.name)
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
}

fun LoginGuaGuaClick(phoneNumber : String,psk : String,vm: GuaGuaViewModel,navHostController: NavHostController) {
    val inputPSK = Encrypt.md5Hash(psk).uppercase(Locale.getDefault())
    saveString("PHONENUM",phoneNumber)
    saveString("GuaGuaPsk",psk)

    CoroutineScope(Job()).launch {
        async { vm.login(phoneNumber,inputPSK) }.await()
        async {
            Handler(Looper.getMainLooper()).post {
                vm.loginResult.observeForever { result ->
                    if (result != null && result.contains("成功")) {
                        saveLoginCode(result)
                        navHostController.navigateAndClear(ShowerScreen.HOME.name)
                        showToast("登录成功")
                    } else if(result != null && result.contains("error")) {
                        showToast(getLoginedMsg(result))
                    }
                }
            }
        }
    }
}



fun saveLoginCode(json : String) : GuaGuaLogin? {
    return try {
        val data = Gson().fromJson(json, GuaGuaLoginResponse::class.java).data
        saveString("GuaGuaPersonInfo",json)
        saveString("loginCode",data.loginCode)
        return data
    } catch (_:Exception) {
        null
    }
}

fun getGuaGuaPersonInfo() : GuaGuaLogin? {
    return try {
        val json = prefs.getString("GuaGuaPersonInfo",null)
        Gson().fromJson(json, GuaGuaLoginResponse::class.java).data
    } catch (_:Exception) {
        null
    }
}

fun getLoginedMsg(json : String) : String {
    return try {
        Gson().fromJson(json, GuaguaLoginMsg::class.java).message ?: "空"
    } catch (e:Exception) {
        e.toString();
    }
}