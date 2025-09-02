package com.hfut.schedule.ui.screen.home.search.function.school.webvpn

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.application.MyApplication.Companion.WEBVPN_URL
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.WebVpnUtil
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.BottomButton
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.webview.getPureUrl
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.isValidWebUrl
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardBottomButton
import com.hfut.schedule.ui.component.container.CardBottomButtons
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WebVpnScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = HazeBlurLevel.MID.code)
    val hazeState = rememberHazeState(blurEnabled = blur >= HazeBlurLevel.MID.code)
    val route = remember { AppNavRoute.WebVpn.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val context = LocalContext.current

    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            route = route,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.topBarBlur(hazeState, ),
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.WebVpn.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,animatedContentScope,route, AppNavRoute.WebVpn.icon)
                    },
                    actions = {
                        FilledTonalButton(
                            onClick = {
                                if(!vm.webVpn) {
                                    Starter.refreshLogin(context)
                                }
                            },
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Text("${if(vm.webVpn) "已" else "未"}登录WebVpn")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                WebVpnUI(vm)
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}

suspend fun getWebVpnCookie(vm: NetWorkViewModel) : String? =
    if(vm.webVpn) {
        val webVpnCookie = DataStoreManager.webVpnCookies.first{ it.isNotEmpty() }
        MyApplication.WEBVPN_COOKIE_HEADER + webVpnCookie
    } else {
        null
    }

fun autoWebVpnForNews(
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

    DividerTextExpandedWith("内网 to 外网") {
        var input by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<String?>(null) }
        CardListItem(
            headlineContent = {
                Text("输入需要校园网才可访问的链接，将其转换为WebVpn链接后，可通过外网访问")
            },
            supportingContent = {
                Text("例如 " + MyApplication.LIBRARY_SEAT + " 点击粘贴到输入框")
            },
            leadingContent = {
                Icon(painterResource(R.drawable.info),null)
            },
            modifier = Modifier.clickable {
                input = MyApplication.LIBRARY_SEAT
            }
        )
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
                                if(vm.webVpn) {
                                    Starter.startWebView(context,it, cookie = cookies)
                                } else {
                                    showToast("先以外地访问模式登录")
                                    Starter.refreshLogin(context)
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
    DividerTextExpandedWith("外网 to 内网") {
        var input by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<String?>(null) }
        CardListItem(
            headlineContent = {
                Text("输入WebVpn链接，还原为原始链接")
            },
            supportingContent = {
                Text("例如 " + MyApplication.JXGLSTU_WEBVPN_URL + " 点击粘贴到输入框")
            },
            leadingContent = {
                Icon(painterResource(R.drawable.info),null)
            },
            modifier = Modifier.clickable {
                input = MyApplication.JXGLSTU_WEBVPN_URL
            }
        )
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
                    HorizontalDivider()
                    Row(modifier = Modifier.align(Alignment.End)) {
                        Text(
                            text = "复制",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                ).clickable { ClipBoardUtils.copy(it) }
                        )
                        Text(
                            text = "打开",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                ).clickable {
                                    Starter.startWebView(context,it)
                                }
                        )
                        Text(
                            text = "清除",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(
                                    horizontal = APP_HORIZONTAL_DP,
                                    vertical = APP_HORIZONTAL_DP - 5.dp
                                ).clickable { result = null }
                        )
                    }
                }
            }
        }
    }
}