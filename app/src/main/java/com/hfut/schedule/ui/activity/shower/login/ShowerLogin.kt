package com.hfut.schedule.ui.activity.shower.login

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.logic.beans.guaGua.GuaGuaLogin
import com.hfut.schedule.logic.beans.guaGua.GuaGuaLoginResponse
import com.hfut.schedule.logic.beans.guaGua.GuaguaLoginMsg
import com.hfut.schedule.logic.utils.Encrypt
import com.hfut.schedule.logic.utils.SharePrefs.saveString
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter.startGuagua
import com.hfut.schedule.ui.activity.shower.function.EditLoginCode

import com.hfut.schedule.ui.utils.BottomTip
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerLogin(vm : GuaGuaViewModel) {

    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("登录-呱呱物联") },
                actions = {
                    Row {
                        IconButton(onClick = {
                            (context as? Activity)?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.logout), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            GuaGuaLoginUI(vm)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaLoginUI(vm : GuaGuaViewModel) {

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
                    .padding(horizontal = 40.dp),
                value = username,
                onValueChange = { username = it },
                label = { Text("手机号" ) },
                singleLine = true,
                // placeholder = { Text("请输入正确格式")},
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
                leadingIcon = { Icon( painterResource(R.drawable.call), contentDescription = "Localized description") },

                trailingIcon = {
                    IconButton(onClick = {
                        username = ""
                        inputAES = ""
                    }) { Icon(painter = painterResource(R.drawable.close), contentDescription = "description") }
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 40.dp),
                value = inputAES,
                onValueChange = { inputAES = it },
                label = { Text("密码") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
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
                    LoginGuaGuaClick(username,inputAES,vm)
                }, modifier = Modifier.scale(scale.value),
                interactionSource = interactionSource

            ) { Text("登录") }
        }
        Spacer(modifier = Modifier.height(30.dp))
        DividerText(text = "备用登录方式")
        EditLoginCode()
        BottomTip(str = "先填写上面的手机号 无需输入密码 点击登录")
        BottomTip(str = "输入loginCode后退出 再次从查询中心-洗浴进入即可")
    }
}

fun LoginGuaGuaClick(phoneNumber : String,psk : String,vm: GuaGuaViewModel) {
    val inputPSK = Encrypt.md5Hash(psk).toUpperCase()
    saveString("PHONENUM",phoneNumber)
    saveString("GuaGuaPsk",psk)

    CoroutineScope(Job()).launch {
        async { vm.login(phoneNumber,inputPSK) }.await()
        async {
            Handler(Looper.getMainLooper()).post {
                vm.loginResult.observeForever { result ->
                    if (result != null && result.contains("成功")) {
                        saveLoginCode(result)
                        startGuagua()
                        MyToast("登录成功")
                    } else if(result != null && result.contains("error")) {
                        MyToast(getLoginedMsg(result))
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