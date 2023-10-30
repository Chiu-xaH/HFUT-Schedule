package com.hfut.schedule.logic.network

import android.util.Log
import com.google.gson.JsonObject
import com.hfut.schedule.logic.network.ServiceCreator.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.LoginServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Body
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {
    private val GetAESKey = GetAESKeyServiceCreator.create<LoginService>()

    private val GetCookie = GetCookieServiceCreator.create<LoginService>()

    private val Jxglstu = JxglstuServiceCreator.create<JxglstuService>()

    private val Login = LoginServiceCreator.create<LoginService>()


    suspend fun login(username : String,password : String,keys : String) = Login.login(username,password,keys,"e1s1","submit").await()

    suspend fun login2(username : String,password : String,keys : String) = Login.login2(username,password,keys,"e1s1","submit").await()

    suspend fun getKey() = GetAESKey.getKey().await()

    suspend fun getCookie() = GetCookie.getCookie().await()

    suspend fun jxglstulogin(cookie : String) = Jxglstu.jxglstulogin(cookie).await()

    suspend fun getDatum(cookie: String) = Jxglstu.getDatum(cookie).await()

    suspend fun getDatum2(cookie: String) = Jxglstu.getDatum2(cookie).await()

    suspend fun getCourse(Cookie : String, json: JsonObject) = Jxglstu.getCourse(Cookie,json).await()


    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if(response.isSuccessful()) {
                        Log.d("响应码", response.code().toString())
                        Log.d("响应头", response.headers().toString())
                        Log.d("响应信息",response.message())
                        response.body()?.toString()?.let { Log.d("响应主体", it) }
                    }
                    else {
                        Log.d("测试","失败")
                        Log.d("响应码", response.code().toString())
                         Log.d("响应头", response.headers().toString())
                         Log.d("响应信息",response.message())
                        Log.d("响应主体",response.body().toString())
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                    Log.d("VM","失败")
                    t.printStackTrace()
                }
            })
        }
    }


}