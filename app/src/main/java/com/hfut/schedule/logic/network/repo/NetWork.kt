package com.hfut.schedule.logic.network.repo

import androidx.lifecycle.MutableLiveData
import com.hfut.schedule.logic.network.servicecreator.CommunitySreviceCreator
import com.hfut.schedule.logic.network.servicecreator.DormitoryScoreServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.servicecreator.GuaGuaServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuHTMLServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Jxglstu.JxglstuSurveyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.LePaoYunServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebServiceCreator
import com.hfut.schedule.logic.network.servicecreator.Login.LoginWebVpnServiceCreator
import com.hfut.schedule.logic.network.servicecreator.MyServiceCreator
import com.hfut.schedule.logic.network.servicecreator.NewsServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneGotoServiceCreator
import com.hfut.schedule.logic.network.servicecreator.OneServiceCreator
import com.hfut.schedule.logic.network.servicecreator.SearchEleServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ServerServiceCreator
import com.hfut.schedule.logic.network.servicecreator.XuanChengServiceCreator
import com.hfut.schedule.logic.network.servicecreator.ZJGDBillServiceCreator
import com.hfut.schedule.logic.network.api.CommunityService
import com.hfut.schedule.logic.network.api.DormitoryScore
import com.hfut.schedule.logic.network.api.FWDTService
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.GuaGuaService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LePaoYunService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.api.MyService
import com.hfut.schedule.logic.network.api.NewsService
import com.hfut.schedule.logic.network.api.OneService
import com.hfut.schedule.logic.network.api.ServerService
import com.hfut.schedule.logic.network.api.WebVpnService
import com.hfut.schedule.logic.network.api.XuanChengService
import com.hfut.schedule.logic.network.api.ZJGDBillService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



object NetWork {
    //引入接口
    // 通用的网络请求方法，支持自定义的操作
    @JvmStatic
    fun <T> makeRequest(
        call: Call<ResponseBody>,
        liveData: MutableLiveData<T>? = null,
        onSuccess: ((Response<ResponseBody>) -> Unit)? = null
    ) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && liveData != null) {
                    val responseBody = response.body()?.string()
                    val result: T? = parseResponse(responseBody)
                    liveData.value = result
                }

                // 执行自定义操作
                onSuccess?.invoke(response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // 通用方法用于解析响应（根据需要进行调整）
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    private fun <T> parseResponse(responseBody: String?): T? {
        return responseBody as? T
    }
}