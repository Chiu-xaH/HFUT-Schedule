package com.hfut.schedule.logic.network.ServiceCreator.Jxglstu

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.interceptor.SurveyTokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object JxglstuHTMLServiceCreator {


    val retrofit = Retrofit.Builder()
       .baseUrl(MyApplication.JxglstuURL)
       .addConverterFactory(ScalarsConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}

object JxglstuJSONServiceCreator {

    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.JxglstuURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)

}
object JxglstuSurveyServiceCreator {



    val Client = OkHttpClient.Builder()
        .followRedirects(false)
        .addInterceptor(SurveyTokenInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(MyApplication.JxglstuURL)
        .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified  T> create() : T = create(T::class.java)
}
