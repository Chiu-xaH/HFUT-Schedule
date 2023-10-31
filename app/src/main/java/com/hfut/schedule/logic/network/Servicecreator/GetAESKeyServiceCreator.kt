package com.hfut.schedule.logic.network.Servicecreator

import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.network.interceptor.AESKeyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetAESKeyServiceCreator {

    val Client = OkHttpClient.Builder()
        .addInterceptor(AESKeyInterceptor())//提取密钥
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LoginURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}