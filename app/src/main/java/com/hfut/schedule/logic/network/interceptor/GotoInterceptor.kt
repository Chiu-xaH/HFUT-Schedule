package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hfut.schedule.logic.util.storage.SharedPrefs
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
        // 登录信息门户
        Log.d("CAS拦截器",locationStr)
        if (locationStr.contains("code=")) {
            Log.d("信息门户登录",locationStr)
            CasGoToInterceptorState.toOneCode.value = location[0]
        }
        // 登录慧新易校
        if (locationStr.contains("synjones")) {
            Log.d("慧新易校登录",locationStr)
            parseHuiXinAuth(locationStr)
        }
        // 登录一些别的平台
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