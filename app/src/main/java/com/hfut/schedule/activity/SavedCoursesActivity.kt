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
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginSuccessViewModelFactory
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.main.saved.NoNetWork
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedCoursesActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(false)).get(LoginSuccessViewModel::class.java) }
    private val viewModel: MainViewModel by viewModels()
    private val vm2 by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val vmUI by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }
    val switchColor= prefs.getBoolean("SWITCHCOLOR",true)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            if(switchColor) {
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
                            //  Text(text = "首页\r\n首页1\r\n首页2\r\n首页3")
                            NoNetWork(vm,vm2,vmUI)
                            //BottomSheetDemo()
                        }
                    }
                }
            } else {
                肥工课程表Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        //  Text(text = "首页\r\n首页1\r\n首页2\r\n首页3")
                        NoNetWork(vm,vm2,vmUI)
                        //BottomSheetDemo()
                    }
                }
            }
        }
    }
}

