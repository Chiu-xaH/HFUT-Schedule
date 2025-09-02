package com.hfut.schedule.ui.screen.home.search.function.my.supabase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.loginSupabase
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.supabase.login.loginSupabaseWithCheck
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

@Composable
fun Supabase(vm: NetWorkViewModel) {
    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { ScrollText(text = "共建平台") },
        leadingContent = {
            if(loading) {
                LoadingIcon()
            } else {
                Icon(painterResource(id = R.drawable.cloud), contentDescription = "")
            }
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = { loginSupabase(context) }
            ) {
                Icon(painterResource(R.drawable.refresh),null)
            }
        },
        modifier = Modifier.clickable {
           scope.launch {
               loading = true
               loginSupabaseWithCheck(jwt,refreshToken,vm,context)
               loading = false
           }
        }
    )
}
