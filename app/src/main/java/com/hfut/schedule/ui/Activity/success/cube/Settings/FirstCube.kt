package com.hfut.schedule.ui.Activity.success.cube.Settings

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.SettingsCubeItems
import com.hfut.schedule.ui.Activity.success.cube.Settings.Test.debug
import dev.chrisbanes.haze.HazeState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstCube() {
    MyAPIItem()
    Spacer(modifier = Modifier.height(5.dp))
    SettingsCubeItems()
    debug()

}