package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.logic.network.interceptor.AESKeyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetAESKeyServiceCreator {
    const val URL = "https://cas.hfut.edu.cn/"

    val Client = OkHttpClient.Builder()
        .addInterceptor(AESKeyInterceptor())//提取密钥
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}