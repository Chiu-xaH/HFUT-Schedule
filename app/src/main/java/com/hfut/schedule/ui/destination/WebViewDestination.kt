package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.util.NavDestination
import com.xah.uicommon.util.language.res

data class WebViewDestination(
    val url : String,
    val cookies : String?,
    val name : String,
    override val icon : Int = R.drawable.net
) : NavDestination() {
    override val key = getKey(url)
    override val title = res(R.string.navigation_label_web)

    companion object {
        fun getKey(url : String): String {
            return "web_${url}"
        }
    }
    @Composable
    override fun Content() {
//        val url = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.URL.argName) ?: return@transitionComposable
//        val cookies = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.COOKIES.argName)
//        val title = backStackEntry.arguments?.getString(AppNavRoute.WebView.Args.TITLE.argName) ?: getPureUrl(url)
//        val icon = backStackEntry.arguments?.getInt(AppNavRoute.WebView.Args.ICON.argName)
//
//        WebViewScreenForNavigation(
//            url,
//            title,
//            icon,
//            cookies,
//            navController,
//            drawerState
//        ) { containerColor = it }
//        WebViewScreenForNavigation(url, name, icon, cookies)
    }
}