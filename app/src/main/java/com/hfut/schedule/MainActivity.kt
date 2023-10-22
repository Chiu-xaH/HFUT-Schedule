package com.hfut.schedule

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ui.vm.LoginViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : ComponentActivity() {
    val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    fun getCookie(url: String) {
        //创建OkHttpClient对象
        val okHttpClient = OkHttpClient()
        //创建Request对象
        val request = Request.Builder()
            .url(url) //设置请求的url
            .get() //设置请求方法为GET
            .build()
        //发送请求并获取响应
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //请求失败时的处理
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                //请求成功时的处理
                if (response.isSuccessful) {
                    //从响应头中获取Cookie字段
                    val cookie = response.header("Cookies")
                    //打印或处理Cookie

                    cookie?.let { Log.d("成功1", it) }
                } else {
                    Log.d("失败", "SS")
                }
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val AccountET : EditText = findViewById(R.id.AccountET)
        val PasswordET : EditText = findViewById(R.id.PasswordET)
        val LoginButton : Button = findViewById(R.id.LoginButton)


        val AESinput = PasswordET.editableText.toString()
        val username = AccountET.editableText.toString()
        LoginButton.setOnClickListener {

            //开启协程同时进行加密转换和传入转换后的数据
            //val cookies : String = "0"
            //val AESoutput = AESEncrypt.encrypt(AESinput,cookies)

            vm.login(username,AESinput)

            //vm.getCookie()

            //传入数据
        }



                 //获取Cookies并作为password




        }

}

