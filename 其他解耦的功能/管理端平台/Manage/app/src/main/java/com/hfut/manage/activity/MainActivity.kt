package com.hfut.manage.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hfut.manage.ui.MainUI
import com.hfut.manage.ui.utils.TransparentSystemBars
import com.hfut.manage.ui.theme.聚在工大管理端Theme
import com.hfut.manage.viewmodel.NetViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val vm by lazy { ViewModelProvider(this)[NetViewModel::class.java] }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            聚在工大管理端Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransparentSystemBars()
                    MainUI(vm)
                }
            }
        }
        lifecycleScope.launch {
            launch { vm.getURL() }
          //  launch { vm.getData() }
        }
    }
}