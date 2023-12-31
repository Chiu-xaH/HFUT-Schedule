package com.hfut.schedule.ui.ComposeUI.Settings.Items

import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.ComposeUI.Saved.ReconstructionUI
import com.hfut.schedule.ui.ComposeUI.Search.Electric.WebViewScreen
import com.hfut.schedule.ui.ComposeUI.Search.LePaoYun.InfoSet
import com.hfut.schedule.ui.UIUtils.MyToast


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(vm : LoginSuccessViewModel, showlable : Boolean, showlablechanged :(Boolean) -> Unit) {
    val switch_focus = SharePrefs.prefs.getBoolean("SWITCHFOCUS",true)
    var showfocus by remember { mutableStateOf(switch_focus) }


    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCH", true) != showlable) { sp.edit().putBoolean("SWITCH", showlable).apply() }
    if (sp.getBoolean("SWITCHFOCUS", true) != showfocus) { sp.edit().putBoolean("SWITCHFOCUS", showfocus).apply() }


    ListItem(
        headlineContent = { Text(text = "底栏标签") },
        leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
        trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) },
        modifier = Modifier.clickable { showlablechanged }
    )


    ListItem(
        headlineContent = { Text(text = "聚焦优先") },
        supportingContent = { Text(text = "使聚焦作为本地速览的第一页面,而不是课表") },
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

    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = { IconButton(onClick = { showDialog = false }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        }
                        },
                        title = { Text("DEBUG") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ReconstructionUI()
                }
            }
        }
    }

    var showBottomSheet_focus by remember { mutableStateOf(false) }
    var sheetState_focus = rememberModalBottomSheetState()
    if (showBottomSheet_focus) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_focus = false },
            sheetState = sheetState_focus
        ) {
            FocusSetting()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }


    var showBottomSheet_arrange by remember { mutableStateOf(false) }
    var sheetState_arrange = rememberModalBottomSheetState()
    if (showBottomSheet_arrange) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_arrange = false },
            sheetState = sheetState_arrange
        ) {
            RequestArrange()
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    ListItem(
        headlineContent = { Text(text = "云运动 信息配置") },
        supportingContent = { Text(text = "需要提交已登录手机的信息") },
        leadingContent = { Icon(painterResource(R.drawable.mode_of_travel), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_input = true }
    )

    ListItem(
        headlineContent = { Text(text = "聚焦编辑") },
        supportingContent = { Text(text = "自定义聚焦的内容及信息来源") },
        leadingContent = { Icon(painterResource(R.drawable.edit), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_focus = true }
    )

    ListItem(
        headlineContent = { Text(text = "请求范围") },
        supportingContent = { Text(text = "自定义加载一页时出现的数目,数目越大,加载时间相应地会更长,但可显示更多信息") },
        leadingContent = { Icon(painterResource(R.drawable.settings_ethernet), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {  showBottomSheet_arrange = true }
    )

    ListItem(
        headlineContent = { Text(text = "扩展课表布局") },
        supportingContent = { Text(text = "使课表方格由4*5扩展到4*7,甚至6*7,即使不扩展课表布局,无法显示出来的课程依旧会被解析,在聚焦中显示出来)") },
        leadingContent = { Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",) },
       // trailingContent = { Switch(checked = showfocus, onCheckedChange = {showfocusch -> showfocus = showfocusch }) },
        modifier = Modifier.clickable { MyToast("正在开发") }
    )
}