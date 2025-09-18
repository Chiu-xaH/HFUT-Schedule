package com.hfut.schedule.logic.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header

interface LibraryService {
    fun checkLogin(
        @Header("authorization") auth : String
    ) : Call<ResponseBody>
}