package com.hfut.schedule.ui.screen.supabase.login

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.model.SupabaseLoginResponse
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.network.state.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginSupabase
import com.hfut.schedule.logic.util.sys.Starter.startSupabase
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.util.navigation.navigateAndClear
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 逻辑层
fun getSchoolEmail() : String? = getPersonInfo().studentId?.let { it + MyApplication.EMAIL }

suspend fun loginSupabaseWithPassword(password : String, vm: NetWorkViewModel, navHostController: NavHostController) = withContext(Dispatchers.IO) {
    vm.supabaseLoginResp.clear()
    saveString("SupabasePsk",password)
    vm.supabaseLoginWithPassword(password)
    val result = vm.supabaseLoginResp.state.first()
    when(result) {
        is UiState.Success -> {
            // 保存
            async {
                launch { DataStoreManager.saveSupabaseRefreshToken(result.data.refreshToken) }
                launch { DataStoreManager.saveSupabaseJwt(result.data.token) }
            }.await()
            launch {
                Handler(Looper.getMainLooper()).post { navHostController.navigateAndClear(SupabaseScreen.HOME.name) }
                showToast("登录成功")
            }
        }
        is UiState.Error<*> -> showToast("密码错误或未注册")
        else -> {}
    }

}

suspend fun regSupabase(password : String, vm: NetWorkViewModel, onResult : (Boolean) -> Unit) = withContext(
    Dispatchers.IO) {
    Handler(Looper.getMainLooper()).post {
        vm.supabaseRegResp.observeForever { result ->
            result?.let {
                onResult(false)
                showToast(
                    if (it.contains("email_verified"))
                        "已注册，请回到 查询中心-邮箱 检查收件箱，点击链接激活成功后再来登录"
                    else
                        "已存在账号或账号不合法"
                )
            }
        }
    }
    launch { saveString("SupabasePsk",password) }
    async { onResult(true) }.await()
    launch { vm.supabaseReg(password) }
}

// 一键登录
suspend fun loginSupabaseWithCheck(jwt : String, refreshToken: String, vm: NetWorkViewModel,context : Context) : Boolean = withContext(Dispatchers.IO) {
    if(jwt.isEmpty() || jwt.isBlank()) {
        // 未登陆过
        return@withContext false
    }

    suspend fun tryLogin() : Boolean {
        if(refreshToken.isEmpty() || refreshToken.isBlank()) {
            showToast("未登陆过，请前往选项中选择刷新登陆状态")
            return false
        }
        // 使用刷新登录
        vm.supabaseLoginWithRefreshToken(refreshToken)
        val login = (vm.supabaseLoginResp.state.value as? UiState.Success)?.data
        if(login == null) {
            // 只能使用密码登录法
            showToast("自动刷新登录失败，请前往选项中选手动刷新登陆状态")
            return false
        } else {
            // 登录成功 保存新的JWT与TOKEN
            showToast("自动刷新登录成功")
            async {
                launch { DataStoreManager.saveSupabaseRefreshToken(login.refreshToken) }
                launch { DataStoreManager.saveSupabaseJwt(login.token) }
            }.await()
            return true
        }
    }

    val status = vm.supabaseCheckResp.state.first()
    when(status) {
        is UiState.Success -> {
            // 已经检查过
            return@withContext true
        }
        is UiState.Error -> {
            // Token不对或过期
            return@withContext tryLogin()
        }
        else -> {
            // 未检查 检查
            vm.supabaseCheckResp.clear()
            vm.supabaseCheckJwt(jwt)
            val check2 = vm.supabaseCheckResp.state.first()
            when(check2) {
                is UiState.Success -> {
                    return@withContext true
                }
                is UiState.Error -> {
                    // Token不对或过期
                    return@withContext tryLogin()
                }
                else -> {
                    return@withContext false
                }
            }
        }
    }
}

