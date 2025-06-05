package com.hfut.schedule.ui.screen.supabase.login

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

@Composable
fun ApiToSupabase(vm : NetWorkViewModel) {
    val supabaseAutoCheck by DataStoreManager.supabaseAutoCheck.collectAsState(initial = true)

    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
    val refreshToken by DataStoreManager.supabaseRefreshTokenFlow.collectAsState(initial = "")
    var showBadge by remember { mutableStateOf(false) }

    val uiState by vm.supabaseGetEventLatestResp.state.collectAsState()
    val loading = uiState is UiState.Loading
    // 预加载 兼顾检查登陆状态
    LaunchedEffect(jwt,supabaseAutoCheck) {
        if((jwt.isNotBlank() || jwt.isNotEmpty()) && supabaseAutoCheck) {
            if(vm.supabaseCheckResp.value == null) {
                vm.supabaseGetEventLatest(jwt)
                onListenStateHolder(vm.supabaseGetEventLatestResp,onError = { _,_ -> }) { result ->
                    vm.supabaseCheckResp.value = true
                    if(result) {
                        // 有新的日程
                        showBadge = true
                    }
                }
            }
        } else {
            vm.supabaseGetEventLatestResp.emitPrepare()
        }
    }


    IconButton(
        onClick = {
            loginSupabaseWithCheck(jwt,refreshToken,vm) {}
        },
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        if(loading) {
            RotatingIcon(R.drawable.progress_activity)
        } else {
            BadgedBox(
                badge = {
                    if (showBadge) Badge()
                }
            ) { Icon(painterResource(id = R.drawable.cloud), contentDescription = "") }
        }
    }
}

