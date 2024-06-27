package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.login.main.LoginUI
import com.hfut.schedule.ui.Activity.success.main.saved.NoNetWork
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    private val startAcitivity = prefs.getBoolean("SWITCHFASTSTART",prefs.getString("TOKEN","")?.isNotEmpty() ?: false)
    private val vm by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    private val vm2 by lazy { ViewModelProvider(this).get(LoginSuccessViewModel::class.java) }
    private val vmUI by lazy { ViewModelProvider(this).get(UIViewModel::class.java) }
    private val viewModel: MainViewModel by viewModels()
    private val switchColor= prefs.getBoolean("SWITCHCOLOR",true)
    val switch_server = SharePrefs.prefs.getBoolean("SWITCHSERVER", true)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            if(!switchColor) {
                肥工课程表Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TransparentSystemBars()
                        if(startAcitivity && intent.getBooleanExtra("nologin",true)) NoNetWork(vm2,vm,vmUI)
                        else LoginUI(vm)
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
                            if(startAcitivity && intent.getBooleanExtra("nologin",true)) NoNetWork(vm2,vm,vmUI)
                            else LoginUI(vm)
                        }
                    }
                }
            }
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_CALENDAR),1)

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALENDAR),1)

            lifecycleScope.launch {
                if(!(startAcitivity && intent.getBooleanExtra("nologin",true))) {
                    launch { vm.getCookie() }
                    launch { SharePrefs.Save("tip","0") }
                    launch {  vm.getKey() }
                }
                launch { vmUI.getUpdate() }
                launch { vm.My() }
                if(switch_server) {
                    launch { vm2.getData() }
                }
            }

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0]  == PackageManager.PERMISSION_GRANTED) {

                }else Toast.makeText(this,"拒绝权限后添加日程将不可用",Toast.LENGTH_SHORT).show()
            }
        }
    }

}




