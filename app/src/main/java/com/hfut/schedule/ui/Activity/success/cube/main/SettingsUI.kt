package com.hfut.schedule.ui.Activity.success.cube.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.SettingsCubeItems
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.SettingsItem

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,innerPaddings : PaddingValues,
                   blur : Boolean,
                   blurchanged : (Boolean) -> Unit
) {


        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize().padding(innerPaddings)) {

            Spacer(modifier = Modifier.height(10.dp))

            MyAPIItem()

            SettingsItem(vm,showlable,showlablechanged,ifSaved,blur,blurchanged)

            SettingsCubeItems()

            Spacer(modifier = Modifier.height(90.dp))
        }

}