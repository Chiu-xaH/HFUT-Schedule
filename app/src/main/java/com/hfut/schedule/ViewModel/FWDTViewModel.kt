package com.hfut.schedule.ViewModel

import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.ServiceCreator.FWDTServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.api.FuwuDaTingService
import com.hfut.schedule.logic.network.api.JxglstuService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FWDTViewModel : ViewModel(){
    private val api = FWDTServiceCreator.create(FuwuDaTingService::class.java)

    fun getValiCode() {

        val call = api.getValiCode()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {}

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}