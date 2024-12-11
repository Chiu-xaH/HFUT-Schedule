package com.hfut.schedule.activity.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs.saveString
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.main.login.SuccessUI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSuccessActivity : BaseActivity() {

    //var webVpn = false
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    override fun UI() {
        intent.getStringExtra("Grade")?.let {
            SuccessUI(
                super.networkVm,it,
                super.loginVm,
                super.uiVm,
                intent.getBooleanExtra("webVpn",false)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.apply {
            launch {
                val cookie = prefs.getString("redirect", "")
                super.networkVm.Jxglstulogin(cookie!!)
            }

            launch {
                val semesterId = Gson().fromJson(prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).semesterId
                saveString("semesterId",semesterId)
            }
        }
    }
}





