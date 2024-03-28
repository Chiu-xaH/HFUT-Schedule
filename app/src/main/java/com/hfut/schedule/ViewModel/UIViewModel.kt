package com.hfut.schedule.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.logic.datamodel.Update
import com.hfut.schedule.logic.datamodel.zjgd.ReturnCard
import com.hfut.schedule.logic.network.ServiceCreator.GiteeServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.api.GiteeService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UIViewModel : ViewModel()  {
    private val Gitee = GiteeServiceCreator.create(GiteeService::class.java)
    val findNewCourse = MutableLiveData<Boolean>()
    var CardValue = MutableLiveData<ReturnCard>()
    var CardAuth = MutableLiveData<String?>(prefs.getString("auth",""))

   // val UpdateValue = MutableLiveData<String?>()
    fun getUpdate() {

        val call = Gitee.getUpdate()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
              //  UpdateValue.value = response.body()?.string()
                Save("versions",response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }
}