package com.hfut.schedule.ViewModel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.network.ServiceCreator.FWDTServiceCreator
import com.hfut.schedule.logic.network.api.FWDTService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FWDTViewMoodel : ViewModel() {
    private val api = FWDTServiceCreator.create(FWDTService::class.java)

    fun login(sno : String,psk : String,code : String) {
        val call = api.loginFWDT(sno,psk,code,"1","1s",true)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}