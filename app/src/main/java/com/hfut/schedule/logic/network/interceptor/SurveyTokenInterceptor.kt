package com.hfut.schedule.logic.network.interceptor

import android.util.Log
import com.hfut.schedule.logic.util.storage.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response

class SurveyTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cookies = response.headers("Set-Cookie")
        var result = ""

        // 遍历cookies列表，找到LOGIN_FLAVORING
        for (cookie in cookies) {
            if (cookie.contains("T_std_lesson_survey_form")) {
                // 提取出LOGIN_FLAVORING的值
                result = cookie.substringBefore(";")
                break
            }
        }

        SharedPrefs.saveString("SurveyCookie", cookies.toString())
        return response
    }
}