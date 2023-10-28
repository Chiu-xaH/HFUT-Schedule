package com.hfut.schedule.logic.network.ServiceCreator

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.hfut.schedule.MyApplication
import com.hfut.schedule.activity.UIAcitivity
import com.hfut.schedule.ui.ViewModel.JxglstuViewModel
import com.hfut.schedule.ui.ViewModel.LoginViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object JxglstuServiceCreator {


    //const val URL = "http://jxglstu.hfut.edu.cn/"

   // val Client = OkHttpClient.Builder()
      //  .build()
   var baseURL : String = "//请修改此处！！"

    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
       // .client(Client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun <T> create(service: Class<T>): T = retrofit.create(service)

}