package com.hfut.schedule.ui.screen.supabase.login

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.enumeration.SupabaseScreen
import com.hfut.schedule.logic.model.SupabaseLoginResponse
import com.hfut.schedule.logic.util.network.reEmptyLiveDta
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.sys.Starter.loginSupabase
import com.hfut.schedule.logic.util.sys.Starter.startSupabase
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.util.navigateAndClear
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 逻辑层
fun getSchoolEmail() : String? = getPersonInfo().username?.let { it + MyApplication.EMAIL }

suspend fun loginSupabaseWithPassword(password : String, vm: NetWorkViewModel, navHostController: NavHostController, onResult: (Boolean) -> Unit) = withContext(
    Dispatchers.IO) {
    Handler(Looper.getMainLooper()).post {
        vm.supabaseLoginResp.observeForever { result ->
            if (result != null) {
                onResult(false)
                if (result.contains("error")) {
                    showToast("密码错误或未注册")
                } else {
                    CoroutineScope(Job()).launch {
                        launch { saveSupabaseRefreshToken(vm) }
                        val isSuccessful = async { saveSupabaseJwt(vm) }.await()
                        launch {
                            if(isSuccessful) {
                                Handler(Looper.getMainLooper()).post { navHostController.navigateAndClear(
                                    SupabaseScreen.HOME.name) }
                                showToast("登录成功")
                            }
                        }
                    }
                }
            }
        }
    }
    launch { saveString("SupabasePsk",password) }
    async { onResult(true) }.await()
    launch { vm.supabaseLoginWithPassword(password) }
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

private suspend fun saveSupabaseJwt(vm: NetWorkViewModel) : Boolean = withContext(Dispatchers.IO) {
    val token = getJwt(vm) ?: return@withContext false
    DataStoreManager.saveSupabaseJwt(token)
    return@withContext true
}

private suspend fun saveSupabaseRefreshToken(vm: NetWorkViewModel) : Boolean = withContext(
    Dispatchers.IO) {
    val token = getRefreshToken(vm) ?: return@withContext false
    DataStoreManager.saveSupabaseRefreshToken(token)
    return@withContext true
}

private fun getJwt(vm: NetWorkViewModel) : String? = try {
    Gson().fromJson(vm.supabaseLoginResp.value, SupabaseLoginResponse::class.java).token
} catch (e : Exception) {
    null
}

private fun getRefreshToken(vm: NetWorkViewModel) : String? = try {
    Gson().fromJson(vm.supabaseLoginResp.value, SupabaseLoginResponse::class.java).refreshToken
} catch (e : Exception) {
    null
}

// 一键登录
fun loginSupabaseWithCheck(jwt : String, refreshToken: String, vm: NetWorkViewModel, onResult : (Boolean) -> Unit) {

    if(jwt.isEmpty() || jwt.isBlank()) {
        onResult(false)
        loginSupabase()
        return
    }

    lateinit var checkObserver: Observer<Boolean?> // 延迟初始化观察者
    lateinit var loginObserver: Observer<String?> // 延迟初始化观察者


    checkObserver = Observer { result ->
        if(result != null) {
            if (result) {
                // 登录有效
                onResult(false)
                startSupabase()
                vm.supabaseCheckResp.removeObserver(checkObserver)
                return@Observer
            } else {
                // 需要刷新
                vm.supabaseCheckResp.removeObserver(checkObserver)
                // 只能使用密码登录法
                if(refreshToken.isEmpty() || refreshToken.isBlank()) {
                    onResult(false)
                    loginSupabase()
                    return@Observer
                }
                // 使用刷新登录
                vm.supabaseLoginWithRefreshToken(refreshToken)
                vm.supabaseLoginResp.observeForever(loginObserver)
                return@Observer
            }
        }
    }

    loginObserver = Observer { result ->
        if(result != null) {
            onResult(false)
            if (!result.contains("error")) {
                // 登录成功 保存新的JWT与TOKEN
                CoroutineScope(Job()).launch {
                    async {
                        launch { saveSupabaseJwt(vm) }
                        launch { saveSupabaseRefreshToken(vm) }
                    }.await()
                    launch {
                        startSupabase()
                        return@launch
                    }
                }
            } else {
                // 只能使用密码登录法
                vm.supabaseLoginResp.removeObserver(loginObserver)
                onResult(true)
                loginSupabase()
                vm.supabaseLoginResp.removeObserver(loginObserver)
                return@Observer
            }
        }
    }

    CoroutineScope(Job()).launch {
        val isLogin = async { vm.supabaseCheckResp.value }.await()
        if(isLogin == true) {
            onResult(false)
            startSupabase()
            return@launch
        } else {
            async { reEmptyLiveDta(vm.supabaseCheckResp) }.await()
            async { onResult(true) }.await()
            async { vm.supabaseCheckJwt(jwt) }.await()
            launch {
                Handler(Looper.getMainLooper()).post {
                    vm.supabaseCheckResp.observeForever(checkObserver)
                }
            }
        }
    }
}

