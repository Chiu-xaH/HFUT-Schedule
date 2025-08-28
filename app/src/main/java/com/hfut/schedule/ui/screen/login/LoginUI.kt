package com.hfut.schedule.ui.screen.login

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.network.UrlImageWithAutoOcr
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.xah.uicommon.component.status.LoadingUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.align.RowHorizontal
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.BottomTip
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//登录方法，auto代表前台调用
private fun loginClick(
    vm : LoginViewModel,
    username : String,
    inputAES : String,
    code : String,
    webVpn : Boolean,
    onRefresh : () -> Unit,
    onLoad : (Boolean) -> Unit,
    onResult : (String) -> Unit
) {
    val cookie = prefs.getString(if(!webVpn)"LOGIN_FLAVORING" else "webVpnKey", "")
    val outputAES = cookie?.let { it1 -> Encrypt.encryptAES(inputAES, it1) }
    val c = "LOGIN_FLAVORING=$cookie"


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
            else outputAES?.let { it1 -> vm.login(username, it1,c,code,webVpn) }
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
                                onResult("密码或验证码错误或软件Bug，可返回后再次进入重试")
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
    val webVpnCookie by DataStoreManager.webVpnCookies.collectAsState(initial = "")

    val w by vm.webVpnTicket.state.collectAsState()

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
        LaunchedEffect(webVpn,onRefresh,cookies) {
            imageUrl = "$url?timestamp=${System.currentTimeMillis()}"
        }
        // 请求图片
        Box(modifier = Modifier.clickable {
            // 点击重载
            imageUrl = "$url?timestamp=${System.currentTimeMillis()}"
        }) {
            UrlImageWithAutoOcr(url = imageUrl,cookie = cookies, width = 100.dp, height = 45.dp, onResult = onResult)
        }
    }
}



