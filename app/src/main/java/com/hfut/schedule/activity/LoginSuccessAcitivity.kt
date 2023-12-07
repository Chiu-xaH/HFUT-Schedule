package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Activity.SuccessUI
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginSuccessAcitivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val vm by lazy { ViewModelProvider(this).get(LoginSuccessViewModel::class.java) }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val grade = intent.getStringExtra("Grade")
        setContent {
            SettingsProvider {
                val stickerUuid = LocalCurrentStickerUuid.current
                LaunchedEffect(stickerUuid) { viewModel.sendUiIntent(MainIntent.UpdateThemeColor(stickerUuid)) }
                MonetColor {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        grade?.let { SuccessUI(vm, it) }
                    }
                }
            }
        }
            val cookie = prefs.getString("redirect", "")
            vm.Jxglstulogin(cookie!!)
    }
}





