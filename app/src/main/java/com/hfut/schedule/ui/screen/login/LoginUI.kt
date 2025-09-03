package com.hfut.schedule.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.network.StatusCode
import com.hfut.schedule.logic.util.network.Encrypt
import com.hfut.schedule.logic.util.network.state.CONNECTION_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.CasInHFUT
import com.hfut.schedule.logic.util.network.state.TIMEOUT_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImageWithAutoOcr
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//登录方法，auto代表前台调用
private fun loginClick(
    context: Context,
    vm : LoginViewModel,
    username : String,
    inputAES : String,
    code : String,
    webVpn : Boolean,
    onLoad : (Boolean) -> Unit,
    onResult : (String) -> Unit,
    scope: CoroutineScope
) {
    val cookie = prefs.getString(if(!webVpn)"LOGIN_FLAVORING" else "webVpnKey", "")
    val outputAES = cookie?.let { it1 -> Encrypt.encryptAES(inputAES, it1) }
    val loginFlavoring = "LOGIN_FLAVORING=$cookie"


    //登陆判定机制
    scope.launch {
        launch {
            //保存账密
            saveString("Username",username)
            saveString("Password",inputAES)
        }
        async {
            //登录
            if (username.length != 10)
                showToast("请输入正确的账号")
            else
                outputAES?.let { it1 -> vm.login(username, it1,loginFlavoring,code,webVpn) }
        }.await()
        async { onLoad(true) }.await()
        async {
            Handler(Looper.getMainLooper()).post{
                vm.code.observeForever { result ->
                    if(result != null) {
                        scope.launch {
                            onLoad(false)
                            when(result) {
                                "XXX" -> {
                                    onResult("可能是教务封网问题,请再次尝试登录")
                                    refresh(vm)
                                }
                                StatusCode.UNAUTHORIZED.code.toString() -> {
                                    scope.launch { refresh(vm) }
                                    refresh(vm)
                                }
                                StatusCode.OK.code.toString() -> {
                                    if(CasInHFUT.excludeJxglstu) {
                                        onResult("登陆成功")
                                        Starter.loginSuccess(context)
                                    } else if(!webVpn) {
                                        onResult("请输入正确的账号")
                                        refresh(vm)
                                    } else {
                                        onResult("登陆成功")
                                        vm.loginJxglstu()
                                        Starter.loginSuccess(context)
                                    }
                                }
                                StatusCode.REDIRECT.code.toString() -> {
                                    when {
                                        vm.location.value.toString().contains("ticket") -> {
                                            onResult("登陆成功")
                                            Starter.loginSuccess(context,webVpn)
                                        }
                                        else -> {
                                            onResult("登陆失败")
                                            refresh(vm)
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
}

@Composable
private fun ImageCodeUI(webVpn : Boolean, vm: LoginViewModel, onResult : (String) -> Unit) {
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
        LaunchedEffect(webVpn, GlobalUIStateHolder.refreshImageCode,cookies) {
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
private val TAB_LOGIN = 0
private val TAB_SETTRINGS = 1

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    vm : LoginViewModel,
    networkVm : NetWorkViewModel,
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
    var webVpn by rememberSaveable { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val Savedusername = prefs.getString("Username", "")
    var username by remember { mutableStateOf(Savedusername ?: "") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val jxglstuStatus by produceState<Int?>(initialValue = null) {
        value = networkVm.checkJxglstuCanUse()
    }
    val showTip = username.startsWith(DateTimeManager.Date_yyyy) && isAnonymity()
    val showTip2 = jxglstuStatus != null && jxglstuStatus!! >= 400
    var tab by remember { mutableIntStateOf(TAB_LOGIN) }
    val context = LocalContext.current

    if(tab == TAB_SETTRINGS) {
        HazeBottomSheet (
            onDismissRequest = { tab = TAB_LOGIN },
            showBottomSheet = true,
            autoShape = false,
            hazeState = hazeState
        ) {
            Column(
                modifier = Modifier
//                        .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
//                        .fillMaxSize()
            ) {
                HazeBottomSheetTopBar("登录选项", isPaddingStatusBar = false)
//                    InnerPaddingHeight(innerPadding,true)
                DividerTextExpandedWith("状态") {
                    CustomCard(
                        modifier = Modifier,
                        color = cardNormalColor()
//                            color = MaterialTheme.colorScheme.surface,
                    ) {
                        val status: Pair<Color?,String> = if(jxglstuStatus == null) {
                            Pair(null,"正在检查与教务系统的连接状态")
                        } else if(jxglstuStatus!! < 400) {
                            Pair(null,"教务系统连接状态正常,可直接登录")
                        } else if(jxglstuStatus!! in 500 until 600) {
                            Pair(MaterialTheme.colorScheme.errorContainer,"教务系统拒绝响应,可能是系统在维护,或使用了爬虫工具导致暂时性的网络IP封禁,请更换网络重新进入或等待几小时后刷新教务")
                        } else if(jxglstuStatus == TIMEOUT_ERROR_CODE) {
                            Pair(MaterialTheme.colorScheme.errorContainer,"教务系统封网,请换为校园网重新使用或打开外地访问以刷新教务")
                        } else if(jxglstuStatus == CONNECTION_ERROR_CODE) {
                            Pair(MaterialTheme.colorScheme.errorContainer,"网络连接失败,是否连接了网络?")
                        } else {
                            Pair(MaterialTheme.colorScheme.errorContainer,"未知错误")
                        }
                        TransplantListItem(
                            headlineContent = { Text(status.second ) },
                            modifier = Modifier.clickable {
                                Starter.startWlanSettings(context)
                            },
                            leadingContent = {
                                if(jxglstuStatus == null) {
                                    LoadingIcon()
                                } else {
                                    BadgedBox(
                                        badge = {
                                            Badge()
                                        },
                                        content = {
                                            Icon(painterResource(R.drawable.warning),null)
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
                DividerTextExpandedWith("选项") {
                    CustomCard(
                        modifier = Modifier,
                        color = cardNormalColor()
//                            color = MaterialTheme.colorScheme.surface,
                    ) {
                        if(!webVpn) {
                            TransplantListItem(
                                supportingContent = { Text("打开开关后,将跳过封网的教务系统,只对信息门户和智慧社区刷新(慧新易校后续支持),用于您离校且在封网的情况下需要刷新其他平台") },
                                headlineContent = { Text("跳过教务系统") },
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        CasInHFUT.excludeJxglstu = !CasInHFUT.excludeJxglstu
                                        refresh(vm)
                                    }
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.arrow_split),null)
                                },
                                trailingContent = {
                                    Switch(checked = CasInHFUT.excludeJxglstu,onCheckedChange = { ch ->
                                        scope.launch {
                                            CasInHFUT.excludeJxglstu = !CasInHFUT.excludeJxglstu
                                            refresh(vm)
                                        }
                                    })
                                },
                            )
                            PaddingHorizontalDivider()
                        }

                        //
                        if(showTip) {
                            TransplantListItem(
                                supportingContent = { Text("新生需先在网页教务系统或信息门户登录过(点击跳转网页),确认账号已可以使用,如您已登陆过可忽略") },
                                headlineContent = { Text("新生提示") },
                                leadingContent = {
                                    BadgedBox(
                                        badge = {
                                            Badge()
                                        },
                                        content = {
                                            Icon(painterResource(R.drawable.info), null)
                                        }
                                    )
                                },
                                modifier = Modifier.clickable {
                                    Starter.startWebUrl(context,MyApplication.CAS_LOGIN_URL)
                                },
                            )
                            PaddingHorizontalDivider()
                        }
                        TransplantListItem(
                            headlineContent = { Text("外地访问(WebVpn)") },
                            supportingContent = { Text("外地访问下暂时仅支持刷新教务系统,不受教务封网限制")},
                            leadingContent = { Icon(painterResource(R.drawable.vpn_key),null) },
                            trailingContent = {
                                Switch(checked = webVpn,onCheckedChange = { ch -> webVpn = ch })
                            },
                            modifier = Modifier.clickable { webVpn = !webVpn },
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = { Text("修改密码") },
                            supportingContent = { Text("修改或重置CAS统一认证密码")},
                            leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                            modifier = Modifier.clickable { Starter.startWebView(context,MyApplication.CAS_LOGIN_URL + "cas/forget","忘记密码",null,R.drawable.lock_reset) },
                        )
                    }
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP*2).navigationBarsPadding())
//                    InnerPaddingHeight(innerPadding,false)
            }
//            Scaffold(
//                modifier = Modifier.fillMaxSize(),
//                containerColor = Color.Transparent,
//                topBar = {
//
//                },) { innerPadding ->
//
//            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = {
                    ScrollText(text = "CAS统一认证登录")
                        },
                actions = {
                    Row {
                        IconButton(
                            onClick = { tab = TAB_LOGIN },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if(tab == TAB_LOGIN) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    Color. Unspecified
                                }
                            ),
                        ) {
                            Icon(painterResource(R.drawable.login),null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(25.5.dp))
                        }
                        IconButton(
                            onClick = { tab = TAB_SETTRINGS },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if(tab == TAB_SETTRINGS) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    Color. Unspecified
                                }
                            ),
                        ) {
                            BadgedBox(
                                badge = {
                                    if(showTip || showTip2) {
                                        Badge()
                                    }
                                },
                                content = {
                                    Icon(painterResource(R.drawable.settings), null,tint = MaterialTheme.colorScheme.primary)
                                }
                            )
                        }
                        IconButton(onClick = {
                            Starter.startFix(context)
                        }) {
                            Icon(painterResource(id = R.drawable.build), contentDescription = "",tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon  = {
                    IconButton(onClick = {
                        activity?.finish()
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
                LargeButton(
                    onClick = {
                        tab = when(tab) {
                            TAB_LOGIN -> TAB_SETTRINGS
                            else -> TAB_LOGIN
                        }
                    },
                    text = when(tab) {
                        TAB_LOGIN -> "选项" + (if(showTip || showTip2) " (发现重要提示)" else "")
                        else -> "主界面"
                    },
                    modifier = Modifier.fillMaxWidth().padding(APP_HORIZONTAL_DP).navigationBarsPadding(),
                    icon = when(tab) {
                        TAB_LOGIN -> R.drawable.settings
                        else -> R.drawable.login
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.9f),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    ) {innerPadding ->
        Box (modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState)) {
            Column {
                TwoTextField(vm,webVpn,innerPadding,username,sharedTransitionScope,animatedContentScope) {
                    username = it
                }
            }
//            when(tab) {
//                TAB_LOGIN -> {
//
//                }
//                TAB_SETTRINGS -> {
//                    Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
//
//                    }
//                }
//            }
        }
    }
}


private suspend fun refresh(vm: LoginViewModel) = withContext(Dispatchers.IO) {
    launch {
        vm.executionAndSession.clear()
        vm.getCookie()
    }
    launch { GlobalUIStateHolder.refreshImageCode++ }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun TwoTextField(
    vm : LoginViewModel,
    webVpn: Boolean,
    innerPadding : PaddingValues,
    username : String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onUsername : (String) -> Unit
) {
    val context = LocalContext.current
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
    var loading by rememberSaveable { mutableStateOf(false) }
    var status by rememberSaveable { mutableStateOf<String?>(null) }

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
                                            ImageCodeUI(webVpn, vm,) {
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
                                loginClick(context,vm,username,inputAES,inputCode,webVpn, onLoad = { loading = it }, onResult = { status = it},scope)
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
                                Starter.goToMain(context)
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
                                loginClick(context,vm,username,inputAES,inputCode,webVpn, onLoad = { loading = it }, onResult = { status = it}, scope)
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

