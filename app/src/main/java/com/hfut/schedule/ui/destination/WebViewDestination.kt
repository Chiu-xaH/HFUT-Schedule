package com.hfut.schedule.ui.destination

import androidx.compose.runtime.Composable
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.webview.WebViewScreenForNavigation
import com.hfut.schedule.ui.screen.grade.GradeScreen
import com.hfut.schedule.ui.util.NavDestination
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.uicommon.util.language.res

data class WebViewDestination(
    val url : String,
    val cookies : String?,
    val name : String,
    val icon : Int?
) : NavDestination() {
    override val key = "web"
    override val title = res(R.string.navigation_label_web)

    @Composable
    override fun Content() {
//        WebViewScreenForNavigation(url, name, icon, cookies)
    }
}