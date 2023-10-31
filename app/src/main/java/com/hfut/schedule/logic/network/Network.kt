package com.hfut.schedule.logic.network

import com.google.gson.JsonObject
import com.hfut.schedule.logic.network.Servicecreator.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.Servicecreator.GetCookieServiceCreator
import com.hfut.schedule.logic.network.Servicecreator.JxglstuServiceCreator
import com.hfut.schedule.logic.network.Servicecreator.LoginServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    suspend fun getDatum(cookie: String) = Jxglstu.getStudentId(cookie).await()
    suspend fun getDatum2(Cookie : String, bizTypeId : String, dataId : String) = Jxglstu.getLessonIds(Cookie, bizTypeId, dataId).await()
    suspend fun getCourse(Cookie : String, json: JsonObject) = Jxglstu.getDatum(Cookie,json).await()


    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) { }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                    t.printStackTrace()
                }
            })
        }
    }


}