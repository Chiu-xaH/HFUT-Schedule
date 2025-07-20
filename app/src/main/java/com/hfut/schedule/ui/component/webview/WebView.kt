package com.hfut.schedule.ui.component.webview

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.WebURLEntity
import com.hfut.schedule.logic.database.entity.WebURLType
import com.hfut.schedule.logic.database.entity.WebUrlDTO
import com.hfut.schedule.logic.util.sys.ShareTo
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.util.AppAnimationManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String,cookies : String? = null,showChanged : () -> Unit,title : String,showTop : Boolean) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var visible by remember { mutableStateOf(true) }
    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            showChanged.invoke()
        }
    }
    var click by remember { mutableStateOf(false) }
    val isExist by produceState(initialValue = false, key1 = click) {
        value = DataBaseManager.webUrlDao.isExist(url)
    }
    val scope = rememberCoroutineScope()

    val tools = @Composable {
        if(webView?.canGoBack() == true) {
            IconButton(onClick = {
                webView?.goBack()
            }) { Icon(Icons.Default.ArrowBack, contentDescription = "") }
        } else {
            IconButton(onClick = {
                showChanged()
            }) { Icon(Icons.Default.Close, contentDescription = "") }
        }

        IconButton(onClick = { webView?.reload() }) { Icon(
            painterResource(id = R.drawable.rotate_right), contentDescription = "") }

        IconButton(onClick = { Starter.startWebUrl(url) }) { Icon(
            painterResource(id = R.drawable.net), contentDescription = "") }

        IconButton(onClick = {
            scope.launch {
                if(isExist) {
                    DataBaseManager.webUrlDao.delFromUrl(url)
                    showToast("已取消收藏")
                } else {
                    DataBaseManager.webUrlDao.insert(
                        WebUrlDTO(
                            name = title,
                            type = WebURLType.COLLECTION,
                            url = url
                        ).toEntity()
                    )
                    showToast("收藏成功")
                }
                click = !click
            }
        }) { Icon(
            painterResource(id = if(isExist) R.drawable.star_filled else  R.drawable.star ), contentDescription = "") }


        IconButton(onClick = { ShareTo.shareString(url) }) { Icon(
            painterResource(id = R.drawable.ios_share), contentDescription = "") }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if(showTop){
                TopAppBar(
                    colors = topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = MaterialTheme.colorScheme.primary
        ),
                    actions = {
                        Row{
                            if(!visible) {
                                IconButton(
                                    onClick = { visible = true }
                                ) {
                                    Icon(painterResource(R.drawable.more_vert),null)
                                }
                            }
                        }
                    },
                    title = { ScrollText(title) }
                )
            }
        },

        bottomBar = {
            if(!showTop){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(APP_HORIZONTAL_DP * 2.75f),
                    contentAlignment = Alignment.Center // 关键在这里
                ) {
                    if(!visible) {
                        IconButton (
                            onClick = { visible = true },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(painterResource(R.drawable.more_vert),null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Text(
                        url.substringAfter("://").substringBefore("/"),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 17.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            AnimatedVisibility(
                visible = visible,
                enter = AppAnimationManager.hiddenRightAnimation.enter,
                exit = AppAnimationManager.hiddenRightAnimation.exit,
                modifier = Modifier.padding(innerPadding).padding(horizontal = APP_HORIZONTAL_DP).align(Alignment.CenterEnd).zIndex(1f)
            ) {
                VerticalFloatingToolbar (expanded = true) {
                    tools()
                    IconButton(onClick = { visible = false }) { Icon(
                        painterResource(id = R.drawable.visibility_off), contentDescription = "") }
                }
            }
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false


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

                        webViewClient = object : WebViewClient() {
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
                                val urlS = request?.url.toString()

                                if (urlS.contains("download")) { // 识别下载链接
                                    Starter.startWebUrl(urlS)
                                    return true // 拦截 WebView 处理
                                }

                                return false // 继续让 WebView 处理
                            }
                        }
                        webView = this
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}