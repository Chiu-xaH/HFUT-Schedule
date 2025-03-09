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
import androidx.compose.runtime.collectAsState
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
import com.hfut.schedule.logic.utils.DataStoreManager
import com.hfut.schedule.logic.utils.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.utils.parse.SemseterParser.getSemseter
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.SharePrefs.saveBoolean
import com.hfut.schedule.logic.utils.data.SharePrefs.saveInt
import com.hfut.schedule.ui.activity.home.cube.items.main.Screen
import com.hfut.schedule.ui.activity.home.cube.items.subitems.FocusCardSettings
import com.hfut.schedule.ui.activity.home.cube.items.subitems.LockUI
import com.hfut.schedule.ui.activity.home.main.saved.COMMUNITY
import com.hfut.schedule.ui.activity.home.main.saved.JXGLSTU
import com.hfut.schedule.ui.utils.components.CustomTopBar
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.Round
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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

        val switch_show_ended = prefs.getBoolean("SWITCHSHOWENDED",true)
        var showEnded by remember { mutableStateOf(switch_show_ended) }
        saveBoolean("SWITCHSHOWENDED",true,showEnded)

        saveBoolean("SWITCHFOCUS",true,showfocus)
        var showBottomSheet_card by remember { mutableStateOf(false) }
        var sheetState_card = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        val switch_default = prefs.getInt("SWITCH_DEFAULT_CALENDAR", COMMUNITY)
        var currentDefaultCalendar by remember { mutableStateOf(switch_default) }
        saveInt("SWITCH_DEFAULT_CALENDAR",currentDefaultCalendar)


        if (showBottomSheet_card) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet_card = false },
                sheetState = sheetState_card,
                shape = Round(sheetState_card)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    topBar = {
                        CustomTopBar("即时卡片")
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
                    containerColor = Color.Transparent,
                    topBar = {
                        CustomTopBar("支付设置")
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




        TransplantListItem(
            headlineContent = { Text(text = "快速启动") },
            supportingContent = { Text(text = "打开后,再次打开应用时将默认打开免登录二级界面,而不是登陆教务页面,但您仍可通过查询中心中的选项以登录") },
            leadingContent = { Icon(painterResource(R.drawable.speed), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = faststart, onCheckedChange = {faststartch -> faststart = faststartch }) },
            modifier = Modifier.clickable { faststart = !faststart }
        )


        if(ifSaved)
            TransplantListItem(
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

        TransplantListItem(
            headlineContent = { Text(text = "学期") },
            leadingContent = {
                Icon(painter = painterResource(id = R.drawable.approval), contentDescription = "")
            },
            supportingContent = {
                Column {
                    Text(text = "全局学期 ${parseSemseter(getSemseter())}", fontWeight = FontWeight.Bold)
                    Text(text = "其他部分功能如教评、成绩等需要在全局学期下切换学期的，可在相应功能区切换")
                }
            },
            modifier = Modifier.clickable {  MyToast("全局学期不可修改,受服务器云控") }
        )

        TransplantListItem(
            headlineContent = { Text(text = "即时卡片") },
            supportingContent = { Text(text = "启动APP时会自动加载或更新一些即时数据,您可按需调整") },
            leadingContent = { Icon(painterResource(R.drawable.reset_iso), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_card = true }
        )

        TransplantListItem(
            headlineContent = { Text(text = "聚焦展示今天已上完的课程") },
            supportingContent = { Text(text = "屏幕底部的Tab栏底栏标签") },
            leadingContent = { Icon(painterResource(R.drawable.search_activity), contentDescription = "Localized description",) },
            trailingContent = { Switch(checked = showEnded, onCheckedChange = { ch -> showEnded = ch}) },
            modifier = Modifier.clickable { showEnded = !showEnded }
        )

        TransplantListItem(
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

        TransplantListItem(
            headlineContent = { Text(text = "默认课程表") },
            supportingContent = {
                Column {
                    Text(text = "您希望打开APP后课程表首先展示的是")
                    Row {
                        FilterChip(
                            onClick = {
                                currentDefaultCalendar = COMMUNITY
                                saveInt("SWITCH_DEFAULT_CALENDAR", COMMUNITY)
                            },
                            label = { Text(text = "智慧社区") }, selected = currentDefaultCalendar == COMMUNITY)
                        Spacer(modifier = Modifier.width(10.dp))
                        FilterChip(
                            onClick = {
                                currentDefaultCalendar = JXGLSTU
                                saveInt("SWITCH_DEFAULT_CALENDAR", JXGLSTU)
                            },
                            label = { Text(text = "教务(缓存)") }, selected = currentDefaultCalendar == JXGLSTU)
                    }
                    Text(text = if(currentDefaultCalendar == COMMUNITY)"(荐)智慧社区课程表随时更新,若发生调选退课会有一定延迟,但会自动更新" else "教务课表跟随每次刷新登陆状态而更新,在登陆教务后,发生调选退课立即发生变动,登录过期后缓存在本地,并支持冲突课程的显示" )
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",) },
            modifier = Modifier.clickable {
                currentDefaultCalendar = when(currentDefaultCalendar) {
                    JXGLSTU -> COMMUNITY
                    COMMUNITY -> JXGLSTU
                    else -> COMMUNITY
                }
                saveInt("SWITCH_DEFAULT_CALENDAR",currentDefaultCalendar)
            }
        )
        TransplantListItem(
            headlineContent = { Text(text = "支付设置") },
            supportingContent = {
                Text(text = "调用校园卡进行网电缴费时,启用生物识别快速验证")
            },
            leadingContent = { Icon(painterResource(R.drawable.lock), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { showBottomSheet_lock = true }
        )

        TransplantListItem(
            headlineContent = { Text(text = "图片验证码自动填充") },
            supportingContent = {
                Text(text = "登录教务时,使用Tesseract库提供的机器学习OCR能力,填充验证码")
            },
            leadingContent = { Icon(painterResource(R.drawable.center_focus_strong), contentDescription = "Localized description",) },
            modifier = Modifier.clickable { navController.navigate(Screen.DownloadScreen.route) }
        )
    }
}