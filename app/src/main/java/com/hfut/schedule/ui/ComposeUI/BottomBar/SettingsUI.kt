package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.LibraryItem
import com.hfut.schedule.ui.ComposeUI.Search.Xuanqu.XuanquItem
import com.hfut.schedule.ui.ComposeUI.Settings.MyAPIItem
import com.hfut.schedule.ui.ComposeUI.Settings.MonetColorItem
import com.hfut.schedule.ui.ComposeUI.Settings.SettingsItems
import com.hfut.schedule.ui.ComposeUI.Search.WebUI

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit, ) {
    val switch_card = prefs.getBoolean("SWITCHCARD",true)
    var showcard by remember { mutableStateOf(switch_card) }
    val switch_api = prefs.getBoolean("SWITCHMYAPI",true)
    var showapi by remember { mutableStateOf(switch_api) }

    val sp =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCH", true) != showlable) { sp.edit().putBoolean("SWITCH", showlable).apply() }
    if (sp.getBoolean("SWITCHCARD", true) != showcard) { sp.edit().putBoolean("SWITCHCARD", showcard).apply() }
    if (sp.getBoolean("SWITCHMYAPI", true) != showapi) { sp.edit().putBoolean("SWITCHMYAPI", showapi).apply() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("肥工教务通") }
            )
        },) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()) {
            Spacer(modifier = Modifier.height(10.dp))

            MyAPIItem()

            ListItem(
                headlineContent = { Text(text = "显示标签") },
                leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) }
            )

            ListItem(
                headlineContent = { Text(text = "聚焦显示一卡通") },
                leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showcard, onCheckedChange = {showcardch -> showcard = showcardch}) }
            )

            ListItem(
                headlineContent = { Text(text = "开发者接口") },
                supportingContent = { Text(text = "本接口提供了除学校系统之外的信息")},
                leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
                trailingContent = { Switch(
                    checked = showapi,
                    onCheckedChange = {showapich ->
                        showapi = showapich
                        Toast.makeText(MyApplication.context,"重启APP后会应用此更改",Toast.LENGTH_SHORT).show()
                    }) }
            )

            MonetColorItem()

            SettingsItems()

            Spacer(modifier = Modifier.height(90.dp))

        }
    }
}