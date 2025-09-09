package com.hfut.schedule.logic.network.servicecreator.login

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.servicecreator.BaseServiceCreator
import com.hfut.schedule.logic.network.interceptor.RedirectInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginServiceCreator {

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .followSslRedirects(false)
        .addInterceptor(RedirectInterceptor())
        .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.CAS_LOGIN_URL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}
object LoginWebVpnServiceCreator : BaseServiceCreator(MyApplication.WEBVPN_URL)