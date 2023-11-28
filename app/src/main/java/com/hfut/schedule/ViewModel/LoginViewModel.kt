package com.hfut.schedule.ViewModel

import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfut.schedule.MyApplication
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.MyServiceCreator
import com.hfut.schedule.logic.network.api.MyService
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var sessionLiveData = MutableLiveData<String>()
    var cookie2 = MutableLiveData<String>()
    var code = MutableLiveData<String>()
    var location = MutableLiveData<String>()
    var execution = MutableLiveData<String>()
   // var ticket = MutableLiveData<String>()


    private val api = LoginServiceCreator.create(LoginService::class.java)
    private val api2 = GetCookieServiceCreator.create(LoginService::class.java)
    private val api3 = GetAESKeyServiceCreator.create(LoginService::class.java)
    private val api4 = MyServiceCreator.create(MyService::class.java)


    fun login(username : String,password : String,keys : String)  {// 创建一个Call对象，用于发送异步请求

        val cookies : String = sessionLiveData.value  + cookie2.value +";" + keys
         val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
        if (sp.getString("ONE", "") != cookies) {
            sp.edit().putString("ONE", cookies).apply()
        }

        val call = api.login(cookies,username, password, execution.value!!,"submit")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {


                    location.value = response.headers()["Location"].toString()
                val TGC = response.headers()["Set-Cookie"].toString().substringBefore(";")
              //  Log.d("C", TGC)
                    code.value = response.code().toString()
                    //Log.d("失败",code.value.toString())
                    val ticket = response.headers()["Location"].toString().substringAfter("=")
                    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                    if (sp.getString("ticket", "") != ticket) {
                        sp.edit().putString("ticket", ticket).apply()
                    }
                if (sp.getString("TGC", "") != TGC) {
                    sp.edit().putString("TGC", TGC).apply()
                }

                   // Log.d("成功", ticket.value!!)


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
              //   Log.d("VM","失败")
                code.value = "XXX"
                t.printStackTrace()
            }
            })


    }

    fun getKey() {


        val call = api3.getKey()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if(response.isSuccessful()){
                    cookie2.value  = response.headers()["Set-Cookie"].toString()
                //    Log.d("测试","成功")

                }
                else Log.d("测试","失败，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                code.value = "XXX"
             //  Log.d("测试","失")
                t.printStackTrace()
            }
        })
    }

    fun getCookie() {

        val call = api2.getCookie()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

               // response.body()?.let { Log.d("主体", it.string()) }

                val doc = Jsoup.parse( response.body()?.string())
                execution.value = doc.select("input[name=execution]").first()?.attr("value")
               // execution.value?.let { Log.d("赵思涵", it) }

                if(response.isSuccessful()) {
                    sessionLiveData.value  = response.headers()["Set-Cookie"].toString().substringBefore(";").plus(";")
                  //  Log.d("成功","getKry")
              //      Log.d("getCookie","成功")

                }
                else Log.d("失败","getKey，${response.code()},${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            //    Log.d("测试q","失")
                code.value = "XXX"
                t.printStackTrace()
            }
        })
    }


    fun My() {
        val call = api4.my()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                if (sp.getString("my", "") !=body ) {
                    sp.edit().putString("my", body).apply()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }





    }



