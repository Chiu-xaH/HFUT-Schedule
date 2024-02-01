package com.hfut.schedule.ui.ComposeUI.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.activity.LoginSuccessAcitivity
import com.hfut.schedule.activity.SavedCoursesActivity
import com.hfut.schedule.logic.Encrypt.AESEncrypt
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Settings.FirstCube
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
fun LoginClick(vm : LoginViewModel,username : String,inputAES : String) {
   // Log.d("input",inputAES)
    val cookie = prefs.getString("cookie", "")

    val outputAES = cookie?.let { it1 -> AESEncrypt.encrypt(inputAES, it1) }

    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if(sp.getString("Username","") != username){ sp.edit().putString("Username", username).apply() }
    if(sp.getString("Password","") != inputAES){ sp.edit().putString("Password", inputAES).apply() }
    val ONE = "LOGIN_FLAVORING=" + cookie
    outputAES?.let { it1 -> vm.login(username, it1,ONE) }


    CoroutineScope(Job()).launch {

        delay(1250)


        // vm.code.value?.let { Log.d("代码", it) }

        if (vm.code.value.toString() == "XXX" || vm.code.value == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(MyApplication.context, "连接Host失败,请再次尝试登录", Toast.LENGTH_SHORT).show()
                vm.getCookie()
            }

        }
        if (vm.code.value.toString() == "401")
            withContext(Dispatchers.Main) {
                Toast.makeText(MyApplication.context, "账号或密码错误,超过五次会冻结五分钟", Toast.LENGTH_SHORT).show()
                vm.getCookie()
            }


        if (vm.code.value.toString() == "200" || username.length != 10)
            withContext(Dispatchers.Main) { Toast.makeText(MyApplication.context, "请输入正确的账号", Toast.LENGTH_SHORT) .show() }

        if (vm.code.value.toString() == "302") {

            if (vm.location.value.toString() == MyApplication.RedirectURL) {
                Toast.makeText(MyApplication.context, "重定向失败,请重新登录", Toast.LENGTH_SHORT).show()
                vm.getCookie()
            }

            if (vm.location.value.toString().contains("ticket")) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(MyApplication.context, "登陆成功", Toast.LENGTH_SHORT).show()

                    val it = Intent(MyApplication.context, LoginSuccessAcitivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra("Grade", username.substring(2, 4))
                    }
                    MyApplication.context.startActivity(it)
                }
            }

            else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(MyApplication.context, "重定向失败,请重新登录", Toast.LENGTH_SHORT).show()
                    vm.getCookie()}
            }
        }
    }
}


fun SavedClick() {
    val json = prefs.getString("json", "")
    if (json?.contains("result") == true) {
        val it = Intent(MyApplication.context, SavedCoursesActivity::class.java)
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.context.startActivity(it)
    } else Toast.makeText(MyApplication.context,"本地数据为空,请登录以更新数据", Toast.LENGTH_SHORT).show()
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUI(vm : LoginViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var showBadge by remember { mutableStateOf(false) }
    if (MyApplication.version != prefs.getString("version", MyApplication.version)) showBadge = true

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("选项") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ){
                    FirstCube()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("肥工教务通") },
                actions = {
                  //  Row {
                        TextButton(onClick = {
                            showBottomSheet = true
                        }){
                            BadgedBox(badge = {
                                if (showBadge)
                                Badge(modifier = Modifier.size(5.dp))
                            }) { Icon(painterResource(id = R.drawable.cube), contentDescription = "主页") }
                        }
                    //    Text(text = "   ")
                   /// }
                   
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) { TwoTextField(vm) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoTextField(vm : LoginViewModel) {

    var hidden by rememberSaveable { mutableStateOf(true) }

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val Savedusername = prefs.getString("Username", "")
    val Savedpassword = prefs.getString("Password","")

    var username by remember { mutableStateOf(Savedusername ?: "") }
    var inputAES by remember { mutableStateOf(Savedpassword ?: "") }

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
                onValueChange = {username = it },
                label = { Text("学号" ) },
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
                label = { Text("信息门户密码") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                    unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                ),
                //  supportingText = { Text("密码为信息门户")},
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
                    val cookie = SharePrefs.prefs.getString("cookie", "")
                    if (cookie != null) LoginClick(vm,username,inputAES)
                    }, modifier = Modifier.scale(scale.value),
                interactionSource = interactionSource

            ) { Text("教务登录") }

            Spacer(modifier = Modifier.width(15.dp))

            FilledTonalButton(
                onClick = { SavedClick() },
                modifier = Modifier.scale(scale2.value),
                interactionSource = interactionSource2,

                ) { Text("免登录") }
        }
    }
}