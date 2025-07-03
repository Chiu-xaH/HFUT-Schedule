package com.hfut.schedule.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.ParseJsons.useCaptcha
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.custom.BottomSheetTopBar
import com.hfut.schedule.ui.component.custom.LoadingUI
import com.hfut.schedule.ui.component.MyCustomCard
import com.hfut.schedule.ui.component.ShareTwoContainer2D
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.URLImageWithOCR
 
import com.hfut.schedule.ui.component.cardNormalColor
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.navigateAndClear
import com.hfut.schedule.viewmodel.network.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//登录方法，auto代表前台调用
private fun loginClick(vm : LoginViewModel, username : String, inputAES : String, code : String, webVpn : Boolean, onRefresh : () -> Unit, onLoad : (Boolean) -> Unit, onResult : (String) -> Unit) {
    val cookie = prefs.getString(if(!webVpn)"cookie" else "webVpnKey", "")
    val outputAES = cookie?.let { it1 -> Encrypt.encryptAES(inputAES, it1) }
    val ONE = "LOGIN_FLAVORING=$cookie"

    //登陆判定机制
    CoroutineScope(Job()).launch {
        launch {
            //保存账密
            saveString("Username",username)
            saveString("Password",inputAES)
        }
        async {
            //登录
            if (username.length != 10) showToast("请输入正确的账号")
            else outputAES?.let { it1 -> vm.login(username, it1,ONE,code,webVpn) }
        }.await()
        async { onLoad(true) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.code.observeForever { result ->
                    if(result != null) {
                        onLoad(false)
                        when(result) {
                            "XXX" -> {
                                onResult("可能是教务或校园网问题,请再次尝试登录")
                                vm.getCookie()
                            }
                            "401" -> {
                                onResult("密码或验证码错误或教务问题")
                                onRefresh()
                                vm.getCookie()
                            }
                            "200" -> {
                                if(!webVpn)
                                    onResult("请输入正确的账号")
                                else {
                                    onResult("登陆成功")
                                    CoroutineScope(Job()).launch {
                                        vm.loginJxglstu()
                                    }
                                    Starter.loginSuccess()
                                }
                            }
                            "302" -> {
                                when {
                                    vm.location.value.toString() == MyApplication.REDIRECT_URL -> {
                                        onResult("登陆失败")
                                        vm.getCookie()
                                    }
                                    vm.location.value.toString().contains("ticket") -> {
                                        onResult("登陆成功")
                                        Starter.loginSuccess(webVpn)
                                    }
                                    else -> {
                                        onResult("未知响应,登陆失败")
                                        vm.getCookie()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageCodeUI(webVpn : Boolean, vm: LoginViewModel, onRefresh: Int = 1, onResult : (String) -> Unit) {
    // refresh当webVpn关闭才起效，开启时不需要refresh，直接重载图片
    val jSessionId by vm.jSessionId.state.collectAsState()
    val webVpnCookie by DataStoreManager.webVpnCookie.collectAsState(initial = "")
//    var refresh by remember { mutableStateOf(true) }
//    if(webVpn) {
//        refresh = false
//    } else {
//        if(refresh) {
//            LaunchedEffect(Unit) {
//                launch {
//                    Handler(Looper.getMainLooper()).post{
//                        vm.jSessionId.observeForever { result ->
//                            if(result != null) {
//                                refresh = false
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    val w by vm.webVpnTicket.state.collectAsState()
//    prefs.getString("webVpnTicket", "")
    val refresh = if(webVpn) {
        w is UiState.Loading
    } else {
        jSessionId is UiState.Loading
    }
    if(refresh) {
        CircularProgressIndicator()
    } else  {
        val url = (
                if(!webVpn) MyApplication.CAS_LOGIN_URL
                else MyApplication.WEBVPN_URL + "http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/"
                ) + "cas/vercode"
        // 让 URL 可变，每次点击时更新
        var imageUrl by remember { mutableStateOf("$url?timestamp=${System.currentTimeMillis()}") }
        val cookies = if(webVpn) MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie else {
            (jSessionId as? UiState.Success)?.data?.jSession
        }

        // webVpn开关变化时重载
        LaunchedEffect(webVpn,onRefresh) {
            imageUrl = "$url?timestamp=${System.currentTimeMillis()}"
        }
        // 请求图片
        Box(modifier = Modifier.clickable {
            // 点击重载
            imageUrl = "$url?timestamp=${System.currentTimeMillis()}"
        }) {
            URLImageWithOCR(url = imageUrl,cookie = cookies, width = 100.dp, height = 45.dp, onResult = onResult)
        }
    }
}



fun isAnonymity() : Boolean {
    val json = prefs.getString("json", "")
    return json?.contains("result") != true
}


enum class MainNav {
    HOME,USE_AGREEMENT,GUEST,GRADE
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(vm : LoginViewModel, navController : NavHostController) {
    val context = LocalActivity.current
    var showBadge by remember { mutableStateOf(false) }
    if (AppVersion.getVersionName() != prefs.getString("version", AppVersion.getVersionName())) showBadge = true
    var webVpn by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "教务登录  ",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                        },
                actions = {
                    Row {
                        IconButton(onClick = {
                            Starter.startFix()
                        }) {
                            Icon(painterResource(id = R.drawable.build), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            context?.finish()
                        }) {
                            Icon(painterResource(id = R.drawable.logout), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                },
                navigationIcon  = {
                    AnimatedWelcomeScreen()
                }
            )
        },
    ) {innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TwoTextField(vm,navController,webVpn)
            StyleCardListItem(
                headlineContent = { Text("外地访问") },
                leadingContent = { Icon(painterResource(R.drawable.vpn_key),null) },
                trailingContent = {
                    Switch(checked = webVpn,onCheckedChange = { ch -> webVpn = ch })
                },
                modifier = Modifier.clickable { webVpn = !webVpn },
                cardModifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(horizontal = 25.dp- APP_HORIZONTAL_DP)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedWelcomeScreen() {
    val welcomeTexts = listOf(
        "你好", "(｡･ω･)ﾉﾞ", "欢迎使用", "ヾ(*ﾟ▽ﾟ)ﾉ","Hello", "٩(ˊωˋ*)و✧","Hola", "(⸝•̀֊•́⸝)", "Bonjour","＼(≧▽≦)"
    )
    var currentIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // 每3秒切换
            currentIndex = (currentIndex + 1) % welcomeTexts.size
        }
    }

    // 界面布局

    Column(modifier = Modifier
        .padding(horizontal = 23.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedContent(
            targetState = welcomeTexts[currentIndex],
            transitionSpec = {
                fadeIn(animationSpec = tween(500)) with fadeOut(animationSpec = tween(500))
            }, label = ""
        ) { targetText ->
            Text(
                text = targetText,
                fontSize = 38.sp,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }


}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoTextField(vm : LoginViewModel, navHostController: NavHostController,webVpn: Boolean) {

    var hidden by rememberSaveable { mutableStateOf(true) }

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val Savedusername = prefs.getString("Username", "")
    val Savedpassword = prefs.getString("Password","")

    var username by remember { mutableStateOf(Savedusername ?: "") }
    var inputAES by remember { mutableStateOf(Savedpassword ?: "") }
    var inputCode by remember { mutableStateOf( "") }
    val switch_open = SharedPrefs.prefs.getBoolean("SWITCH_ML",false)
    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("图片验证码自动填充")
                },) {innerPadding ->
                DownloadMLUI(innerPadding)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    val jSession by vm.jSessionId.state.collectAsState()
    // 跟随JSON

//    val useCaptcha = if()

    var onRefresh by remember { androidx.compose.runtime.mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(status) {
        status?.let { showToast(it) }
    }
    val scope = rememberCoroutineScope()


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

        Spacer(modifier = Modifier.height(10.dp))

        RowHorizontal {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                value = username,
                onValueChange = { username = it },
                label = { Text("学号" ) },
                singleLine = true,
                // placeholder = { Text("请输入正确格式")},
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
                leadingIcon = { Icon( painterResource(R.drawable.person), contentDescription = "Localized description") },

                trailingIcon = {
                    IconButton(onClick = {
                        username = ""
                        inputAES = ""
                    }) { Icon(painter = painterResource(R.drawable.close), contentDescription = "description") }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        RowHorizontal {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 25.dp),
                value = inputAES,
                onValueChange = { inputAES = it },
                label = { Text("信息门户密码") },
                singleLine = true,
                colors = textFiledTransplant(),
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
        val captchaUI = @Composable {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                RowHorizontal {
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 25.dp),
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        label = { Text("图片验证码" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                        leadingIcon = { Icon( painterResource(R.drawable.password), contentDescription = "Localized description") },
                        trailingIcon = {
                            Box(modifier = Modifier.padding(5.dp)) {
                                ImageCodeUI(webVpn,vm, onRefresh =onRefresh ) {
                                    inputCode = it
                                }
                            }
                        },
                        supportingText = if(!switch_open) {
                            {
                                Text("点击下载模型文件以启用自动填充", modifier = Modifier.clickable {
                                    showBottomSheet = true
                                })
                            }
                        } else null
                    )
                }
            }
        }
        CommonNetworkScreen(jSession,isFullScreen = false, onReload = { }, loadingText = "正在检查是否需要图片验证码") {
            val useCaptcha = (jSession as UiState.Success).data.needCaptcha
            if(useCaptcha) {
                captchaUI()
            }
        }


        Spacer(modifier = Modifier.height(25.dp))

        if(loading) {
            LoadingUI()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val cookie = SharedPrefs.prefs.getString("LOGIN_FLAVORING", "")
                        if (cookie != null) loginClick(vm,username,inputAES,inputCode,webVpn, onRefresh = { onRefresh++ }, onLoad = { loading = it }, onResult = { status = it})
                    },
                    modifier = Modifier.fillMaxWidth().scale(scale.value).let { if(isAnonymity()) it.weight(.5f) else it },
                    interactionSource = interactionSource,
                    shape = MaterialTheme.shapes.medium,
                ) { Text( "登录") }

                if(isAnonymity()) {
                    saveBoolean("SWITCHFASTSTART",true,true)
                    Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))


                    FilledTonalButton(
                        onClick = {
                            Starter.goToMain()
                        },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().scale(scale2.value).weight(.5f),
                        interactionSource = interactionSource2,
                        ) { Text("游客") }
                }
            }
        }
    }
}

@Composable
fun GetComponentHeightExample() {
    val size = remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                // 获取组件的尺寸
                size.value = coordinates.size // IntSize
            }
    ) {
        Checkbox(checked = false, onCheckedChange = {})
    }

    // 将高度以dp形式打印出来
    val heightInDp = with(density) { size.value.height.toDp() }
    println("组件的高度是：$heightInDp dp")
}