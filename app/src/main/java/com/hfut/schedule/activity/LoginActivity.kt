package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.MyApplication
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.SharePrefs
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Activity.LoginUI
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val viewModel: MainViewModel by viewModels()
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
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
                        LoginUI(vm)
                    }
                }
            }
        }

        lifecycleScope.apply {
            launch { vm.getCookie() }
            launch {  vm.getKey() }
            launch { vm.My() }
        }//协程并行执行，提高效率

        }
}

