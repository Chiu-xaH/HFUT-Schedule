package com.hfut.schedule.logic.network.ServiceCreator.Login

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.ServiceCreator.BaseServiceCreator
import com.hfut.schedule.logic.network.interceptor.RedirectInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object LoginWebServiceCreator : BaseServiceCreator(MyApplication.loginWebURL,false)

object LoginWeb2ServiceCreator : BaseServiceCreator(MyApplication.loginWebURL2,false)