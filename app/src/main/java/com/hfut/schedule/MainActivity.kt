package com.hfut.schedule

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ui.vm.LoginViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MainActivity : ComponentActivity() {
    val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
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
            val cookies : String = "0"
            //val AESoutput = AESEncrypt.encrypt(AESinput,cookies)
            vm.login(username,AESinput)
            //传入数据
        }


                 //获取Cookies并作为password




        }

}

