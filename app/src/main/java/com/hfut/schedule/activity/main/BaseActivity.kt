package com.hfut.schedule.activity.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.viewmodel.GuaGuaViewModel
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.LoginSuccessViewModelFactory
import com.hfut.schedule.viewmodel.LoginViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.utils.monet.LocalCurrentStickerUuid
import com.hfut.schedule.ui.utils.monet.MainIntent
import com.hfut.schedule.ui.utils.monet.MainViewModel
import com.hfut.schedule.ui.utils.monet.SettingsProvider
import com.hfut.schedule.ui.utils.TransparentSystemBars
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : ComponentActivity() {
    private val switchColor= SharePrefs.prefs.getBoolean("SWITCHCOLOR",true)
    private val monetVm: MainViewModel by viewModels()
    val loginVm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    open val networkVm by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(false)).get(NetWorkViewModel::class.java) }
    val showerVm by lazy { ViewModelProvider(this).get(GuaGuaViewModel::class.java) }
    val uiVm by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }

    val animation = SharePrefs.prefs.getInt("ANIMATION", MyApplication.Animation)
    val switchblur = SharePrefs.prefs.getBoolean("SWITCHBLUR",  AndroidVersion.canBlur)


    @Composable
    open fun UI() {
//        open val blur by remember { mutableStateOf(switchblur) }
//        val hazeState = remember { HazeState() }
//        val navController = rememberNavController()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            if(!switchColor) {
                肥工课程表Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        UI()
                    }
                }
            } else {
                SettingsProvider {
                    // 更新主题色
                    val stickerUuid = LocalCurrentStickerUuid.current
                    LaunchedEffect(stickerUuid) {
                        monetVm.sendUiIntent(MainIntent.UpdateThemeColor(stickerUuid))
                    }
                    MonetColor {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            TransparentSystemBars()
                            UI()
                        }
                    }
                }
            }
        }
    }
}
