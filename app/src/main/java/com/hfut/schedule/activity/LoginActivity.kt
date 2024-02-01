package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ui.UIUtils.TransparentSystemBars
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Activity.LoginUI
import com.hfut.schedule.ui.MonetColor.LocalCurrentStickerUuid
import com.hfut.schedule.ui.MonetColor.MainIntent
import com.hfut.schedule.ui.MonetColor.MainViewModel
import com.hfut.schedule.ui.MonetColor.SettingsProvider
import com.hfut.schedule.ui.theme.MonetColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
   // private val vm2 by lazy { ViewModelProvider(this).get(LoginSuccessViewModel::class.java) }
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
        if(prefs.getBoolean("SWITCHFASTSTART",false) && intent.getBooleanExtra("nologin",true)) {
            val it = Intent(this,SavedCoursesActivity::class.java)
            startActivity(it)
        }
       // val CommuityTOKEN = SharePrefs.prefs.getString("TOKEN","")
        lifecycleScope.launch {
            launch { vm.getCookie() }
            launch {  vm.getKey() }
            launch { vm.My() }
            //launch {
              //  async { CommuityTOKEN?.let { vm2.Exam(it) }  }.await()
                ///async {
                   // Handler(Looper.getMainLooper()).post{
                     //   vm2.ExamData.observeForever { result ->
                            // Log.d("r",result)
                       //     if (result != null) {
                         //       if(result.contains("成功")) {
                           //         CoroutineScope(Job()).launch {
                             //           val it = Intent(MyApplication.context, SavedCoursesActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                               //         MyApplication.context.startActivity(it)
                                 //   }
                               // }
                            //}
                        //}
                    //}
                //}
            ///}
        }
    }
}

