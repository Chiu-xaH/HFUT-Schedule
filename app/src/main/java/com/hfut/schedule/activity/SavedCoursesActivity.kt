package com.hfut.schedule.activity

import android.annotation.SuppressLint
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
import com.hfut.schedule.activity.ui.theme.肥工课程表Theme
import com.hfut.schedule.ui.ComposeUI.NoNet
import com.hfut.schedule.ui.ComposeUI.TransparentSystemBars


class SavedCoursesActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            肥工课程表Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   TransparentSystemBars()
                  //  Text(text = "首页\r\n首页1\r\n首页2\r\n首页3")
                         NoNet()
                    //BottomSheetDemo()
                }
            }
        }
    }





}

