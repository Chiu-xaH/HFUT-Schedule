package com.hfut.schedule

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

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

