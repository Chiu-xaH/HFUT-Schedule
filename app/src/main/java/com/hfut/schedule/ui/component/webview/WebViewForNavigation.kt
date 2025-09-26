package com.hfut.schedule.ui.component.webview

import android.graphics.Canvas
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.enableSlowWholeDocumentDraw
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.navigation.NavHostController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.ColorMode
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.animationOpen
import com.hfut.schedule.ui.style.special.CustomBottomSheet
import com.hfut.schedule.ui.util.AppAnimationManager
import com.xah.transition.component.awaitTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.state.TransitionConfig
import com.xah.transition.style.DefaultTransitionStyle
import com.xah.transition.util.canPopBack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun getPureUrl(url : String): String {
    return if(url.startsWith("file://")) {
        "本地"
    } else {
        url.substringAfter("://").substringBefore("/")
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WebViewBackIcon(
    webView: WebView?,
    icon : Int? = null,
    navController: NavHostController,
    route : String,
    color : Color,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    val back : () -> Unit = {
        if(webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            if(navController.canPopBack()) {
                navController.popBackStack()
            }
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
                        if(navController.canPopBack()) {
                            navController.popBackStack()
                        }
                    }
                )
        ) {
            content()
        }
    }
    if(icon == null) {
        button {
            Icon(cIcon, contentDescription = "",tint = color, modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
        }
    } else {
        var show by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            show = true
            sharedTransitionScope.awaitTransition()
            delay(1500L)
            show = false
            if(!enablePredictive || TransitionConfig.transplantBackground) {
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
                    Icon(painterResource(icon), contentDescription = null, tint = color,modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP).iconElementShare( route = route))
                }
                AnimatedVisibility(
                    visible = !show,
                    enter = DefaultTransitionStyle.centerAllAnimation.enter,
                    exit = DefaultTransitionStyle.centerAllAnimation.exit
                ) {
                    Icon(cIcon, contentDescription = null, tint = color,modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
                }
            }
        }
    }
}




