package com.hfut.schedule.ui.component.webview

import android.graphics.Canvas
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings.FORCE_DARK_OFF
import android.webkit.WebSettings.FORCE_DARK_ON
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.navigation.NavHostController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.ColorMode
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.topBarBlur
import com.hfut.schedule.ui.style.topBarTransplantColor
import com.hfut.schedule.ui.style.zIndexBlur
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.component.TransitionScaffold
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.TransitionState
import com.xah.transition.style.DefaultTransitionStyle
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

fun getPureUrl(url : String): String {
    return if(url.startsWith("file://")) {
        "本地"
    } else {
        url.substringAfter("://").substringBefore("/")
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDialog(
    showDialog : Boolean,
    showChanged : () -> Unit,
    url : String,
    title : String = getPureUrl(url),
    cookie :String? = null,
) {

    val switch_startUri = prefs.getBoolean("SWITCHSTARTURI",true)

    if (showDialog) {
        if(switch_startUri) {
            Dialog(
                onDismissRequest = showChanged,
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                WebViewScreen(url,cookie, showChanged,title)
            }
        } else {
            Starter.startWebUrl(url)
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WebViewBackIcon(
    webView: WebView?,
    icon : Int? = null,
    navController: NavHostController,
    route : String,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val back : () -> Unit = {
        if(webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            navController.popBackStack()
        }
    }
    val cIcon = if(webView?.canGoBack() == true) {
        Icons.Default.ArrowBack
    } else {
        Icons.Default.Close
    }
    val button = @Composable { content  : @Composable () -> Unit ->
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = back,
                    onLongClick = {
                        navController.popBackStack()
                    }
                )
        ) {
            content()
        }
    }
    with(sharedTransitionScope) {
        if(icon == null) {
            button {
                Icon(cIcon, contentDescription = "",tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
            }
        } else {
            val speed = TransitionState.curveStyle.speedMs + TransitionState.curveStyle.speedMs/2
            var show by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                show = true
                delay(speed*1L)
                delay(1000L)
                show = false
                if(TransitionState.transplantBackground) {
                    delay(3000L)
                    show = true
                }
            }


            button {
                Box() {
                    AnimatedVisibility(
                        visible = show,
                        enter = DefaultTransitionStyle.centerAllAnimation.enter,
                        exit = DefaultTransitionStyle.centerAllAnimation.exit
                    ) {
                        Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = iconElementShare(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),animatedContentScope = animatedContentScope, route = route))
                    }
                    AnimatedVisibility(
                        visible = !show,
                        enter = DefaultTransitionStyle.centerAllAnimation.enter,
                        exit = DefaultTransitionStyle.centerAllAnimation.exit
                    ) {
                        Icon(cIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
                    }
                }
            }
        }
    }
}

@Composable
fun isThemeDark() : Boolean {
    val colorCode = remember { ColorMode.entries }
    val setting by DataStoreManager.colorModeFlow.collectAsState(initial = null)
    if(setting != null) {
        val mode = colorCode.find { it.code == setting } ?: ColorMode.AUTO
        return when(mode) {
            ColorMode.AUTO -> isSystemInDarkTheme()
            ColorMode.DARK -> true
            ColorMode.LIGHT -> false
        }
    } else {
        return isSystemInDarkTheme()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun NewWebViewScreen(
    url : String,
    title : String = getPureUrl(url),
    icon : Int? = null,
    cookies : String? = null,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val isDark = isThemeDark()
    val webViewDark by DataStoreManager.webViewDark.collectAsState(initial = true)
    var currentTitle by remember { mutableStateOf(title) }
    var fullScreen by remember { mutableStateOf(false) }

    var currentUrl by remember { mutableStateOf(url) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var visible by remember { mutableStateOf(true) }
    var backCount by remember { mutableIntStateOf(1) }
    BackHandler {
        if(fullScreen) {
            fullScreen = false
        }
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            if(backCount > 0) {
                showToast("再滑一次退出")
                backCount--
            } else {
                navController.popBackStack()
            }
        }
    }
    var click by remember { mutableStateOf(false) }
    val isExist by produceState(initialValue = false, key1 = click) {
        value = DataBaseManager.webUrlDao.isExist(currentUrl)
    }
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    val tools = @Composable {
        if(webView?.canGoBack() == true) {
            IconButton(onClick = {
                webView?.goBack()
            }) { Icon(Icons.Default.ArrowBack, contentDescription = "") }
        } else {
            IconButton(onClick = {
                navController.popBackStack()
            }) { Icon(Icons.Default.Close, contentDescription = "") }
        }

        IconButton(onClick = { webView?.reload() }) { Icon(
            painterResource(id = R.drawable.rotate_right), contentDescription = "") }

        IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(
            painterResource(id = R.drawable.net), contentDescription = "") }

        IconButton(onClick = {
            if(!fullScreen) {
                fullScreen = true
                visible = true
            } else {
                fullScreen = false
                visible = false
            }
        }) { Icon(
            painterResource(id = if(!fullScreen)R.drawable.expand_content else R.drawable.collapse_content), contentDescription = "") }

        IconButton(onClick = {
            scope.launch {
                if(isExist) {
                    DataBaseManager.webUrlDao.delFromUrl(currentUrl)
                    showToast("已取消收藏")
                } else {
                    DataBaseManager.webUrlDao.insert(
                        WebUrlDTO(
                            name = title,
                            type = WebURLType.COLLECTION,
                            url = currentUrl
                        ).toEntity()
                    )
                    showToast("收藏成功")
                }
                click = !click
            }
        }) { Icon(
            painterResource(id = if(isExist) R.drawable.star_filled else  R.drawable.star ), contentDescription = "") }


        IconButton(onClick = { ShareTo.shareString(currentUrl) }) { Icon(
            painterResource(id = R.drawable.ios_share), contentDescription = "") }
    }

    val blur by DataStoreManager.hazeBlurFlow.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var topColor by remember { mutableStateOf<Color?>(null) }

    val route = remember { AppNavRoute.WebView.shareRoute(url) }
    with(sharedTransitionScope) {
        CustomTransitionScaffold (
            enablePredictive = false,
            route = route,
            animatedContentScope = animatedContentScope,
            navHostController = navController,
            topBar = {
                AnimatedVisibility(
                    visible = !fullScreen,
                    enter = AppAnimationManager.toTopAnimation.enter,
                    exit = AppAnimationManager.toTopAnimation.exit
                ) {
                    TopAppBar(
                        modifier = Modifier.topBarBlur(hazeState,useTry = true, color = topColor),
                        colors = topBarTransplantColor(),
                        actions = {
                            Row{
                                if(!visible) {
                                    IconButton(
                                        onClick = { visible = true }
                                    ) {
                                        Icon(painterResource(R.drawable.more_vert),null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            WebViewBackIcon(webView,icon,navController,route,sharedTransitionScope,animatedContentScope)
                        },
                        title = {
                            Column {
                                ScrollText(currentTitle)
                                ScrollText(
                                    getPureUrl(currentUrl),
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            val bottom = innerPadding.calculateBottomPadding().value.toInt() + APP_HORIZONTAL_DP.value.toInt()
            val top = innerPadding.calculateTopPadding().value.toInt()

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = visible,
                    enter = AppAnimationManager.hiddenRightAnimation.enter,
                    exit = AppAnimationManager.hiddenRightAnimation.exit,
                    modifier = Modifier.padding(innerPadding).padding(horizontal = APP_HORIZONTAL_DP).align(Alignment.CenterEnd).zIndex(1f)
                ) {
                    VerticalFloatingToolbar (
                        expanded = true,
                        colors =  FloatingToolbarDefaults.standardFloatingToolbarColors(Color.Transparent),
                        modifier = Modifier.clip(MaterialTheme.shapes.extraLarge).zIndexBlur(hazeState,FloatingToolbarDefaults.standardFloatingToolbarColors().toolbarContainerColor)
                    ) {
                        tools()
                        IconButton(onClick = { visible = false }) { Icon(
                            painterResource(id = R.drawable.visibility_off), contentDescription = "") }
                    }
                }
                if(loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(innerPadding).zIndex(2f)
                    )
                }
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false

                            // 深色模式
                            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                // Android 13+
                                WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, true)
                            } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                // Android 10‑12
                                val mode = if (isDark)
                                    WebSettingsCompat.FORCE_DARK_ON
                                else
                                    WebSettingsCompat.FORCE_DARK_OFF
                                WebSettingsCompat.setForceDark(settings, mode)
                            }

                            // 启用 Cookie
                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)
                            // 设置 Cookie
                            // **清除已有 Cookie**
                            cookieManager.removeSessionCookies(null)
                            cookieManager.removeAllCookies(null)
                            cookieManager.flush()

                            // **异步设置 Cookie，并确保生效后再加载 URL**
                            cookies?.let {
                                cookieManager.setCookie(url, it) {
                                    cookieManager.flush()
                                    post {
                                        loadUrl(url)
                                    }
                                }
                            } ?: run {
                                loadUrl(url) // 没有 Cookie 直接加载
                            }
                            // 标题获取
                            if(!currentUrl.startsWith("file://")) {
                                webChromeClient = object : WebChromeClient() {
                                    override fun onReceivedTitle(view: WebView?, title: String?) {
                                        if (title != null) {
                                            currentTitle = title
                                        }
                                        super.onReceivedTitle(view, title)
                                    }
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    if (url != null) {
                                        currentUrl = url
                                    }
                                    webView = this@apply
                                    if (isDark && webViewDark) {
                                        val css = """
                                html, body {
                                    background-color: #121212 !important;
                                    color: #eeeeee !important;
                                }
                                * {
                                    background-color: transparent !important;
                                    color: #eeeeee !important;
                                    border-color: #333333 !important;
                                }
                                a { color: #8ab4f8 !important; }
                            """.trimIndent()

                                        val js = """
                                (function() {
                                    let style = document.createElement('style');
                                    style.type = 'text/css';
                                    style.appendChild(document.createTextNode(`$css`));
                                    document.head.appendChild(style);
                                })();
                            """.trimIndent()

                                        view?.evaluateJavascript(js, null)
                                    }
                                    view?.postDelayed({
                                        try {
                                            val bitmap = createBitmap(view.width, view.height)
                                            val canvas = Canvas(bitmap)
                                            view.draw(canvas)

                                            // 读取顶部某个位置的像素颜色，比如(10, 10)
                                            val colors = (0 until 5).map { bitmap[0, it] }
                                            val avgColorInt = colors.reduce { acc, c ->
                                                val a = (android.graphics.Color.alpha(acc) + android.graphics.Color.alpha(c)) / 2
                                                val r = (android.graphics.Color.red(acc) + android.graphics.Color.red(c)) / 2
                                                val g = (android.graphics.Color.green(acc) + android.graphics.Color.green(c)) / 2
                                                val b = (android.graphics.Color.blue(acc) + android.graphics.Color.blue(c)) / 2
                                                android.graphics.Color.argb(a, r, g, b)
                                            }
                                            topColor =  Color(avgColorInt)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }, 100) // 延迟一点确保绘制完成
                                    // 注入 JS，为网页内容添加 padding（单位 px）
                                    view?.evaluateJavascript(
                                        """
        (function() {
            document.body.style.paddingTop = '${top}px';
            document.body.style.paddingBottom = '${bottom}px';
        })();
        """.trimIndent(),
                                        null
                                    )
                                    loading = false
                                }

                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): WebResourceResponse? {
                                    request?.url?.toString()?.let { requestUrl ->
                                        cookies?.let {
                                            cookieManager.setCookie(requestUrl, it)
                                        }
                                    }
                                    return super.shouldInterceptRequest(view, request)
                                }
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    loading = true
                                    val urlS = request?.url.toString()

                                    if (urlS.contains("download")) { // 识别下载链接
                                        Starter.startWebUrl(urlS)
                                        return true // 拦截 WebView 处理
                                    }

                                    return false // 继续让 WebView 处理
                                }
                            }
                            // 监听 WebView 滚动事件
                            setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                                if (scrollY > oldScrollY) {
                                    visible = false // 向下滚动时隐藏 Toolbar
                                } else if (scrollY < oldScrollY) {
//                                visible = true // 向上滚动时显示 Toolbar
                                }
                            }
                        }
                    },
                    modifier = Modifier.hazeSource(hazeState).fillMaxSize()
                )
            }
        }
    }
}