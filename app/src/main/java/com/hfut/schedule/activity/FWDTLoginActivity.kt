package com.hfut.schedule.activity

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
import com.hfut.schedule.ViewModel.MainViewModel
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars
import com.hfut.schedule.ui.theme.DynamicColr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FWDTLoginActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            var dynamicColorEnabled by remember { mutableStateOf(true) }
            val currentTheme by mainViewModel.currentTheme
            DynamicColr( context = applicationContext,
                currentTheme = currentTheme,
                dynamicColor = dynamicColorEnabled){
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()

                }
            }
        }
    }
}

