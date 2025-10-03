package com.hfut.schedule.ui.component.webview

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.animationOpen
import com.hfut.schedule.ui.util.webview.WebViewBackHandler
import com.hfut.schedule.ui.util.webview.WebViewBackIcon
import com.hfut.schedule.ui.util.webview.WebViewContent
import com.hfut.schedule.ui.util.webview.WebViewTools
import com.hfut.schedule.ui.util.webview.WebViewTopBar
import com.hfut.schedule.ui.util.webview.evaluateJs
import com.hfut.schedule.ui.util.webview.getPureUrl
import com.hfut.schedule.ui.util.webview.getWebView
import com.hfut.schedule.ui.util.webview.isThemeDark
import com.hfut.schedule.ui.util.webview.scrollListener
import com.hfut.schedule.ui.util.webview.setInitial
import com.hfut.schedule.ui.util.webview.sharedInterceptRequest
import com.hfut.schedule.ui.util.webview.sharedOverrideUrlLoading
import com.hfut.schedule.ui.util.webview.updateTitle
import com.hfut.schedule.ui.util.webview.updateUrl
import com.xah.transition.util.popBackStackForTransition
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun WebViewScreenForNavigation(
    url : String,
    title : String = getPureUrl(url),
    icon : Int? = null,
    cookies : String? = null,
    navController : NavHostController,
    drawerState: DrawerState,
    onColor: (Color?) -> Unit
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var visible by remember { mutableStateOf(true) }
    val isDark = isThemeDark()
    val webViewDark by DataStoreManager.enableForceWebViewDark.collectAsState(initial = true)
    var currentUrl by remember { mutableStateOf(url) }
    var currentTitle by remember { mutableStateOf(title) }
    var fullScreen by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var topColor by remember { mutableStateOf<Color?>(null) }
    val topBarTitleColor = topColor?.let {
        if (it.luminance() < 0.5f) Color.White else Color.Black
    } ?: MaterialTheme.colorScheme.primary

    val enableControlCenter by DataStoreManager.enableControlCenter.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val route = remember { AppNavRoute.WebView.shareRoute(url) }
    LaunchedEffect(topColor) {
        onColor(topColor)
    }

    DisposableEffect(Unit) {
        onDispose {
            // 恢复颜色
            onColor(null)
            webView?.destroy()
        }
    }

    WebViewBackHandler(
        webView,
        fullScreen,
        loading,
        { fullScreen = it },
    ) {
        navController.popBackStack()
    }


    val tools = @Composable {
        WebViewTools(
            webView,
            {  navController.popBackStack() },
            fullScreen,
            currentUrl,
            currentTitle,
            url,
            {
                fullScreen = it
                visible = it
            }
        ) {
            if(enableControlCenter) {
                IconButton(onClick = {
                    scope.launch {
                        visible = false
                        drawerState.animationOpen()
                    }
                }) { Icon(painterResource(id = R.drawable.flash_on), contentDescription = "") }
            }
        }
    }

    CustomTransitionScaffold (
        enablePredictive = false,
        route = route,
        navHostController = navController,
        topBar = {
            WebViewTopBar(
                fullScreen,
                topColor,
                topBarTitleColor,
                visible,
                { visible = it },
                {
                    WebViewBackIcon(webView,icon,topBarTitleColor,route){
                        navController.popBackStackForTransition()
                    }
                },
                currentTitle,
                currentUrl
            )
        },
    ) { innerPadding ->
        val bottom = innerPadding.calculateBottomPadding().value.toInt() + APP_HORIZONTAL_DP.value.toInt()
        WebViewContent(
            innerPadding,
            visible,
            tools,
            { visible = it },
            loading,
        ) { context ->
            webView = getWebView(context) {
                // 初始加载
                val cookieManager = CookieManager.getInstance()
                setInitial(cookieManager,cookies, url)
                // 标题获取
                updateTitle(currentUrl) {
                    currentTitle = it
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 更新
                        webView = view
                        updateUrl(url) {
                            currentUrl = it
                        }
                        view?.evaluateJs(isDark, webViewDark, bottom) {
                            topColor = it
                        }
                        loading = false
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        sharedInterceptRequest(request,currentUrl,cookies,cookieManager)
                        return super.shouldInterceptRequest(view, request)
                    }
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
                        sharedOverrideUrlLoading(request,context) {
                            loading = it
                        }
                }
                // 监听滚动
                scrollListener {
                    visible = it
                }
            }
            webView!!
        }
    }
}

