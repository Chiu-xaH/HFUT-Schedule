package com.hfut.schedule.ui.Activity.shower

import android.app.Activity
import android.content.Context
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.Encrypt
import com.hfut.schedule.ui.UIUtils.MyToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowerLogin(vm : LoginViewModel) {

    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("登录 呱呱物联") },
                actions = {

                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(painterResource(id = R.drawable.logout), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
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
fun GuaGuaLoginUI(vm : LoginViewModel) {

    var hidden by rememberSaveable { mutableStateOf(true) }

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val Savedusername = prefs.getString("PhoneNumber", "")
    val Savedpassword = prefs.getString("GuaGuaPsk","")

    var username by remember { mutableStateOf(Savedusername ?: "") }
    var inputAES by remember { mutableStateOf(Savedpassword ?: "") }
    var webVpn by remember { mutableStateOf(false) }

    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小

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
                leadingIcon = { Icon( painterResource(R.drawable.person), contentDescription = "Localized description") },

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
                supportingText = { Text("密码为微信小程序-呱呱物联所注册")},
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
                    LoginGuaGuaClick(username,inputAES)
                }, modifier = Modifier.scale(scale.value),
                interactionSource = interactionSource

            ) { Text("登录") }

        //    Spacer(modifier = Modifier.width(15.dp))

          //  FilledTonalButton(
            //    onClick = { SavedClick() },
              //  modifier = Modifier.scale(scale2.value),
                //interactionSource = interactionSource2,

               // ) { Text("免登录") }
        }
        // Spacer(modifier = Modifier.height(10.dp))
        //GetComponentHeightExample()
    }
}

fun LoginGuaGuaClick(phoneNumber : String,psk : String) {
    val inputPSK = Encrypt.md5Hash(psk)
    MyToast("正在设计功能与界面 敬请期待")
}