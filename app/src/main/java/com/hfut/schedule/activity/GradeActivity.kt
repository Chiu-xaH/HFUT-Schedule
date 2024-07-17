package com.hfut.schedule.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginSuccessViewModelFactory
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.Activity.card.main.CardUI
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.Grade
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.GradeItemUI
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.GradeItemUIJXGLSTU
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.GradeUI
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ui.theme.MonetColor

class GradeActivity : ComponentActivity() {
    private val switchColor= SharePrefs.prefs.getBoolean("SWITCHCOLOR",true)
    private val viewModel: MainViewModel by viewModels()
    var webVpn = false
    private val vm by lazy { ViewModelProvider(this, LoginSuccessViewModelFactory(webVpn)).get(LoginSuccessViewModel::class.java) }

    //private val vmUI by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val ifSaved = intent.getBooleanExtra("saved",true)
        webVpn = intent.getBooleanExtra("webVpn",false)
        setContent {
            if(!switchColor) {
                肥工课程表Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        GradeUI(ifSaved,vm)
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
                            GradeUI(ifSaved,vm)
                        }
                    }
                }
            }
        }
    }
}

