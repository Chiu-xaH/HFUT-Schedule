package com.hfut.schedule.logic.network.ServiceCreator


import com.hfut.cookiedemo.MyInterceptor
import com.hfut.schedule.logic.network.okHttp.interceptor.AESKeyInterceptor
import com.hfut.schedule.logic.network.okHttp.interceptor.AddInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object LoginServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        .addNetworkInterceptor(AESKeyInterceptor())//提取密钥
       // .addNetworkInterceptor(AddInterceptor())//提交解析出的Cookie到POST
        //.cookieJar(PersistenceCookieJar())
        //.addNetworkInterceptor(MyInterceptor())//获取响应与请求头
        //.followRedirects(false)
        //.followSslRedirects(true)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}