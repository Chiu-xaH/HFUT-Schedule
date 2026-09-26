package com.hfut.schedule.ui.screen.home.search.function.huiXin

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.ui.component.container.TransplantListItem


import com.xah.common.ui.component.text.ScrollText
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
        trailingContent = {
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        val url = Constant.HUI_XIN_URL + "plat/pay" + "?synjones-auth=" + prefs.getString("auth","")
                        Starter.startWebUrlInner(context, url,"付款码",icon = R.drawable.barcode)
                    }
                },
                modifier = Modifier.size(30.dp)
            ) {
                // 特殊缩小size
                Icon(painterResource(R.drawable.barcode),null, modifier = Modifier.size(20.dp-1.dp))
            }
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebUrlInner(
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