package com.hfut.schedule.activity.util

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.ColorMode
import com.hfut.schedule.ui.theme.AppTheme
import com.hfut.schedule.ui.theme.DefaultAppTheme
import com.hfut.schedule.ui.theme.setNightMode
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseActivity : ComponentActivity() {
    val networkVm by lazy { ViewModelProvider(this)[NetWorkViewModel::class.java] }
    val loginVm by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    val showerVm by lazy { ViewModelProvider(this)[GuaGuaViewModel::class.java] }
    val uiVm by lazy { ViewModelProvider(this)[UIViewModel::class.java] }

    @Composable
    abstract fun UI()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        lifecycleScope.launch {
//            val currentColorModeIndex = DataStoreManager.colorMode.first()
//            when(currentColorModeIndex) {
//                ColorMode.DARK.code -> {
//                    setNightMode(this@BaseActivity,ColorMode.DARK)
//                }
//                ColorMode.LIGHT.code -> {
//                    setNightMode(this@BaseActivity,ColorMode.LIGHT)
//                }
//                ColorMode.AUTO.code  -> {
//                    setNightMode(this@BaseActivity, ColorMode.AUTO)
//                }
//            }
//        }
        setContent {
            AppTheme {
                UI()
            }
        }
    }
}