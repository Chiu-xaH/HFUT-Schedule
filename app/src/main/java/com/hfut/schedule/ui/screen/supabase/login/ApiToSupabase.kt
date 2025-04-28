package com.hfut.schedule.ui.screen.supabase.login

import android.os.Handler
import android.os.Looper
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
import androidx.lifecycle.Observer
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.component.RotatingIcon
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun ApiToSupabase(vm : NetWorkViewModel) {
    var loading by remember { mutableStateOf(false) }
    val supabaseAutoCheck by DataStoreManager.supabaseAutoCheck.collectAsState(initial = true)

    val jwt by DataStoreManager.supabaseJwtFlow.collectAsState(initial = "")
    val refreshToken by DataStoreManager.supabaseRefreshTokenFlow.collectAsState(initial = "")
    var showBadge by remember { mutableStateOf(false) }
    // 预加载 兼顾检查登陆状态
    lateinit var observer: Observer<Boolean?> // 延迟初始化观察者

    LaunchedEffect(jwt,supabaseAutoCheck) {
        if((jwt.isNotBlank() || jwt.isNotEmpty()) && supabaseAutoCheck && vm.supabaseCheckResp.value == null) {
            async { loading = true }.await()
            async { vm.supabaseGetEventLatest(jwt) }.await()
            async {
                observer = Observer { result ->
                    if(result != null) {
                        if(result) {
                            vm.supabaseGetEventLatestResp.value?.let {
                                if(prefs.getString("SUPABASE_LATEST",null) != it) {
                                    showBadge = true
                                    saveString("SUPABASE_LATEST",it)
                                } else {
                                    showBadge = false
                                }
                            }
                            vm.supabaseCheckResp.value = true
                        } else {
                            vm.supabaseCheckResp.value = false
                        }
                        loading = false
                    }

                }
            }.await()
            launch {
                Handler(Looper.getMainLooper()).post {
                    vm.supabaseGetEventLatestStatusResp.observeForever(observer)
                }
            }
        }
    }


    IconButton(
        onClick = {
            loginSupabaseWithCheck(jwt,refreshToken,vm) { loading = it }
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

