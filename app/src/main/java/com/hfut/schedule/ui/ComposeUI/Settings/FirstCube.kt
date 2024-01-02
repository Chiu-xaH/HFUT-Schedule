package com.hfut.schedule.ui.ComposeUI.Settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ui.ComposeUI.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.ComposeUI.Settings.Items.SettingsCubeItems
import com.hfut.schedule.ui.ComposeUI.Settings.Test.debug

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstCube( ) {
    MyAPIItem()
    Spacer(modifier = Modifier.height(5.dp))
    SettingsCubeItems()
    debug()

}