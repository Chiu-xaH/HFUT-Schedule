package com.hfut.schedule.ui.screen.home.search.function.huiXin

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute

import com.xah.common.component.text.ScrollText
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HuiXin() {
    val context = LocalContext.current
    val icon = remember { R.drawable.corporate_fare }
    val title = stringResource(R.string.navigation_label_hui_xin)
    val url = remember { getHuiXinURL() }
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(
                    context,
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
    val urlHuixin = Constant.HUI_XIN_URL + "plat" + "?synjones-auth=" + auth
    return urlHuixin
}