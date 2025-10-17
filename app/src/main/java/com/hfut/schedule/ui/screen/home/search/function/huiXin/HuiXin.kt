package com.hfut.schedule.ui.screen.home.search.function.huiXin

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.uicommon.component.text.ScrollText
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HuiXin(
    navController : NavHostController,
) {
    val icon = remember { R.drawable.corporate_fare }
    val title = remember { "慧新易校" }
    val url = remember { getHuiXinURL() }
    val route = AppNavRoute.WebView.shareRoute(url)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(
                    navController,
                    url = url,
                    title = title,
                    icon = icon,
                )
            }
        }
    )
}

fun getHuiXinURL(): String {
    val auth = SharedPrefs.prefs.getString("auth","")
    val urlHuixin = MyApplication.HUI_XIN_URL + "plat" + "?synjones-auth=" + auth
    return urlHuixin
}