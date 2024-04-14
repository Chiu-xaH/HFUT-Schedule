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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.MyAPIItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.PersonPart
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.PartTwo
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.PartOne
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.UpdateItem
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.UpdateUI
import com.hfut.schedule.ui.Activity.success.cube.Settings.getUpdates

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   ifSaved : Boolean,
                   innerPaddings : PaddingValues,
                   blur : Boolean,
                   blurchanged : (Boolean) -> Unit
) {


        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(innerPaddings)) {

            Spacer(modifier = Modifier.height(5.dp))

            Text(text = "登录信息", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))
            PersonPart()

            Text(text = "API接口", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))
            MyAPIItem()

            if (APPVersion.getVersionName() != getUpdates().version) {
                Text(text = "更新", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))
                UpdateUI()
            }

            Text(text = "应用设置", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp))
            PartOne(vm,showlable,showlablechanged,ifSaved,blur,blurchanged)
            PartTwo()

            Spacer(modifier = Modifier
                .height(innerPaddings.calculateBottomPadding())
                .height(5.dp))
        }

}