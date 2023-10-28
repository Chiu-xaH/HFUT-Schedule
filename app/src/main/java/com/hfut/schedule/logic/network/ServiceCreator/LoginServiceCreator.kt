package com.hfut.schedule.logic.network.ServiceCreator


import com.hfut.schedule.logic.network.interceptor.AESKeyInterceptor
import com.hfut.schedule.logic.network.interceptor.AddInterceptor
import com.hfut.schedule.logic.network.interceptor.ReceiveInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object LoginServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        //.addInterceptor(AddInterceptor())//提交解析出的Cookie到POST
        .followRedirects(true)
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}