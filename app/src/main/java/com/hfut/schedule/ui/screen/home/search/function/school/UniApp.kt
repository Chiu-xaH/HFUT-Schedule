package com.hfut.schedule.ui.screen.home.search.function.school

import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun UniApp(
    navController : NavHostController,
) {
    val icon = remember { R.drawable.wechat }
    val title = remember { "合工大教务" }
    val url = remember { "https://jwglapp.hfut.edu.cn/uniapp/#/pages/tab/main/main"}
    val route = AppNavRoute.WebView.shareRoute(url)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val jwt by DataStoreManager.uniAppJwt.collectAsState(initial = null)
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null,
//                modifier = Modifier.iconElementShare(route = route)
            )
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(
                    context,
//                    navController,
                    url = url,
                    title = title,
                    cookie = jwt,
                    icon = icon,
                )
            }
        }
    )
}

