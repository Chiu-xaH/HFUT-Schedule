package com.hfut.schedule.activity.util

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ui.theme.AppTheme
import com.hfut.schedule.ui.theme.DefaultAppTheme
import com.hfut.schedule.viewmodel.network.GuaGuaViewModel
import com.hfut.schedule.viewmodel.network.LoginViewModel
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.network.NetworkViewModelFactory
import com.hfut.schedule.viewmodel.ui.UIViewModel

abstract class BaseActivity : ComponentActivity() {
    val loginVm by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }
    open val networkVm by lazy { ViewModelProvider(
        this,
        NetworkViewModelFactory(false)
    )[NetWorkViewModel::class.java] }
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
        setContent {
            AppTheme {
                UI()
            }
        }
    }
}