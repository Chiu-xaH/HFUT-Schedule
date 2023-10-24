package com.hfut.schedule

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.logic.network.OkHttp.PersistenceCookieJar
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

        vm.getKey()

        val key = PersistenceCookieJar().get()

        Log.d("传送",key)

        LoginButton.setOnClickListener {
            val AESoutput = AESEncrypt.encrypt(AESinput,key)
            vm.login(username,AESoutput)
        }
        }
}

