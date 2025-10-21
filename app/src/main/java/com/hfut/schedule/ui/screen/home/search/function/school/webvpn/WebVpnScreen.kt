package com.hfut.schedule.ui.screen.home.search.function.school.webvpn

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.application.MyApplication.Companion.WEBVPN_URL
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.network.WebVpnUtil
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.button.LiquidButton

import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.util.webview.getPureUrl
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WebVpnScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.WebVpn.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current
    val backdrop = rememberLayerBackdrop()

//    val providerState = rememberLiquidGlassProviderState(
        // if the providing content has any transparent area and there is a background behind the content, set the
        // background color here, or set it to null
//        backgroundColor = Color.White
//    )

    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState, ),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.WebVpn.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route, AppNavRoute.WebVpn.icon)
                },
                actions = {
                    LiquidButton(
                        onClick = {
                            if(!GlobalUIStateHolder.webVpn) {
                                Starter.refreshLogin(context)
                            }
                        },
//                        isCircle = true,
                        backdrop = backdrop,
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                    ) {
//                        Icon(painterResource(R.drawable.search),null)
                        Text("${if(GlobalUIStateHolder.webVpn) "已" else "未"}登录WebVpn")
                    }
//                        FilledTonalButton(
//                            onClick = {
//                                if(!GlobalUIStateHolder.webVpn) {
//                                    Starter.refreshLogin(context)
//                                }
//                            },
//                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
//                        ) {
//                            Text("${if(GlobalUIStateHolder.webVpn) "已" else "未"}登录WebVpn")
//                        }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            InnerPaddingHeight(innerPadding,true)
            WebVpnUI(vm)
            InnerPaddingHeight(innerPadding,false)
        }
    }
//    }
}

suspend fun getWebVpnCookie() : String? {
    val webVpnCookie = DataStoreManager.webVpnCookies.first()
    return MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
}
suspend fun getWebVpnCookie(vm: NetWorkViewModel) : String? =
    if(GlobalUIStateHolder.webVpn) {
        val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        null
    }

suspend fun autoWebVpnForNews(
    context: Context,
    url: String,
    title: String = getPureUrl(url),
    cookie: String? = null,
    icon: Int? = null
) {
    if(cookie == null) {
        Starter.startWebView(context,url,title,null,icon)
    } else {
        Starter.startWebView(context,WebVpnUtil.getWebVpnUrl(url),title,cookie,icon)
    }
}

