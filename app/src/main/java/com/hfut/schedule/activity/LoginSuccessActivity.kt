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
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.ui.Activity.success.main.login.SuccessUI
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSuccessActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val vm by lazy { ViewModelProvider(this).get(LoginSuccessViewModel::class.java) }
    private val vm2 by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val switchColor= prefs.getBoolean("SWITCHCOLOR",true)
    private val vmUI by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val grade = intent.getStringExtra("Grade")
        setContent {
            if(switchColor) {
                SettingsProvider {
                    val stickerUuid = LocalCurrentStickerUuid.current
                    LaunchedEffect(stickerUuid) { viewModel.sendUiIntent(MainIntent.UpdateThemeColor(stickerUuid)) }
                    MonetColor {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            TransparentSystemBars()
                            grade?.let { SuccessUI(vm, it,vm2,vmUI) }
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
                        grade?.let { SuccessUI(vm, it,vm2,vmUI) }
                    }
                }
            }
        }
        lifecycleScope.apply{
            launch {
                val cookie = prefs.getString("redirect", "")
                vm.Jxglstulogin(cookie!!)
            }
            launch {
                val semesterId = Gson().fromJson(prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).semesterId
                if(semesterId != null) Save("semesterId",semesterId)
                else  Save("semesterId","254")
            }
        }
    }
}





