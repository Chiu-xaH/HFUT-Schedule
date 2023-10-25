package com.hfut.schedule.Activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.logic.AESEncrypt
import com.hfut.schedule.R
import com.hfut.schedule.ui.ViewModel.LoginViewModel

class LoginActivity : ComponentActivity() {
    val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        val AccountET: EditText = findViewById(R.id.AccountET)
        val PasswordET: EditText = findViewById(R.id.PasswordET)
        val LoginButton: Button = findViewById(R.id.LoginButton)



        //得到AESKey
        vm.getKey()
        Thread.sleep(3000)
        val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
        val key = prefs.getString("cookie", "")

        key?.let { Log.d("传送", it) }

            LoginButton.setOnClickListener {
                val AESinput = PasswordET.editableText.toString()
                val username = AccountET.editableText.toString()
                val AESoutput = AESEncrypt.encrypt(AESinput, key!!)
                Log.d("测试s",AESinput)
                Log.d("密钥",key)
                Log.d("加密后",AESoutput)
                Log.d("加密和",username)
                vm.login(username,AESoutput)
            }
        }
    }


