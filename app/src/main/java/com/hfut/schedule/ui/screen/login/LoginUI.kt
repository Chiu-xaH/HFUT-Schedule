package com.hfut.schedule.ui.screen.login

//import androidx.compose.ui.tooling.preview.Preview
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.network.util.StatusCode
import com.hfut.schedule.logic.util.development.getKeyStackTrace
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.network.state.CONNECTION_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.TIMEOUT_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.UrlImageWithAutoOcr
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.card.function.main.RefreshHuiXin
import com.hfut.schedule.ui.screen.home.cube.sub.DownloadMLUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.corner.bottomSheetRound
import com.hfut.schedule.ui.style.special.bottomBarBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.component.text.BottomTip
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.URL

//登录方法，auto代表前台调用
private fun loginClick(
    context: Context,
    vm : LoginViewModel,
    username : String,
    inputAES : String,
    code : String,
    onLoad : (Boolean) -> Unit,
    onResult : (String) -> Unit,
    scope: CoroutineScope
) {
    val cookie = prefs.getString(if(!GlobalUIStateHolder.webVpn)"LOGIN_FLAVORING" else "webVpnKey", "")
    val outputAES = cookie?.let { it1 -> Crypto.encryptAES(inputAES, it1) }
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
                outputAES?.let { it1 -> vm.login(username, it1,loginFlavoring,code) }
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
                                    if(GlobalUIStateHolder.excludeJxglstu) {
                                        onResult("登陆成功")
                                        Starter.loginSuccess(context)
                                    } else if(!GlobalUIStateHolder.webVpn) {
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
                                            Starter.loginSuccess(context)
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
private fun ImageCodeUI( vm: LoginViewModel, onResult : (String) -> Unit) {
    // refresh当webVpn关闭才起效，开启时不需要refresh，直接重载图片
    val jSessionId by vm.jSessionId.state.collectAsState()
    val webVpnCookie by DataStoreManager.webVpnCookies.collectAsState(initial = "")

    val w by vm.webVpnTicket.state.collectAsState()

    val refresh = if(GlobalUIStateHolder.webVpn) {
        w is UiState.Loading
    } else {
        jSessionId is UiState.Loading
    }
    if(refresh) {
        CircularProgressIndicator()
    } else  {
        val url = (
                if(!GlobalUIStateHolder.webVpn) MyApplication.CAS_LOGIN_URL
                else MyApplication.WEBVPN_URL + "http/77726476706e69737468656265737421f3f652d22f367d44300d8db9d6562d/"
                ) + "cas/vercode"
        // 让 URL 可变，每次点击时更新
        var imageUrl by remember { mutableStateOf("$url?timestamp=${System.currentTimeMillis()}") }
        val cookies = if(GlobalUIStateHolder.webVpn) MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie else {
            (jSessionId as? UiState.Success)?.data?.jSession
        }

        // webVpn开关变化时重载
        LaunchedEffect(GlobalUIStateHolder.webVpn, GlobalUIStateHolder.refreshImageCode,cookies) {
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


fun isAnonymity() : Boolean = getPersonInfo().name == null

private const val TAB_LOGIN = 0
private const val TAB_SETTRINGS = 1

private data class CheckResult(val can : Boolean?,val text : String,val code : Int?)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun LoginScreen(
    vm : LoginViewModel,
    networkVm : NetWorkViewModel,
) {
    val scope = rememberCoroutineScope()
    val activity = LocalActivity.current
//    var webVpn by rememberSaveable { mutableStateOf(false) }
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
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

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = topBarTransplantColor(),
                title = {
                    ScrollText(text = "CAS登录" + if(GlobalUIStateHolder.webVpn) " (WebVpn)" else "")
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
                        val hasNotice = showTip || showTip2
                        Box() {
                            if(hasNotice) {
                                Badge(
//                                    containerColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.zIndex(1f).align(Alignment.TopEnd).padding(1.5.dp)
                                ) {
                                    val l = listOf(showTip,showTip2)
                                    Text(l.filter { it==true }.size.toString())
                                }
                            }
                            IconButton(
                                onClick = { tab = TAB_SETTRINGS },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(tab == TAB_SETTRINGS) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        Color. Unspecified
                                    },
                                ),
                            ) {
                                Icon(
                                    painterResource(R.drawable.settings),
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
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
        bottomBar = bottom@ {
            Column (modifier = Modifier.bottomBarBlur(hazeState, color = MaterialTheme.colorScheme.surfaceContainer)) {
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
                if(tab == TAB_SETTRINGS) {
                    LargeButton(
                        onClick = {
                            tab = when(tab) {
                                TAB_LOGIN -> TAB_SETTRINGS
                                else -> TAB_LOGIN
                            }
                        },
                        text = "回到登录页面",
                        modifier = Modifier.fillMaxWidth().padding(APP_HORIZONTAL_DP).navigationBarsPadding(),
                        icon = R.drawable.login,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.9f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    val text =

                        if(showTip && showTip2) {
                            "两条重要提示"
                        } else if(showTip) {
                            "新生登录前须知"
                        } else if(showTip2) {
                            "教务系统无法联通"
                        } else {
                            "选项"
                        }
                    LargeButton(
                        onClick = {
                            tab = when(tab) {
                                TAB_LOGIN -> TAB_SETTRINGS
                                else -> TAB_LOGIN
                            }
                        },
                        text = text,
                        modifier = Modifier.fillMaxWidth().padding(APP_HORIZONTAL_DP).navigationBarsPadding(),
                        icon = if(!showTip && !showTip2) R.drawable.settings else R.drawable.notifications,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(.9f),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    ) {innerPadding ->
        Box (modifier = Modifier
            .fillMaxSize()
            .hazeSource(hazeState)) {
            AnimatedVisibility(
                visible = tab == TAB_LOGIN,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    TwoTextField(vm,innerPadding,username) {
                        username = it
                    }
                }
            }
            AnimatedVisibility(
                visible = tab == TAB_SETTRINGS,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column (modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    InnerPaddingHeight(innerPadding,true)
                    DividerTextExpandedWith("状态") {
                        CustomCard(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            val status = if(jxglstuStatus == null) {
                                CheckResult(null,"正在检查",null)
                            } else if(jxglstuStatus!! < 400) {
                                CheckResult(true,"状态正常,可直接登录",jxglstuStatus)
                            } else if(jxglstuStatus!! in 500 until 600) {
                                CheckResult(false,"教务系统拒绝响应,可能是系统在维护,或使用了爬虫工具导致暂时性的网络IP封禁,请更换网络重新进入或等待几小时后,或打开跳过教务系统",jxglstuStatus)
                            } else if(jxglstuStatus == TIMEOUT_ERROR_CODE) {
                                CheckResult(false,"教务系统封网,请换为校园网重新使用或打开外地访问",jxglstuStatus)
                            } else if(jxglstuStatus == CONNECTION_ERROR_CODE) {
                                CheckResult(false,"连接失败,设备无网络或对方服务器存在问题丢包,尝试重新进入此页面",jxglstuStatus)
                            } else {
                                CheckResult(false,"状态码 $jxglstuStatus",jxglstuStatus)
                            }
                            TransplantListItem(
                                supportingContent = { Text(status.text ) },
                                modifier = Modifier.clickable {
                                    Starter.startWlanSettings(context)
                                },
                                headlineContent = {
                                    Text("教务系统联通状态")
                                },
                                leadingContent = {
                                    when(status.can) {
                                        null -> LoadingIcon()
                                        true -> Icon(painterResource(R.drawable.check_circle),null)
                                        false -> {
                                            BadgedBox(
                                                badge = {
                                                    Badge()
                                                },
                                                content = {
                                                    Icon(painterResource(R.drawable.link_off),null)
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                            if(showTip) {
                                PaddingHorizontalDivider()
                                TransplantListItem(
                                    supportingContent = { Text("新生需先在网页教务系统或信息门户登录过(点击跳转网页),确认账号已可以使用,如您已登陆过可忽略") },
                                    headlineContent = { Text("致新生") },
                                    leadingContent = {
                                        BadgedBox(
                                            badge = {
                                                Badge()
                                            },
                                            content = {
                                                Icon(painterResource(R.drawable.check_circle), null)
                                            }
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        Starter.startWebUrl(context,MyApplication.CAS_LOGIN_URL)
                                    },
                                )
                            }
                        }
                    }
                    DividerTextExpandedWith("选项") {
                        CustomCard(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            RefreshHuiXin(networkVm,true)
                            PaddingHorizontalDivider()
//                            TransplantListItem(
//                                supportingContent = { Text("自授权登录") },
//                                headlineContent = { Text("借助扫码登陆通道，自己为自己授权登录") },
//                                modifier = Modifier.clickable {
//                                    showToast("正在开发")
//                                },
//                                leadingContent = {
//                                    Icon(painterResource(R.drawable.qr_code_2),null)
//                                },
//                            )
//                            PaddingHorizontalDivider()
//                            TransplantListItem(
//                                supportingContent = { Text("不使用CAS统一认证登录，直接用账号密码登录教务系统") },
//                                headlineContent = { Text("教务系统备用通道") },
//                                modifier = Modifier.clickable {
//                                    showToast("正在开发")
//                                },
//                                leadingContent = {
//                                    Icon(painterResource(R.drawable.login),null)
//                                },
//                            )
//                            PaddingHorizontalDivider()
                            if(!GlobalUIStateHolder.webVpn) {
                                TransplantListItem(
                                    supportingContent = { Text("打开开关后,将跳过教务系统而登录,用于离校且教务封网的情况下需刷新其他平台") },
                                    headlineContent = { Text("跳过教务系统") },
                                    modifier = Modifier.clickable {
                                        scope.launch {
                                            GlobalUIStateHolder.excludeJxglstu = !GlobalUIStateHolder.excludeJxglstu
                                            refresh(vm)
                                        }
                                    },
                                    leadingContent = {
                                        Icon(painterResource(R.drawable.arrow_split),null)
                                    },
                                    trailingContent = {
                                        Switch(checked = GlobalUIStateHolder.excludeJxglstu,onCheckedChange = { ch ->
                                            scope.launch {
                                                GlobalUIStateHolder.excludeJxglstu = !GlobalUIStateHolder.excludeJxglstu
                                                refresh(vm)
                                            }
                                        })
                                    },
                                )
                                PaddingHorizontalDivider()
                            }

                            TransplantListItem(
                                headlineContent = { Text("外地访问(WebVpn)") },
                                supportingContent = { Text("外地访问支持刷新教务系统和访问内网链接,不受教务封网限制;\n登陆成功后，在 查询中心-WebVpn 可打开全局WebVpn，即可直接登录使用大创系统、图书馆、一些封网的通知公告等内容")},
                                leadingContent = { Icon(painterResource(R.drawable.vpn_key),null) },
                                trailingContent = {
                                    Switch(checked = GlobalUIStateHolder.webVpn,onCheckedChange = { ch -> GlobalUIStateHolder.webVpn = !GlobalUIStateHolder.webVpn })
                                },
                                modifier = Modifier.clickable { GlobalUIStateHolder.webVpn = !GlobalUIStateHolder.webVpn },
                            )
                            PaddingHorizontalDivider()
                            TransplantListItem(
                                headlineContent = { Text("修改密码") },
                                supportingContent = { Text("修改或重置CAS统一认证密码")},
                                leadingContent = { Icon(painterResource(R.drawable.lock_reset),null) },
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        Starter.startWebView(context,MyApplication.CAS_LOGIN_URL + "cas/forget","忘记密码",null,R.drawable.lock_reset)
                                    }
                                },
                            )
                            PaddingHorizontalDivider()
                            CheckExistUI(networkVm)
                            Spacer(Modifier.height(APP_HORIZONTAL_DP))
                        }
                    }
                    DividerTextExpandedWith("范围") {
                        CustomCard(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.surface,
                        ) {
                            Text("当前登录将刷新如下平台", modifier = Modifier.padding(start = APP_HORIZONTAL_DP, top = APP_HORIZONTAL_DP-CARD_NORMAL_DP))
                            val list = listOf(
                                CasPlatform("教务系统",canWebVpn = true,canWithoutJxglstu = false, canWithJxglstu = true,maybeUnlinked = true),
                                CasPlatform("信息门户", canWebVpn = false,canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = false),// 可支持webVpn
                                CasPlatform("智慧社区", canWebVpn = false,canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = false),// 可支持webVpn
                                CasPlatform("学工系统", canWebVpn = false, canWithoutJxglstu = true,canWithJxglstu = true, maybeUnlinked = false),// 可支持webVpn
                                CasPlatform("慧新易校",canWebVpn = true, canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = true),
                                CasPlatform("指间工大", canWebVpn = false,canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = false),// 可支持webVpn
                                CasPlatform("体测平台",canWebVpn = false, canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = true),
                                CasPlatform("WebVpn",canWebVpn = true, canWithoutJxglstu = false,canWithJxglstu = false, maybeUnlinked = false),
                                CasPlatform("图书馆",canWebVpn = true, canWithoutJxglstu = true, canWithJxglstu = true, maybeUnlinked = true),
                                CasPlatform("大创系统",canWebVpn = true, canWithoutJxglstu = false, canWithJxglstu = false, maybeUnlinked = true),
                            )
                            LazyRow(modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP/2)) {
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                                items(list.size) { index ->
                                    val item = list[index]
                                    val enabled =
                                        if(GlobalUIStateHolder.webVpn) {
                                            item.canWebVpn
                                        } else if(GlobalUIStateHolder.excludeJxglstu) {
                                            item.canWithoutJxglstu
                                        } else {
                                            item.canWithJxglstu
                                        }
                                    AssistChip(
                                        onClick = {},
                                        enabled = enabled,
                                        leadingIcon = {
                                            Icon(
                                                painterResource(
                                                    if(enabled) R.drawable.check
                                                    else R.drawable.close
                                                ),
                                                null,
                                            )
                                        },
                                        label = { Text(item.name) }
                                    )
                                    if(index != list.size-1) {
                                        Spacer(Modifier.width(CARD_NORMAL_DP*2))
                                    }
                                }
                                item { Spacer(Modifier.width(APP_HORIZONTAL_DP)) }
                            }
                        }
                    }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                    BottomTip("如果登不进去就左上角叉掉重进，再试一两次，学校经常把各种平台搞得有问题")
                    Spacer(Modifier.height(APP_HORIZONTAL_DP*2).navigationBarsPadding())
                    InnerPaddingHeight(innerPadding,false)
                }
            }
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
    innerPadding : PaddingValues,
    username : String,
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
                                        ImageCodeUI(vm) {
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
                            loginClick(context,vm,username,inputAES,inputCode, onLoad = { loading = it }, onResult = { status = it},scope)
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
                            Starter.goToMain(context)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    text = "游客",
                    icon = R.drawable.partner_exchange,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            } else {
                Button(
                    onClick = {
                        val cookie = prefs.getString("LOGIN_FLAVORING", "")
                        if (cookie != null) {
                            loginClick(context,vm,username,inputAES,inputCode, onLoad = { loading = it }, onResult = { status = it}, scope)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        "登录",
                        modifier = Modifier.padding(vertical = CARD_NORMAL_DP * 1),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
    InnerPaddingHeight(innerPadding,false)
}


private data class CasPlatform(
    val name : String,
    val canWebVpn : Boolean,
    val canWithoutJxglstu : Boolean,
    val canWithJxglstu : Boolean,
    val maybeUnlinked : Boolean
)


 suspend fun preloadDnsFromUrl(url: String): String? = withContext(Dispatchers.IO) {
    try {
        val host = URL(url).host
        val address = InetAddress.getByName(host)
        address.hostAddress
    } catch (e: Exception) {
        Log.e("DNS解析",getKeyStackTrace(e))
        e.printStackTrace()
        null
    }
}


fun preloadDnsFromUrl2(url: String): String? {
    return try {
        val host = URL(url).host   // 提取 host
        val address = InetAddress.getByName(host)
        address.hostAddress.also {
            println("$host -> $it")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun  CheckExistUI(
    vm: NetWorkViewModel
) {
    val refreshNetwork = suspend {

    }
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf(prefs.getString("Username","")?:"") }
    TransplantListItem(
        headlineContent = { Text("账号查询") },
        supportingContent = { Text("验证账号是否因离校被删除或新生未录入") },
        leadingContent = {
            Icon(painterResource(R.drawable.person_check),null)
        }
    )
    CustomTextField(
        input = input,
        singleLine = true,
        label = { Text("输入学号") },
        trailingIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        refreshNetwork()
                    }
                }
            ) {
                Icon(painterResource(R.drawable.search),null)
            }
        }
    ) { input = it }
}