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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.network.onListenStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.launch

@Composable
fun ApiToSupabase(vm : NetWorkViewModel) {
//    val supabaseAutoCheck by DataStoreManager.enableSupabaseAutoCheck.collectAsState(initial = true)
//    val scope = rememberCoroutineScope()
//    val jwt by DataStoreManager.supabaseJwt.collectAsState(initial = "")
//    val check by vm.supabaseCheckResp.state.collectAsState()

//    val refreshToken by DataStoreManager.supabaseRefreshToken.collectAsState(initial = "")
//    var showBadge by remember { mutableStateOf(false) }

//    val uiState by vm.supabaseGetEventLatestResp.state.collectAsState()
//    val loading = uiState is UiState.Loading
    // 预加载 兼顾检查登陆状态
//    LaunchedEffect(jwt,supabaseAutoCheck) {
//        if((jwt.isNotBlank() || jwt.isNotEmpty()) && supabaseAutoCheck) {
//            if(check is UiState.Prepare) {
//                vm.supabaseGetEventLatest(jwt)
//                onListenStateHolder(vm.supabaseGetEventLatestResp,onError = { _,_ -> }) { result ->
//                    vm.supabaseCheckResp.emitData(true)
//                    if(result) {
//                        // 有新的日程
//                        showBadge = true
//                    }
//                }
//            }
//        } else {
//            vm.supabaseGetEventLatestResp.emitPrepare()
//        }
//    }
    val context = LocalContext.current


    IconButton(
        onClick = {
            Starter.startSupabase(context)
//            scope.launch {
//                loginSupabaseWithCheck(jwt,refreshToken,vm, context)
//            }
        },
        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(painterResource(id = R.drawable.cloud), contentDescription = "")
//        BadgedBox(
//            badge = {
//                if (showBadge) Badge()
//            }
//        ) {  }
    }
}

