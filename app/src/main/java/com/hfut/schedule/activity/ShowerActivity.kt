package com.hfut.schedule.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginSuccessViewModelFactory
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.shower.ShowerGuaGua
import com.hfut.schedule.ui.Activity.shower.ShowerLogin
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ui.theme.MonetColor

class ShowerActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val vm2 by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(false)).get(
        LoginSuccessViewModel::class.java) }

    private val vmUI by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }
    private val viewModel: MainViewModel by viewModels()
    private val switchColor= SharePrefs.prefs.getBoolean("SWITCHCOLOR",true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(!switchColor) {
                肥工课程表Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        if(intent.getBooleanExtra("nologin",true)) ShowerGuaGua()
                        else ShowerLogin(vm)
                    }
                }
            } else {
                SettingsProvider {
                    // 更新主题色
                    val stickerUuid = LocalCurrentStickerUuid.current
                    LaunchedEffect(stickerUuid) {
                        viewModel.sendUiIntent(MainIntent.UpdateThemeColor(stickerUuid))
                    }
                    MonetColor {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            TransparentSystemBars()
                            if(intent.getBooleanExtra("nologin",true)) ShowerGuaGua()
                            else ShowerLogin(vm)
                        }
                    }
                }
            }
        }
    }
}

