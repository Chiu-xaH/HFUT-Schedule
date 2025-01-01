package com.hfut.schedule.activity.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs.saveString
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.main.login.SuccessUI
import com.hfut.schedule.viewmodel.LoginSuccessViewModelFactory
import com.hfut.schedule.viewmodel.NetWorkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSuccessActivity : BaseActivity() {

    var webVpn = false
    val networkVms by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(webVpn)).get(NetWorkViewModel::class.java) }
    //var webVpn = false
    @Composable
    override fun UI() {
        intent.getStringExtra("Grade")?.let {
            SuccessUI(
                networkVms,
                it,
                super.loginVm,
                super.uiVm,
                webVpn
            )
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webVpn = intent.getBooleanExtra("webVpn",false)
        lifecycleScope.apply {
            if(!webVpn) {
                launch {
                    val cookie = prefs.getString("redirect", "")
                    networkVms.Jxglstulogin(cookie!!)
                }
            }
            launch {
                val semesterId = Gson().fromJson(prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).semesterId
                saveString("semesterId",semesterId)
            }
        }
    }
}





