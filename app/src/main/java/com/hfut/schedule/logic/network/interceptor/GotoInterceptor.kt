package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Interceptor
import okhttp3.Response

class GotoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val location = response.headers("Location")
        val locationStr = location.toString()
        Log.d("CAS拦截器",locationStr)
        when {
            locationStr.contains("code=") -> {
                // 登录信息门户
                CasGoToInterceptorState.toOneCode.value = location[0]
            }
            locationStr.contains("synjones") -> {
                // 登录慧新易校
                parseHuiXinAuth(locationStr)
            }
        }
        return response
    }
}

private fun parseHuiXinAuth(location : String) {
    var key = location.substringAfter("synjones-auth=").substringBefore("&")
    SharedPrefs.saveString("auth",key)
    showToast("一卡通登录成功")
}

object CasGoToInterceptorState {
    var toOneCode = MutableStateFlow<String?>(null)
    var toCommunityTicket = MutableStateFlow<String?>(null)
}