package com.hfut.schedule.logic.network.ServiceCreator

import com.hfut.schedule.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LePaoYunServiceCreator {
    // val Client = OkHttpClient.Builder()
    //  .followRedirects(false)
    //   .addInterceptor(RedirectInterceptor())
    //   .build()


    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.LePaoYunURL)
        //  .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}