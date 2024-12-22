package com.hfut.schedule.ui.activity.home.cube.items.NavUIs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Semseter.getSemseter
import com.hfut.schedule.logic.utils.Semseter.getSemseterCloud
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.SharePrefs.saveBoolean
import com.hfut.schedule.ui.activity.home.cube.items.subitems.FocusCardSettings
import com.hfut.schedule.ui.activity.home.cube.items.subitems.LockUI
import com.hfut.schedule.ui.utils.MyToast
import com.hfut.schedule.ui.utils.Round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APPScreen(navController: NavController,
              innerPaddings : PaddingValues,
              ifSaved : Boolean,) {
    // Design your second screen here
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings)) {
        Spacer(modifier = Modifier.height(5.dp))

        val switch_focus = prefs.getBoolean("SWITCHFOCUS",true)
        var showfocus by remember { mutableStateOf(switch_focus) }
        val switch_faststart = SharePrefs.prefs.getBoolean("SWITCHFASTSTART",
            prefs.getString("TOKEN","")?.isNotEmpty() ?: false)
        var faststart by remember { mutableStateOf(switch_faststart) }
        saveBoolean("SWITCHFASTSTART", prefs.getString("TOKEN","")?.isNotEmpty() ?: false,faststart)

        val switch_startUri = prefs.getBoolean("SWITCHSTARTURI",true)
        var showStartUri by remember { mutableStateOf(switch_startUri) }
        saveBoolean("SWITCHSTARTURI",true,showStartUri)

        val switch_update = prefs.getBoolean("SWITCHUPDATE",true)
        var showSUpdate by remember { mutableStateOf(switch_update) }
        saveBoolean("SWITCHUPDATE",true,showSUpdate)


        saveBoolean("SWITCHFOCUS",true,showfocus)
        var showBottomSheet_card by remember { mutableStateOf(false) }
        var sheetState_card = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showBottomSheet_card) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_card = false },
                sheetState = sheetState_card,
                shape = Round(sheetState_card)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("即时卡片") },
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        FocusCardSettings()
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
        var showBottomSheet_lock by remember { mutableStateOf(false) }
        var sheetState_lock = rememberModalBottomSheetState()
        if (showBottomSheet_lock) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_lock = false },
                sheetState = sheetState_lock,
                shape = Round(sheetState_lock)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("支付设置") },
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        LockUI()
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }




        ListItem(
            headlineContent = { Text(text = "快速启动") },
            supportingContent = { Text(text = "打开后,再次打开应用时将默认打开免登录二级界面,而不是登陆教务页面,但您仍可通过查询中心中的选项以登录") },
            leadingContent = { Icon(painterResource(R.drawable.speed), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = faststart, onCheckedChange = {faststartch -> faststart = faststartch }) },
            modifier = Modifier.clickable { faststart = !faststart }
        )


        if(ifSaved)
            ListItem(
                headlineContent = { Text(text = "主页面") },
                supportingContent = {
                    Column {
                        Text(text = "选择作为本地速览的第一页面")
                        Row {
                            FilterChip(
                                onClick = {
                                    showfocus = true
                                    saveBoolean("SWITCHFOCUS",true,showfocus)
                                },
                                label = { Text(text = "聚焦") }, selected = showfocus)
                            Spacer(modifier = Modifier.width(10.dp))
                            FilterChip(
                                onClick = {
                                    showfocus = false
                                    saveBoolean("SWITCHFOCUS",false,showfocus)
                                },
                                label = { Text(text = "课程表") }, selected = !showfocus)
                        }
                    }
                },
                leadingContent = { Icon(painterResource(if(showfocus) R.drawable.lightbulb else R.drawable.calendar), contentDescription = "Localized description",) },
                //trailingContent = { Switch(checked = showfocus, onCheckedChange = {showfocusch -> showfocus = showfocusch }) },
                modifier = Modifier.clickable {
                    showfocus = !showfocus
                    saveBoolean("SWITCHFOCUS",true,showfocus)
                }
            )

        ListItem(
            headlineContent = { Text(text = "学期") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.approval), contentDescription = "")
            },
            supportingContent = {
                Column {
                    Text(text = "全局学期 ${getSemseter(getSemseterCloud())}", fontWeight = FontWeight.Bold)
                    Text(text = "其他部分功能如教评、成绩等需要在全局学期下切换学期的，可在相应功能区切换")
                }
            },
            modifier = Modifier.clickable {  MyToast("全局学期不可修改,受服务器云控") }
        )

        ListItem(
            headlineContent = { Text(text = "即时卡片") },
            supportingContent = { Text(text = "启动APP时会自动加载或更新一些即时数据,您可按需调整") },
            leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_card = true }
        )

        ListItem(
            headlineContent = { Text(text = "打开外部链接") },
            supportingContent = {
                Column {
                    Text(text = "您希望链接从应用内部打开或者调用系统浏览器")
                    Row {
                        FilterChip(
                            onClick = {
                                showStartUri = true
                                saveBoolean("SWITCHSTARTURI",true,showStartUri)
                            },
                            label = { Text(text = "应用内部") }, selected = showStartUri)
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                showStartUri = false
                                saveBoolean("SWITCHSTARTURI",false,showStartUri)
                            },
                            label = { Text(text = "外部浏览器") }, selected = !showStartUri)
                    }
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                showStartUri = !showStartUri
                saveBoolean("SWITCHSTARTURI",true,showStartUri)
            }
        )

        ListItem(
            headlineContent = { Text(text = "支付设置") },
            supportingContent = {
                Text(text = "调用校园卡进行网电缴费时,启用生物识别快速验证")
            },
            leadingContent = { Icon(painterResource(R.drawable.lock), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_lock = true }
        )
    }
}