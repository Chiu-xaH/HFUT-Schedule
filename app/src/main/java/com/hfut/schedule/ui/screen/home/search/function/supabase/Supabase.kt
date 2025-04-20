package com.hfut.schedule.ui.screen.home.search.function.supabase

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.screen.supabase.login.loginSupabaseWithCheck
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun Supabase(vm: NetWorkViewModel) {
    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
    val refreshToken by DataStoreManager.supabaseRefreshTokenFlow.collectAsState(initial = "")

    var loading by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "信息共建") },
        leadingContent = {
            if(loading) {
                RotatingIcon(R.drawable.progress_activity)
            } else {
                Icon(painterResource(id = R.drawable.database), contentDescription = "")
            }
        },
        modifier = Modifier.clickable {
            loginSupabaseWithCheck(jwt,refreshToken,vm) { loading = it }
        }
    )
}
