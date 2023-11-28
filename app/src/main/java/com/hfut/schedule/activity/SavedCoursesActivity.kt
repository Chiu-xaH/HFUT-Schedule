package com.hfut.schedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.ui.DynamicColor.DynamicColorViewModel
import com.hfut.schedule.ui.ComposeUI.NoNet.NoNetWork
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars
import com.hfut.schedule.ui.theme.DynamicColr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedCoursesActivity : ComponentActivity() {
    private val dynamicColorViewModel: DynamicColorViewModel by viewModels()
    private val vm by lazy { ViewModelProvider(this).get(JxglstuViewModel::class.java) }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val prefs = getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
            val dyswitch = prefs.getBoolean("dyswitch",true)
            var dynamicColorEnabled by remember { mutableStateOf(dyswitch) }
            val currentTheme by dynamicColorViewModel.currentTheme
            DynamicColr( context = applicationContext,
                currentTheme = currentTheme,
                dynamicColor = dynamicColorEnabled){
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   TransparentSystemBars()
                  //  Text(text = "首页\r\n首页1\r\n首页2\r\n首页3")
                        NoNetWork(vm,dynamicColorEnabled = dynamicColorEnabled, dynamicColorViewModel = dynamicColorViewModel,
                            onChangeDynamicColorEnabled = { dynamicColorEnabledch -> dynamicColorEnabled = dynamicColorEnabledch })
                    //BottomSheetDemo()
                }
            }
        }
    }





}

