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
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginSupabase
import com.hfut.schedule.logic.util.sys.Starter.startSupabase
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.xah.transition.util.navigateAndClear
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
suspend fun loginSupabaseWithCheck(jwt : String, refreshToken: String, vm: NetWorkViewModel,context : Context) = withContext(Dispatchers.IO) {

    if(jwt.isEmpty() || jwt.isBlank()) {
//        onResult(false)
        loginSupabase(context)
        return@withContext
    }

//    lateinit var checkObserver: Observer<Boolean?> // 延迟初始化观察者
//    lateinit var loginObserver: Observer<String?> // 延迟初始化观察者

//
//    checkObserver = Observer { result ->
//        if(result != null) {
//            if (result) {
//                // 登录有效
//                onResult(false)
//                startSupabase()
//                vm.supabaseCheckResp.removeObserver(checkObserver)
//                return@Observer
//            } else {
//                // 需要刷新
//                vm.supabaseCheckResp.removeObserver(checkObserver)
//                // 只能使用密码登录法
//                if(refreshToken.isEmpty() || refreshToken.isBlank()) {
//                    onResult(false)
//                    loginSupabase()
//                    return@Observer
//                }
//                // 使用刷新登录
//                vm.supabaseLoginWithRefreshToken(refreshToken)
//                vm.supabaseLoginResp.observeForever(loginObserver)
//                return@Observer
//            }
//        }
//    }

//    loginObserver = Observer { result ->
//        if(result != null) {
//            onResult(false)
//            if (!result.contains("error")) {
//                // 登录成功 保存新的JWT与TOKEN
//                CoroutineScope(Job()).launch {
//                    async {
//                        launch { saveSupabaseJwt(vm) }
//                        launch { saveSupabaseRefreshToken(vm) }
//                    }.await()
//                    launch {
//                        startSupabase()
//                        return@launch
//                    }
//                }
//            } else {
//                // 只能使用密码登录法
//                vm.supabaseLoginResp.removeObserver(loginObserver)
//                onResult(true)
//                loginSupabase()
//                vm.supabaseLoginResp.removeObserver(loginObserver)
//                return@Observer
//            }
//        }
//    }
    val checkLogin : suspend () -> Unit = checkLogin@ {
        vm.supabaseCheckJwt(jwt)
        val check2 = (vm.supabaseCheckResp.state.value as? UiState.Success)?.data ?: return@checkLogin
        if (check2) {
            // 登录有效
            startSupabase(context)
            return@checkLogin
        } else {
            // 需要刷新
            // 只能使用密码登录法
            if(refreshToken.isEmpty() || refreshToken.isBlank()) {
                loginSupabase(context)
                return@checkLogin
            }
            // 使用刷新登录
            vm.supabaseLoginWithRefreshToken(refreshToken)
            val login = (vm.supabaseLoginResp.state.value as? UiState.Success)?.data
            if(login == null) {
                // 只能使用密码登录法
                loginSupabase(context)
                return@checkLogin
            } else {
                // 登录成功 保存新的JWT与TOKEN
                async {
                    launch { DataStoreManager.saveSupabaseRefreshToken(login.refreshToken) }
                    launch { DataStoreManager.saveSupabaseJwt(login.token) }
                }.await()
                launch { startSupabase(context) }
                return@checkLogin
            }
            return@checkLogin
        }
    }
    val check = vm.supabaseCheckResp.state.first()
    if(check is UiState.Success) {
        if(check.data) {
            startSupabase(context)
            return@withContext
        } else {
            return@withContext checkLogin()
        }
    } else {
        return@withContext checkLogin()
    }
//    CoroutineScope(Job()).launch {
//        val isLogin = async { vm.supabaseCheckResp.value }.await()
//        if(isLogin == true) {
//            onResult(false)
//            startSupabase()
//            return@launch
//        } else {
//            async { reEmptyLiveDta(vm.supabaseCheckResp) }.await()
//            async { onResult(true) }.await()
//            async { vm.supabaseCheckJwt(jwt) }.await()
//            launch {
//                Handler(Looper.getMainLooper()).post {
//                    vm.supabaseCheckResp.observeForever(checkObserver)
//                }
//            }
//        }
//    }
}