@Composable
fun isThemeDark() : Boolean {
    val colorCode = remember { ColorMode.entries }
    val setting by DataStoreManager.colorMode.collectAsState(initial = null)
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
fun WebViewScreenForNavigation(
    url : String,
    title : String = getPureUrl(url),
    icon : Int? = null,
    cookies : String? = null,
    navController : NavHostController,
    drawerState: DrawerState,
    onColor: (Color?) -> Unit
) {
    val isDark = isThemeDark()
    val webViewDark by DataStoreManager.enableForceWebViewDark.collectAsState(initial = true)
    var currentTitle by remember { mutableStateOf(title) }
    var fullScreen by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(url) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var visible by remember { mutableStateOf(true) }
    var backCount by remember { mutableIntStateOf(1) }
    var click by remember { mutableStateOf(false) }
    val isExist by produceState(initialValue = false, key1 = click) {
        value = DataBaseManager.webUrlDao.isExist(currentUrl)
    }
    val enableControlCenter by DataStoreManager.enableControlCenter.collectAsState(initial = false)

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var on by remember { mutableStateOf<(String) -> Unit>({}) }
    var operationFavorite by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        if(operationFavorite) {
            var input by remember { mutableStateOf(currentTitle) }
            suspend fun favorite(useCurrent : Boolean) {
                val favoriteUrl = if(useCurrent) currentUrl else url
                if(isExist) {
                    DataBaseManager.webUrlDao.delFromUrl(favoriteUrl)
                    showToast("已取消收藏")
                } else {
                    DataBaseManager.webUrlDao.insert(
                        WebUrlDTO(
                            name = input,
                            type = WebURLType.COLLECTION,
                            url = favoriteUrl
                        ).toEntity()
                    )
                    showToast("收藏成功")
                }
                click = !click
            }
            CustomBottomSheet (
                onDismissRequest = { showBottomSheet = false },
                showBottomSheet = showBottomSheet,
                autoShape = false
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    HazeBottomSheetTopBar("选择链接", isPaddingStatusBar = false)
                    CustomTextField(
                        input = input,
                        label = { Text("标题") },
                        singleLine = false
                    ) { input = it }
                    Spacer(Modifier.height(CARD_NORMAL_DP))
                    CardListItem(
                        headlineContent = { Text(currentUrl) },
                        overlineContent = { Text("现链接") },
                        modifier = Modifier.clickable {
                            scope.launch {
                                favorite(true)
                                showBottomSheet = false
                            }
                        }
                    )
                    CardListItem(
                        headlineContent = { Text(url) },
                        overlineContent = { Text("初始链接(若用于WebVpn链接转换，推荐此项)") },
                        modifier = Modifier.clickable {
                            scope.launch {
                                favorite(false)
                                showBottomSheet = false
                            }
                        }
                    )
                    Spacer(Modifier
                        .height(APP_HORIZONTAL_DP)
                        .navigationBarsPadding())
                }
            }
        } else {
            if(currentUrl == url) {
                on(url)
            } else {
                CustomBottomSheet (
                    onDismissRequest = { showBottomSheet = false },
                    showBottomSheet = showBottomSheet,
                    autoShape = false
                ) {
                    Column {
                        HazeBottomSheetTopBar("选择链接", isPaddingStatusBar = false)
                        CardListItem(
                            headlineContent = { Text(currentUrl) },
                            overlineContent = { Text("现链接") },
                            modifier = Modifier.clickable {
                                on(currentUrl)
                                showBottomSheet = false
                            }
                        )
                        CardListItem(
                            headlineContent = { Text(url) },
                            overlineContent = { Text("初始链接(若用于WebVpn链接转换，推荐此项)") },
                            modifier = Modifier.clickable {
                                on(url)
                                showBottomSheet = false
                            }
                        )
                        Spacer(Modifier
                            .height(APP_HORIZONTAL_DP)
                            .navigationBarsPadding())
                    }
                }
            }
        }
    }
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

        IconButton(onClick = {
            on = { Starter.startWebUrl(context,it) }
            showBottomSheet = true
        }) { Icon(
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
            on = {}
            operationFavorite = true
            showBottomSheet = true
        }) { Icon(
            painterResource(id = if(isExist) R.drawable.star_filled else  R.drawable.star ), contentDescription = "") }
        IconButton(onClick = {
            on = { ClipBoardUtils.copy(it) }
            showBottomSheet = true
        }) { Icon(
            painterResource(id = R.drawable.copy_all), contentDescription = "") }

        IconButton(onClick = {
            on = { ShareTo.shareString(currentUrl) }
            showBottomSheet = true
        }) { Icon(
            painterResource(id = R.drawable.ios_share), contentDescription = "") }

        if(enableControlCenter) {
            IconButton(onClick = {
                scope.launch {
                    visible = false
                    drawerState.animationOpen()
                }
            }) { Icon(painterResource(id = R.drawable.flash_on), contentDescription = "") }
        }
    }
    var topColor by remember { mutableStateOf<Color?>(null) }
    val topBarTitleColor = topColor?.let {
        if (it.luminance() < 0.5f) Color.White else Color.Black
    } ?: MaterialTheme.colorScheme.primary
    val route = remember { AppNavRoute.WebView.shareRoute(url) }
    LaunchedEffect(topColor) {
        onColor(topColor)
    }

    BackHandler {
        if(fullScreen) {
            fullScreen = false
        }
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            if(backCount > 0 && loading == false) {
                showToast("再滑一次退出")
                backCount--
            } else {
                navController.popBackStack()
            }
        }
    }


        CustomTransitionScaffold (
            enablePredictive = false,
            route = route,
            navHostController = navController,
            topBar = {
                AnimatedVisibility(
                    visible = !fullScreen,
                    enter = AppAnimationManager.toTopAnimation.enter,
                    exit = AppAnimationManager.toTopAnimation.exit
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = topColor ?: MaterialTheme.colorScheme.surface,
                            titleContentColor = topBarTitleColor,
                            scrolledContainerColor = Color.Transparent,
                        ),
                        actions = {
                            Row{
                                if(!visible) {
                                    IconButton(
                                        onClick = { visible = true }
                                    ) {
                                        Icon(painterResource(R.drawable.more_vert),null, tint = topBarTitleColor)
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            WebViewBackIcon(webView,icon,navController,route,topBarTitleColor)
                        },
                        title = {
                            Column {
                                ScrollText(currentTitle)
                                ScrollText(
                                    getPureUrl(currentUrl),
                                    modifier = Modifier.padding(start = 2.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            val bottom = innerPadding.calculateBottomPadding().value.toInt() + APP_HORIZONTAL_DP.value.toInt()
//            val top = innerPadding.calculateTopPadding().value.toInt()

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = visible,
                    enter = AppAnimationManager.hiddenRightAnimation.enter,
                    exit = AppAnimationManager.hiddenRightAnimation.exit,
                    modifier = Modifier
                        .align(Alignment.CenterEnd).zIndex(1f)
                ) {
                    VerticalFloatingToolbar (
                        expanded = true,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = APP_HORIZONTAL_DP)
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
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            enableSlowWholeDocumentDraw()
                            setWebContentsDebuggingEnabled(true)
                            settings.safeBrowsingEnabled = false
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
                                        val js = FORCE_DARK_JS.trimIndent()
                                        view?.evaluateJavascript(js, null)
                                    }

                                    selectColor(view) { topColor = it }
                                    // document.body.style.paddingTop = '${top}px';
                                    // 注入 JS，为网页内容添加 padding（单位 px）
                                    view?.evaluateJavascript(getPaddingPxJs(null,bottom), null)
                                    loading = false
                                }

                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): WebResourceResponse? {
                                    val req = request
                                    if(req != null) {
                                        val c = req.requestHeaders["Cookie"]
                                        if(currentUrl.startsWith(MyApplication.PE_URL)) {
                                            cookieManager.setCookie(req.url.toString(), cookies)
                                            cookieManager.flush()
                                        } else
                                        if(cookies != null && c?.contains(cookies) == false) {
                                            cookieManager.setCookie(req.url.toString(), cookies)
                                            cookieManager.flush()
                                        }
                                    }

                                    return super.shouldInterceptRequest(view, request)
                                }
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    loading = true
                                    val urlS = request?.url.toString()

                                    if (urlS.contains("download")) { // 识别下载链接
                                        Starter.startWebUrl(context,urlS)
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
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
//    }
}

private const val FORCE_DARK_CSS = """
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
                            """

const val FORCE_DARK_JS = """
                                (function() {
                                    let style = document.createElement('style');
                                    style.type = 'text/css';
                                    style.appendChild(document.createTextNode(`$FORCE_DARK_CSS`));
                                    document.head.appendChild(style);
                                })();
                            """

fun getPaddingPxJs(top : Int?,bottom : Int?) : String = """
        (function() {
            ${top?.let { "document.body.style.paddingTop = '${it}px';" }}
            ${bottom?.let { "document.body.style.paddingBottom = '${it}px';" }}
        })();
""".trimIndent()

// 从左取到右，传入step一定间距，最后选择取色中最多同色的那个
fun selectColor(view : WebView?,step : Int = 3,onColor : (Color?) -> Unit) {
    view?.postDelayed({
        try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            // 读取顶部某个位置的像素颜色，比如(10, 10)
            // 取色从最左侧、最右侧、中间
            val width = bitmap.width
            val height = bitmap.height
            // 从左取到右，传入step一定间距，最后选择取色中最多同色的那个
            val y = 0
            val colorMap = mutableMapOf<Int, Int>()
            for (i in 0..step) {
                val x = (i * width) / step
                val color = bitmap[x.coerceAtMost(width - 1), y]
                colorMap[color] = colorMap.getOrDefault(color, 0) + 1
            }

            val mostFrequentColor = colorMap.maxByOrNull { it.value }?.key
            onColor(mostFrequentColor?.let { Color(it) })
        } catch (e: Exception) {
            e.printStackTrace()
            onColor(null)
        }
    }, 100)
}