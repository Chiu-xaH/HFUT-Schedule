package com.hfut.schedule.ui.util.webview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.ClipBoardHelper
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.color.ColorMode
import com.hfut.schedule.ui.util.navigation.AppAnimationManager
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.shared.LogUtil
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

fun getWebView(
    context : Context,
    onWebView : WebView.() -> Unit = {}
): WebView {
    return WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        WebView.enableSlowWholeDocumentDraw()
        WebView.setWebContentsDebuggingEnabled(true)
        settings.safeBrowsingEnabled = false

        onWebView()
    }
}

@Composable
fun rememberWebView(
    onWebView: WebView.() -> Unit = {}
): State<WebView?> {
    val context = LocalContext.current.applicationContext
    return produceState<WebView?>(initialValue = null) {
        value = getWebView(context, onWebView)
    }
}

@Composable
fun WebViewTools(
    webView: WebView?,
    onExit : () -> Unit,
    currentUrl : String,
    currentTitle : String,
    url : String,
    onSearch : () -> Unit,
    onExtend :  @Composable (() -> Unit) = {},
) {
    val scope = rememberCoroutineScope()
    var click by remember { mutableStateOf(false) }
    var on by remember { mutableStateOf<(String) -> Unit>({}) }
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var operationFavorite by remember { mutableStateOf(false) }
    val isExist by produceState(initialValue = false, key1 = click) {
        value = DataBaseManager.webUrlDao.isExist(currentUrl)
    }

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
            HazeBottomSheet (
                onDismissRequest = { showBottomSheet = false },
                showBottomSheet = showBottomSheet,
//                isFullScreen = false
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
                HazeBottomSheet (
                    onDismissRequest = { showBottomSheet = false },
                    showBottomSheet = showBottomSheet,
//                    isFullScreen = false
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

    if(webView?.canGoBack() == true) {
        IconButton(onClick = {
            webView.goBack()
        }) { Icon(painterResource(R.drawable.arrow_back), contentDescription = "") }
    } else {
        IconButton(onClick = onExit) { Icon(painterResource(R.drawable.close), contentDescription = "") }
    }

    IconButton(onClick = { webView?.reload() }) { Icon(
        painterResource(id = R.drawable.rotate_right), contentDescription = "") }

    IconButton(onClick = {
        on = { Starter.startWebUrlOuter(context,it) }
        showBottomSheet = true
    }) { Icon(
        painterResource(id = R.drawable.net), contentDescription = "") }

    IconButton(onClick = {
        onSearch()
    }) { Icon(painterResource(id = R.drawable.search), contentDescription = "") }

    IconButton(onClick = {
        on = {}
        operationFavorite = true
        showBottomSheet = true
    }) { Icon(
        painterResource(id = if(isExist) R.drawable.star_filled else  R.drawable.star ), contentDescription = "") }
    IconButton(onClick = {
        on = { ClipBoardHelper.copy(it) }
        showBottomSheet = true
    }) { Icon(
        painterResource(id = R.drawable.copy_all), contentDescription = "") }

    IconButton(onClick = {
        on = { ShareTo.shareString(currentUrl) }
        showBottomSheet = true
    }) { Icon(
        painterResource(id = R.drawable.ios_share), contentDescription = "") }

    onExtend()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewTopBar(
    webView: WebView?,
    search : Boolean,
    fullScreen : Boolean,
    topColor : Color?,
    topBarTitleColor : Color,
    visible : Boolean,
    onVisible : (Boolean) -> Unit,
    navigationIcon: @Composable (() -> Unit),
    currentTitle : String,
    currentUrl : String
) {
    var input by remember { mutableStateOf("") }
    var activeMatchOrdinal by remember { mutableStateOf(0) }
    var numberOfMatches by remember { mutableStateOf(0) }

    val searchEmpty by remember(numberOfMatches) { derivedStateOf { numberOfMatches == 0 } }

    AnimatedVisibility(
        visible = !fullScreen,
        enter = AppAnimationManager.toTopAnimation.enter,
        exit = AppAnimationManager.toTopAnimation.exit
    ) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topColor ?: MaterialTheme.colorScheme.surface,
                    titleContentColor = topBarTitleColor,
                    scrolledContainerColor = Color.Transparent,
                ),
                actions = {
                    Row {
                        if (!visible) {
                            IconButton(
                                onClick = { onVisible(true) }
                            ) {
                                Icon(
                                    painterResource(R.drawable.more_vert),
                                    null,
                                    tint = topBarTitleColor
                                )
                            }
                        }
                    }
                },
                navigationIcon = navigationIcon,
                title = {
                    if(search) {
                        LaunchedEffect(Unit) {
                            webView?.setFindListener { a, n, isDoneCounting ->
                                if (isDoneCounting) {
                                    numberOfMatches = n
                                    activeMatchOrdinal = if(!searchEmpty) {
                                        a+1
                                    } else {
                                        0
                                    }
                                }
                            }
                        }
                        CustomTextField(
                            modifier = Modifier.padding(end = APP_HORIZONTAL_DP, bottom = APP_HORIZONTAL_DP, top = APP_HORIZONTAL_DP),
                            input = input,
                            leadingIcon = {
                                Icon(painterResource(R.drawable.search),null)
                            },
                            label = {
                                Text(
                                    "页面内搜索" +
                                            if(input.isEmpty()) ""
                                            else if(searchEmpty) " 无结果"
                                            else " ${activeMatchOrdinal}/$numberOfMatches"
                                )
                            },
                            shape = CircleShape,
                            trailingIcon = {
                                Row {
                                    IconButton(
                                        onClick = {
                                            webView?.findNext(false)
                                        },
                                        enabled = !searchEmpty
                                    ) {
                                        Icon(painterResource(R.drawable.keyboard_arrow_left),null)
                                    }
                                    IconButton(
                                        onClick = {
                                            webView?.findNext(true)
                                        },
                                        enabled = !searchEmpty
                                    ) {
                                        Icon(painterResource(R.drawable.keyboard_arrow_right),null)
                                    }
                                }
                            }
                            // 翻页器
                        ) {
                            input = it
                            // 搜索
                            webView?.findAllAsync(input)
                        }
                    } else {
                        Column {
                            ScrollText(currentTitle)
                            ScrollText(
                                getPureUrl(currentUrl),
                                modifier = Modifier.padding(start = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                },
            )
        }
    }
}


@Composable
fun WebViewBackHandler(
    webView: WebView?,
    fullScreen : Boolean,
    loading : Boolean,
    onFullScreen: (Boolean) -> Unit,
    onExit : () -> Unit,
) {
    var backCount by remember { mutableIntStateOf(1) }
    BackHandler {
        if(fullScreen) {
            onFullScreen(false)
        }
        if (webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            if(backCount > 0 && loading == false) {
                showToast("再滑一次退出")
                backCount--
            } else {
                onExit()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WebViewContent(
    innerPadding : PaddingValues,
    visible : Boolean,
    tools : @Composable (() -> Unit),
    onVisible: (Boolean) -> Unit,
    loading: Boolean,
    factory: (Context) -> WebView
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = AppAnimationManager.hiddenRightAnimation.enter,
            exit = AppAnimationManager.hiddenRightAnimation.exit,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(1f)
        ) {
            VerticalFloatingToolbar (
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = APP_HORIZONTAL_DP),
                expanded = true,
            ) {
                tools()
                IconButton(onClick = { onVisible(false) }) { Icon(
                    painterResource(id = R.drawable.visibility_off), contentDescription = "") }
            }
        }
        if(loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(innerPadding)
                    .zIndex(2f)
            )
        }
        AndroidView(
            factory = factory,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Language("css")
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

@Language("javascript")
const val FORCE_DARK_JS = """
                                (function() {
                                    let style = document.createElement('style');
                                    style.type = 'text/css';
                                    style.appendChild(document.createTextNode(`$FORCE_DARK_CSS`));
                                    document.head.appendChild(style);
                                })();
                            """

@Language("javascript")
fun getPaddingPxJs(top : Int?,bottom : Int?) : String = """
        (function() {
            ${top?.let { "document.body.style.paddingTop = '${it}px';" }}
            ${bottom?.let { "document.body.style.paddingBottom = '${it}px';" }}
        })();
""".trimIndent()



// 从左取到右，传入step一定间距，最后选择取色中最多同色的那个
fun selectColor(
    view: WebView?,
    step: Int = 3,
    onColor: (Color?) -> Unit
) {
    view?.postDelayed({
        try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            val colorInt = pickColorFromTop(bitmap, step)

            bitmap.recycle()

            onColor(colorInt?.let { Color(it) })
        } catch (e: Exception) {
            LogUtil.error(e)
            onColor(null)
        }
    }, 100)
}

fun pickColorFromTop(
    bitmap: Bitmap,
    step: Int = 3,
): Int? {
    if (bitmap.width == 0 || bitmap.height == 0) return null

    val width = bitmap.width

    val colorMap = mutableMapOf<Int, Int>()

    for (i in 0..step) {
        val x = ((i * width) / step).coerceAtMost(width - 1)
        val color = bitmap[x, 0]
        colorMap[color] = colorMap.getOrDefault(color, 0) + 1
    }

    return colorMap.maxByOrNull { it.value }?.key
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

fun getPureUrl(url : String): String {
    return if(url.startsWith("file://")) {
        "本地"
    } else {
        url.substringAfter("://").substringBefore("/")
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WebViewBackIcon(
    webView: WebView?,
    color : Color,
    onExit : () -> Unit
) {
    val back : () -> Unit = {
        if(webView?.canGoBack() == true) {
            webView.goBack()
        } else {
            onExit()
        }
    }
    val cIcon = if(webView?.canGoBack() == true) {
        painterResource(R.drawable.arrow_back)
    } else {
        painterResource(R.drawable.close)
    }
    val button = @Composable { content  : @Composable () -> Unit ->
        Box(
            modifier = Modifier
                .combinedClickable(
                    onClick = back,
                    onLongClick = onExit
                )
        ) {
            content()
        }
    }
    button {
        Icon(cIcon, contentDescription = null, tint = color,modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
    }
}

// 监听 WebView 滚动事件
fun WebView.scrollListener(
    onVisible: (Boolean) -> Unit
) {
    setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
        if (scrollY > oldScrollY) {
            // 向下滚动
            // 隐藏ar
            onVisible(false)
        }
//        else if (scrollY < oldScrollY) {
            // 向上滚动
            // 显示topbar
//        }
    }
}

// 标题获取
fun WebView.updateTitle(
    currentUrl : String,
    onTitleChange : (String) -> Unit
) {
    if(!currentUrl.startsWith("file://")) {
        webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                title?.let { onTitleChange(it) }
                super.onReceivedTitle(view, title)
            }
        }
    }
}

// 初始加载
fun WebView.setInitial(
    cookieManager : CookieManager,
    cookies : String?,
    url: String
) {
    if(url.startsWith(Constant.UNI_APP_URL) && cookies != null) {
        val additionalHttpHeaders = HashMap<String, String>()
        additionalHttpHeaders["Authorization"] = cookies
        loadUrl(url,additionalHttpHeaders)
        return
    }
    // 启用 Cookie
    cookieManager.setAcceptCookie(true)//true
    cookieManager.setAcceptThirdPartyCookies(this, true)//true
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
}

fun sharedOverrideUrlLoading(
    request: WebResourceRequest?,
    context: Context,
    onLoading : (Boolean) -> Unit
): Boolean {
    onLoading(true)
    val url = request?.url.toString()

    if (url.contains("download")) { // 识别下载链接
        Starter.startWebUrlOuter(context,url)
        return true // 拦截 WebView 处理
    }

    return false // 继续让 WebView 处理
}

fun sharedInterceptRequest(
    request: WebResourceRequest?,
    currentUrl : String,
    cookies : String?,
    cookieManager : CookieManager,
) {
    val req = request
    if(req != null) {
        val c = req.requestHeaders["Cookie"]
        if(
            // 适配区
            currentUrl.startsWith(Constant.JXGLSTU_URL) ||
            currentUrl.startsWith(Constant.JXGLSTU_WEBVPN_URL) ||
            currentUrl.startsWith(Constant.PE_URL)
        ) {
            cookieManager.setCookie(req.url.toString(), cookies)
            cookieManager.flush()
        } else {
            if(cookies != null && c?.contains(cookies) == false) {
                cookieManager.setCookie(req.url.toString(), cookies)
                cookieManager.flush()
            }
        }
    }
}


fun updateUrl(
    url: String?,
    onUrlChange : (String) -> Unit,
) {
    url?.let { onUrlChange(it) }
}

fun WebView.evaluateJs(
    isDark : Boolean,
    webViewDark : Boolean,
    bottom : Int,
    onColorChange : (Color?) -> Unit
) {
    if (isDark && webViewDark) {
        val js = FORCE_DARK_JS.trimIndent()
        this.evaluateJavascript(js, null)
    }
    selectColor(this, onColor = onColorChange)
    // 注入 JS，为网页内容添加 padding（单位 px）
    this.evaluateJavascript(getPaddingPxJs(null,bottom), null)
}