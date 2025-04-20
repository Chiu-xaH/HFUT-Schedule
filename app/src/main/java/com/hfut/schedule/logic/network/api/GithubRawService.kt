package com.hfut.schedule.logic.network.api

import com.hfut.schedule.logic.util.sys.DateTimeUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubRawService {
    // 获取某年的节假日
    @GET("Chiu-xaH/holiday-cn/master/{year}.json")
    fun getYearHoliday(
        @Path("year") year : String = DateTimeUtils.Date_yyyy
    ) : Call<ResponseBody>
}