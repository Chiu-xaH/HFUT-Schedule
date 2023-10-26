package com.hfut.schedule.logic.network.okHttp.interceptor

import okhttp3.Interceptor
import okhttp3.Response
//提交到POST，从RI解析出来的Cookie，只提交SESSION即可
class AddInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        TODO()
    }
}