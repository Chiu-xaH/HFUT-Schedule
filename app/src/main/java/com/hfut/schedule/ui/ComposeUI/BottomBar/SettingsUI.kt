package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.LePaoYun.InfoSet
import com.hfut.schedule.ui.ComposeUI.Settings.MyAPIItem
import com.hfut.schedule.ui.ComposeUI.Settings.SettingsCubeItems
import com.hfut.schedule.ui.ComposeUI.Settings.FocusSetting
import com.hfut.schedule.ui.ComposeUI.Settings.SettingsItem

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit, ) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("肥工教务通 选项") }
            )
        },) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()) {

            Spacer(modifier = Modifier.height(10.dp))

            MyAPIItem()

            SettingsItem(vm,showlable,showlablechanged)

            SettingsCubeItems()

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}