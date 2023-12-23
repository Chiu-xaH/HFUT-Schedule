package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
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
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.LePaoYun.InfoSet
import com.hfut.schedule.ui.ComposeUI.Search.LibraryItem
import com.hfut.schedule.ui.ComposeUI.Search.Xuanqu.XuanquItem
import com.hfut.schedule.ui.ComposeUI.Settings.MyAPIItem
import com.hfut.schedule.ui.ComposeUI.Settings.MonetColorItem
import com.hfut.schedule.ui.ComposeUI.Settings.SettingsItems
import com.hfut.schedule.ui.ComposeUI.Search.WebUI

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : LoginSuccessViewModel
                   ,showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit, ) {
    val switch_card = prefs.getBoolean("SWITCHCARD",true)
    var showcard by remember { mutableStateOf(switch_card) }
    val switch_api = prefs.getBoolean("SWITCHMYAPI",true)
    var showapi by remember { mutableStateOf(switch_api) }
    val switch_beta = prefs.getBoolean("SWITCHBETA",true)
    var showbeta by remember { mutableStateOf(switch_beta) }
    val switch_focus = prefs.getBoolean("SWITCHFOCUS",false)
    var showfocus by remember { mutableStateOf(switch_focus) }


    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCH", true) != showlable) { sp.edit().putBoolean("SWITCH", showlable).apply() }
    if (sp.getBoolean("SWITCHCARD", true) != showcard) { sp.edit().putBoolean("SWITCHCARD", showcard).apply() }
    if (sp.getBoolean("SWITCHMYAPI", true) != showapi) { sp.edit().putBoolean("SWITCHMYAPI", showapi).apply() }
    if (sp.getBoolean("SWITCHBETA", false) != showbeta) { sp.edit().putBoolean("SWITCHBETA", showbeta).apply() }
    if (sp.getBoolean("SWITCHFOCUS", false) != showfocus) { sp.edit().putBoolean("SWITCHFOCUS", showfocus).apply() }

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
                headlineContent = { Text(text = "底栏标签") },
                leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
                modifier = Modifier.clickable { showlablechanged }
            )

            ListItem(
                headlineContent = { Text(text = "聚焦显示一卡通") },
                leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showcard, onCheckedChange = { showcardch -> showcard = showcardch }) },
                modifier = Modifier.clickable { showcard = !showcard }
            )

            ListItem(
                headlineContent = { Text(text = "聚焦接口") },
                supportingContent = { Text(text = "本接口提供了除学校系统之外的聚焦信息,推荐23地科专业打开")},
                leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
                modifier = Modifier.clickable { showapi = !showapi }
            )

            ListItem(
                headlineContent = { Text(text = "聚焦优先") },
                supportingContent = { Text(text = "使聚焦作为本地速览的第一页面,而不是课表")},
                leadingContent = { Icon(painterResource(R.drawable.lightbulb), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showfocus, onCheckedChange = {showfocusch -> showfocus = showfocusch }) },
                modifier = Modifier.clickable { showfocus = !showfocus }
            )





            var showBottomSheet_input by remember { mutableStateOf(false) }
            val sheetState_input = rememberModalBottomSheetState()
            if (showBottomSheet_input) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet_input = false },
                    sheetState = sheetState_input
                ) {
                    InfoSet()
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            ListItem(
                headlineContent = { Text(text = "Beta 功能") },
                leadingContent = { Icon(painterResource(id = R.drawable.hotel_class), contentDescription = "Localized description") },
                supportingContent = { Text(text = "打开此开关后,会显示一些Beta功能及未开发完成的功能")},
                trailingContent = { Switch(checked = showbeta, onCheckedChange = {showbetach -> showbeta = showbetach }) },
                modifier = Modifier.clickable { showbeta = !showbeta }
            )

            ListItem(
                headlineContent = { Text(text = "云运动 信息配置") },
                supportingContent = { Text(text = "需要提交已登录手机的信息")},
                leadingContent = { Icon(painterResource(R.drawable.mode_of_travel), contentDescription = "Localized description",) },
                modifier = Modifier.clickable { showBottomSheet_input = true }
            )


            // ListItem(
             //   headlineContent = { Text(text = "聚焦信息 添加") },
               // leadingContent = { Icon(painterResource(R.drawable.add_circle), contentDescription = "Localized description",) },
              //  modifier = Modifier.clickable {  }
           // )

            SettingsItems()


            Spacer(modifier = Modifier.height(90.dp))

        }
    }
}