fun isAnonymity() : Boolean {
    val json = prefs.getString("json", "")
    return json?.contains("result") != true
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    vm : LoginViewModel,
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val context = LocalActivity.current
    var webVpn by remember { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val Savedusername = prefs.getString("Username", "")
    var username by remember { mutableStateOf(Savedusername ?: "") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = {
                    Text(
                        text = "CAS统一认证登录",
                    )
                        },
                actions = {
                    Row {
                        IconButton(onClick = {
                            Starter.startFix()
                        }) {
                            Icon(painterResource(id = R.drawable.build), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                },
                navigationIcon  = {
                    IconButton(onClick = {
                        context?.finish()
                    }) {
                        Icon(painterResource(id = R.drawable.close), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                    }
                },
                modifier = Modifier.topBarBlur(hazeState, MaterialTheme.colorScheme.surfaceContainer )
            )
        },
        bottomBar = {
            Column (modifier = Modifier.bottomBarBlur(hazeState, color = MaterialTheme.colorScheme.surfaceContainer)) {
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                CustomCard(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(vertical = APP_HORIZONTAL_DP),
                    color = MaterialTheme.colorScheme.surface.copy(.9f),
                ) {
                    if(username.startsWith(DateTimeManager.Date_yyyy)) {
                        TransplantListItem(
                            supportingContent = { Text("新生需先在网页登录过，确认自己的账号已经可以使用，才能登录聚在工大，如您已登陆过可忽略") },
                            headlineContent = { Text("点击跳转网页") },
                            leadingContent = { Icon(painterResource(R.drawable.warning), null) },
                            modifier = Modifier.clickable {
                                Starter.startWebUrl(MyApplication.CAS_LOGIN_URL)
                            },
                        )
                        PaddingHorizontalDivider()
                    }
                    TransplantListItem(
                        headlineContent = { Text("修改密码") },
                        overlineContent = { Text("CAS统一认证")},
                        leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                        modifier = Modifier.clickable { Starter.startWebView(MyApplication.CAS_LOGIN_URL + "cas/forget","忘记密码",null,R.drawable.lock_reset) },
                    )
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = { Text("外地访问") },
                        overlineContent = { Text("WebVpn")},
                        leadingContent = { Icon(painterResource(R.drawable.vpn_key),null) },
                        trailingContent = {
                            Switch(checked = webVpn,onCheckedChange = { ch -> webVpn = ch })
                        },
                        modifier = Modifier.clickable { webVpn = !webVpn },
                    )
                    PaddingHorizontalDivider()
                    Spacer(Modifier.height(CARD_NORMAL_DP*2))
                    BottomTip(
                        if(webVpn)"外地访问下暂时仅支持以WebVpn登录教务系统(后续扩展)" else "将同时登录 慧新易校,智慧社区,信息门户,教务系统 四个平台"
                    )
                    Spacer(Modifier.height(CARD_NORMAL_DP*2))
//                    TransplantListItem(
//                        headlineContent = { Text() },
//                        overlineContent = { Text("提示")},
//                    )
                }
            }
        }
    ) {innerPadding ->
        Box (modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState)) {
            TwoTextField(vm,webVpn,innerPadding,username,sharedTransitionScope,animatedContentScope) {
                username = it
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedWelcomeScreen() {
    val welcomeTexts = listOf(
        "你好", "(｡･ω･)ﾉﾞ", "欢迎使用", "ヾ(*ﾟ▽ﾟ)ﾉ","Hello", "٩(ˊωˋ*)و✧","Hola", "(⸝•̀֊•́⸝)", "Bonjour","＼(≧▽≦)ﾉ"
    )
    var currentIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // 每3秒切换
            currentIndex = (currentIndex + 1) % welcomeTexts.size
        }
    }

    // 界面布局

    Column(modifier = Modifier
        .padding(horizontal = APP_HORIZONTAL_DP)) {
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedContent(
            targetState = welcomeTexts[currentIndex],
            transitionSpec = {
                fadeIn(animationSpec = tween(AppAnimationManager.ANIMATION_SPEED)) togetherWith(fadeOut(animationSpec = tween(AppAnimationManager.ANIMATION_SPEED)))
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TwoTextField(
    vm : LoginViewModel,
    webVpn: Boolean,
    innerPadding : PaddingValues,
    username : String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onUsername : (String) -> Unit
) {
    var hidden by rememberSaveable { mutableStateOf(true) }
    var inputAES by rememberSaveable { mutableStateOf(prefs.getString("Password","") ?: "") }
    var inputCode by rememberSaveable { mutableStateOf( "") }
    // 创建一个动画值，根据按钮的按下状态来改变阴影的大小
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            sheetState = sheetState,
            shape = bottomSheetRound(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    BottomSheetTopBar("图片验证码自动填充")
                },) {innerPadding ->
                DownloadMLUI(innerPadding,null)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    val jSession by vm.jSessionId.state.collectAsState()
    var onRefresh by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(status) {
        status?.let { showToast(it) }
    }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())) {

        InnerPaddingHeight(innerPadding,true)

        CustomCard(
            color = MaterialTheme.colorScheme.surface.copy(1f)
        ) {
            Column (modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP)) {

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = username,
                    onValueChange = onUsername,
                    label = { Text("学号") },
                    singleLine = true,
                    // placeholder = { Text("请输入正确格式")},
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.person),
                            contentDescription = "Localized description"
                        )
                    },

                    trailingIcon = {
                        IconButton(onClick = {
                            onUsername("")
                            inputAES = ""
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = "description"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    value = inputAES,
                    onValueChange = { inputAES = it },
                    label = { Text("密码(信息门户)") },
                    singleLine = true,
                    colors = textFiledTransplant(),
                    visualTransformation = if (hidden) PasswordVisualTransformation()
                    else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.key),
                            contentDescription = "Localized description"
                        )
                    },
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
                CommonNetworkScreen(
                    jSession,
                    isFullScreen = false,
                    onReload = { },
                    loadingText = "正在检查是否需要图片验证码"
                ) {
                    val useCaptcha = (jSession as UiState.Success).data.needCaptcha
                    if (useCaptcha) {
                        Column {
                            Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
                            RowHorizontal {
                                TextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = APP_HORIZONTAL_DP),
                                    value = inputCode,
                                    onValueChange = { inputCode = it },
                                    label = { Text("验证码(不区分大小写)") },
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = textFiledTransplant(),
                                    leadingIcon = {
                                        Icon(
                                            painterResource(R.drawable.password),
                                            contentDescription = "Localized description"
                                        )
                                    },
                                    trailingIcon = {
                                        Box(modifier = Modifier.padding(5.dp)) {
                                            ImageCodeUI(webVpn, vm, onRefresh = onRefresh) {
                                                inputCode = it
                                            }
                                        }
                                    },
                                    supportingText = if (!prefs.getBoolean("SWITCH_ML", false)) {
                                        {
                                            Text(
                                                "点击下载模型文件以启用自动填充",
                                                modifier = Modifier.clickable {
                                                    showBottomSheet = true
                                                })
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))


        if(loading) {
            LoadingUI()
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP),
                horizontalArrangement = Arrangement.Center
            ) {
                if(isAnonymity()) {
                    LargeButton(
                        onClick = {
                            val cookie = prefs.getString("LOGIN_FLAVORING", "")
                            if (cookie != null) {
                                loginClick(vm,username,inputAES,inputCode,webVpn, onRefresh = { onRefresh++ }, onLoad = { loading = it }, onResult = { status = it})
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(.5f),
                        text = "登录",
                        icon = R.drawable.login
                    )
                    Spacer(modifier = Modifier.width(APP_HORIZONTAL_DP))
                    LargeButton(
                        onClick = {
                            scope.launch {
                                DataStoreManager.saveFastStart(true)
                                Starter.goToMain()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        text = "游客",
                        icon = R.drawable.partner_exchange,
                        iconModifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope=animatedContentScope, route = AppNavRoute.UseAgreement.route),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                } else {
                    Button(
                        onClick = {
                            val cookie = prefs.getString("LOGIN_FLAVORING", "")
                            if (cookie != null) {
                                loginClick(vm,username,inputAES,inputCode,webVpn, onRefresh = { onRefresh++ }, onLoad = { loading = it }, onResult = { status = it})
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("登录")
                    }
                }
            }
        }
        InnerPaddingHeight(innerPadding,false)
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