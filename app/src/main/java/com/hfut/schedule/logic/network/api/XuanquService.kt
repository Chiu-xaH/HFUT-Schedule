package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface XuanquService {
    @FormUrlEncoded
    @POST ("query/getStudentScore")
    fun SearchXuanqu(@Field ("student_code") code : String) : Call<ResponseBody>
}