@Composable
fun WebVpnUI(vm: NetWorkViewModel) {
    val cookies by produceState<String?>(initialValue = null)  {
        value = getWebVpnCookie(vm)
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    DividerTextExpandedWith("内网 to 外网") {
        var input by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<String?>(null) }
        CustomCard(color = cardNormalColor()) {
            TransplantListItem(
                headlineContent = {
                    Text("输入需校园网才可访问的链接(或目前封网的平台),将其转换为WebVpn链接后,在登录WebVpn的前提下,可通过外网访问并直接登录")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                },
                modifier = Modifier.clickable {
                    input = MyApplication.LIBRARY_SEAT
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text("当使用外地访问登录后，可查看通知公告中外网无法查看的内容")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.stream),null)
                },
                modifier = Modifier.clickable {
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text("(座位预约) " + MyApplication.LIBRARY_SEAT)
                },
                overlineContent = { Text("点击粘贴到输入框")},
                leadingContent = { Text("例1")},
                modifier = Modifier.clickable {
                    input = MyApplication.LIBRARY_SEAT
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                leadingContent = { Text("例2")},
                headlineContent = {
                    Text("(图书馆) " + MyApplication.NEW_LIBRARY_URL)
                },
                overlineContent = { Text("点击粘贴到输入框")},
                modifier = Modifier.clickable {
                    input = MyApplication.NEW_LIBRARY_URL
                }
            )
        }
        CustomCard(color = cardNormalColor()) {
            CustomTextField(
                label = { Text("输入以http://或https://开头的合法链接")},
                input = input,
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(painterResource(R.drawable.close),null)
                    }
                },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            ) { input = it }
            BottomButton(
                onClick = {
                    result = WebVpnUtil.getWebVpnUrl(
                        input.let { if(it.endsWith("/")) it else "$it/" }
                    )
                },
                enable = input.isNotEmpty() && input.isNotBlank() && isValidWebUrl(input,true),
                text = "转换为WebVpn链接"
            )
        }

        result?.let {
            CustomCard(color = cardNormalColor()) {
                Column {
                    TransplantListItem(
                        headlineContent = {
                            Text(it, fontWeight = FontWeight.Bold)
                        },
                        overlineContent = {
                            Text("转换结果")
                        },
                    )
                    CardBottomButtons(
                        listOf(
                            CardBottomButton("复制") {
                                ClipBoardUtils.copy(it)
                            },
                            CardBottomButton("打开") {
                                scope.launch {
                                    if(GlobalUIStateHolder.webVpn) {
                                        Starter.startWebView(context,it, cookie = cookies)
                                    } else {
                                        showToast("先以外地访问模式登录")
                                        Starter.refreshLogin(context)
                                    }
                                }
                            },
                            CardBottomButton("清除") {
                                result = null
                            },
                        )
                    )
                }
            }
        }
        CardListItem(
            headlineContent = { Text("全局WebVpn")},
            supportingContent = { Text("打开后,App内所有打开网页的场景都将自动转换为WebVpn链接;\n仅登录WebVpn时可打开,退出App后自动关闭")},
            trailingContent = {
                Switch(checked = GlobalUIStateHolder.globalWebVpn, enabled = GlobalUIStateHolder.webVpn, onCheckedChange = { GlobalUIStateHolder.globalWebVpn = !GlobalUIStateHolder.globalWebVpn})
            },
            leadingContent = {
                Icon(painterResource(R.drawable.multiple_stop),null)
            },
            modifier = Modifier.clickable {
                if(GlobalUIStateHolder.webVpn) {
                    GlobalUIStateHolder.globalWebVpn = !GlobalUIStateHolder.globalWebVpn
                } else {
                    showToast("先以外地访问模式登录")
                    Starter.refreshLogin(context)
                }
            }
        )
    }
    DividerTextExpandedWith("外网 to 内网") {
        var input by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<String?>(null) }
        CustomCard(color = cardNormalColor()) {
            TransplantListItem(
                headlineContent = {
                    Text("输入WebVpn链接，还原为原始链接")
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                },
                modifier = Modifier.clickable {
                    input = MyApplication.LIBRARY_SEAT
                }
            )
            PaddingHorizontalDivider()
            TransplantListItem(
                headlineContent = {
                    Text("(教务系统) " + MyApplication.JXGLSTU_WEBVPN_URL)
                },
                overlineContent = { Text("点击粘贴到输入框")},
                leadingContent = { Text("例")},
                modifier = Modifier.clickable {
                    input = MyApplication.JXGLSTU_WEBVPN_URL
                }
            )
        }
        CustomCard(color = cardNormalColor()) {
            CustomTextField(
                label = { Text("输入以${WEBVPN_URL}开头的合法链接")},
                input = input,
                trailingIcon = {
                    IconButton(onClick = { input = "" }) {
                        Icon(painterResource(R.drawable.close),null)
                    }
                },
                singleLine = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            ) { input = it }
            BottomButton(
                onClick = {
                    result = WebVpnUtil.getOrdinaryUrl(
                        input.let { if(it.endsWith("/")) it else "$it/" }
                    )
                },
                enable = isValidWebUrl(input,true) && input.startsWith(WEBVPN_URL),
                text = "转换为原始链接"
            )
        }

        result?.let {
            CustomCard(color = cardNormalColor()) {
                Column {
                    TransplantListItem(
                        headlineContent = {
                            Text(it, fontWeight = FontWeight.Bold)
                        },
                        overlineContent = {
                            Text("转换结果")
                        },
                    )
                    CardBottomButtons(
                        listOf(
                            CardBottomButton("复制") {
                                ClipBoardUtils.copy(it)
                            },
                            CardBottomButton("打开") {
                                scope.launch {
                                    Starter.startWebView(context,it)
                                }
                            },
                            CardBottomButton("清除") {
                                result = null
                            },
                        )
                    )
                }
            }
        }
    }
}