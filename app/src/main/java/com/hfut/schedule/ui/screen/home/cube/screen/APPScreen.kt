package com.hfut.schedule.ui.screen.home.cube.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.parse.SemseterParser.parseSemseter
import com.hfut.schedule.logic.util.parse.SemseterParser.getSemseterWithoutSuspend
import com.hfut.schedule.logic.util.parse.SemseterParser.reverseGetSemester
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveBoolean
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveInt
import com.hfut.schedule.logic.util.storage.cleanCache
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.cube.Screen
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.isSuccessTransfer
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.style.align.RowHorizontal
import com.xah.transition.util.TransitionPredictiveBackHandler
import kotlinx.coroutines.launch
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.status.CustomSwitch
import com.hfut.schedule.ui.util.SaveComposeAsImage
import com.xah.uicommon.component.slider.CustomSlider
import kotlinx.coroutines.async

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun APPScreen(
    navController: NavHostController,
    innerPaddings: PaddingValues,
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }
    TransitionPredictiveBackHandler(navController,enablePredictive) {
        scale = it
    }
    val context = LocalContext.current
    val tabThumbFilePath = remember { mutableStateOf("") }
    val saveTrigger = remember { mutableIntStateOf(0) }
    if (saveTrigger.intValue == 1) {
        SaveComposeAsImage(saveTrigger, "my_tab", tabThumbFilePath)
    }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .fillMaxSize()
        .padding(innerPaddings).scale(scale)) {
        Spacer(modifier = Modifier.height(5.dp))
        val switch_focus = prefs.getBoolean("SWITCHFOCUS",true)
        var showfocus by remember { mutableStateOf(switch_focus) }

        val controlCenter by DataStoreManager.enableControlCenter.collectAsState(initial = false)

        val switch_update = prefs.getBoolean("SWITCHUPDATE",true)
        var showSUpdate by remember { mutableStateOf(switch_update) }
        saveBoolean("SWITCHUPDATE",true,showSUpdate)

        val switch_show_ended = prefs.getBoolean("SWITCHSHOWENDED",true)
        var showEnded by remember { mutableStateOf(switch_show_ended) }
        saveBoolean("SWITCHSHOWENDED",true,showEnded)

        saveBoolean("SWITCHFOCUS",true,showfocus)
//        var showBottomSheet_card by remember { mutableStateOf(false) }

        val switch_default = prefs.getInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)
        var currentDefaultCalendar by remember { mutableIntStateOf(switch_default) }
        saveInt("SWITCH_DEFAULT_CALENDAR",currentDefaultCalendar)

        val scope = rememberCoroutineScope()
        val autoTerm by DataStoreManager.enableAutoTerm.collectAsState(initial = true)
        val autoTermValue by DataStoreManager.customTermValue.collectAsState(initial = getSemseterWithoutSuspend())
        val maxFlow by DataStoreManager.maxFlow.collectAsState(initial = MyApplication.DEFAULT_MAX_FREE_FLOW)
        var value by remember { mutableFloatStateOf(maxFlow.toFloat()) }
        LaunchedEffect(maxFlow) {
            value = maxFlow.toFloat()
        }

        DividerTextExpandedWith("偏好") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text(text = "主页面") },
                    supportingContent = {
                        Column {
                            Text(text = "选择作为冷启动后的第一页面")
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
                    leadingContent = { Icon(
                        painterResource(R.drawable.home),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable {
                        showfocus = !showfocus
                        saveBoolean("SWITCHFOCUS",true,showfocus)
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "默认课程表") },
                    supportingContent = {
                        Column {
                            Text(text = "您希望打开APP后聚焦展示的数据源以及课程表首先展示的页面")
                            Row {
                                FilterChip(
                                    onClick = {
                                        currentDefaultCalendar = CourseType.COMMUNITY.code
                                        saveInt("SWITCH_DEFAULT_CALENDAR", CourseType.COMMUNITY.code)
                                    },
                                    label = { Text(text = "智慧社区") }, selected = currentDefaultCalendar == CourseType.COMMUNITY.code)
                                Spacer(modifier = Modifier.width(10.dp))
                                FilterChip(
                                    onClick = {
                                        currentDefaultCalendar = CourseType.JXGLSTU.code
                                        saveInt("SWITCH_DEFAULT_CALENDAR", CourseType.JXGLSTU.code)
                                    },
                                    label = { Text(text = "教务(缓存)") }, selected = currentDefaultCalendar == CourseType.JXGLSTU.code)
                            }
                            Text(text = if(currentDefaultCalendar == CourseType.COMMUNITY.code)"智慧社区课表有调课不需要像教务数据源自己手动刷新，基本次日会自动刷新，但是有时会抽风，对面给的数据不新鲜，转专业用户更推荐使用教务数据源" else "教务课表跟随每次刷新登陆状态而更新,在登陆教务后,发生调选退课立即发生变动,登录过期后缓存在本地,支持调休设置" )
                        }
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.calendar),
                        contentDescription = "Localized description"
                    ) },
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "聚焦仍显示已上完的课程") },
                    supportingContent = {
                        Text("显示今天上完的课程")
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.search_activity),
                        contentDescription = "Localized description"
                    ) },
                    trailingContent = { Switch(checked = showEnded, onCheckedChange = { ch -> showEnded = ch}) },
                    modifier = Modifier.clickable { showEnded = !showEnded }
                )
            }

        }
        DividerTextExpandedWith("配置") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("宣城校区校园网月免费额度 ${formatDecimal(value.toDouble(),0)}GiB")},
                    supportingContent = {
                        Text("用于计算和显示使用百分比 (初始值为${MyApplication.DEFAULT_MAX_FREE_FLOW}GiB)")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.net),null)
                    },
                )
                CustomSlider(
                    value = value,
                    onValueChange = {
                        value = it
                    },
                    onValueChangeFinished = {
                        scope.launch {
                            DataStoreManager.saveMaxFlow(formatDecimal(value.toDouble(),0).toInt())
                        }
                    },
                    steps = 37,
                    valueRange = 10f..200f,
                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
//                    showProcessText = true,
//                    processText = formatDecimal(value.toDouble(),0).toString() + "GiB"
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "自动计算学期") },
                    leadingContent = {
                        Icon(painter = painterResource(id = R.drawable.approval), contentDescription = "")
                    },
                    supportingContent = {
                        Column {
                            Text(text = parseSemseter(if(autoTerm) getSemseterWithoutSuspend() else autoTermValue), fontWeight = FontWeight.Bold)
                            Text(text = "全局学期主控教务课程表等信息，其他部分功能如教评等可在相应功能区自由切换学期\n由本地函数计算，每年的2~7月为第二学期，8~次1月为第一学期")
                        }
                    },
                    trailingContent = {
                        Switch(checked = autoTerm, onCheckedChange = { scope.launch { DataStoreManager.saveAutoTerm(!autoTerm) }})
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveAutoTerm(!autoTerm) }
                    }
                )
                if(!autoTerm) {
                    RowHorizontal {
                        FilledTonalButton (
                            onClick = { scope.launch {
                                if(autoTermValue >= 0) {
                                    DataStoreManager.saveAutoTermValue(autoTermValue-20)
                                }
                            } }
                        ) {
                            Icon(Icons.Filled.KeyboardArrowLeft,null)
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP))
                        FilledTonalButton(
                            onClick = { scope.launch {
                                reverseGetSemester(DateTimeManager.Date_yyyy_MM)?.let { DataStoreManager.saveAutoTermValue(it) }
                            } }
                        ) {
                            Icon(painterResource(R.drawable.refresh),null)
                        }
                        Spacer(Modifier.width(APP_HORIZONTAL_DP))
                        FilledTonalButton(
                            onClick = { scope.launch {
                                DataStoreManager.saveAutoTermValue(autoTermValue+20)
                            } }
                        ) {
                            Icon(Icons.Filled.KeyboardArrowRight,null)
                        }
                    }
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                }
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "默认日程账户") },
                    supportingContent = {
                        Text("自定义添加到系统日历的账户;\n利用邮箱账户，可进行课表、聚焦日程的多设备同步")
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.calendar_add_on),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable { navController.navigate(Screen.CalendarScreen.route) }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text(text = "图片验证码自动填充") },
                    supportingContent = {
                        Text(text = "登录教务时,使用Tesseract库提供的OCR能力,填充验证码")
                    },
                    leadingContent = { Icon(
                        painterResource(R.drawable.center_focus_strong),
                        contentDescription = "Localized description"
                    ) },
                    modifier = Modifier.clickable { navController.navigate(Screen.DownloadScreen.route) }
                )
            }
        }
        DividerTextExpandedWith("存储") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("备份与恢复") },
                    leadingContent = { Icon(painterResource(R.drawable.database),null)},
                    supportingContent = {
                        Text("将本地数据库和偏好设置导出或导入")
                    },
                    modifier = Modifier.clickable {
                        showToast("正在开发")
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("缓存清理") },
                    leadingContent = { Icon(painterResource(R.drawable.mop),null)},
                    supportingContent = {
                        Text("清理一些缓存，这不会影响应用数据")
                    },
                    modifier = Modifier.clickable {
                        scope.launch {
                            val result = async { cleanCache(context) }.await()
                            showToast("已清理 $result MB")
                        }
                    }
                )
            }
        }
        DividerTextExpandedWith("交互") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                //                TransplantListItem(
//                    headlineContent = { Text("长截图") },
//                    leadingContent = { Icon(painterResource(R.drawable.screenshot_frame),null)},
//                    supportingContent = {
//                        Text("开启后，在支持长截图的界面中，可自动滚动并保存截图")
//                    },
//                    trailingContent = {
//                        Switch(checked = false, onCheckedChange = { })
//                    },
//                    modifier = Modifier.clickable {
//                        saveTrigger.value = 1
//                    }
//                )
//                PaddingHorizontalDivider()

                TransplantListItem(
                    headlineContent = { Text(text = "预测式返回") },
                    supportingContent = {
                        if(AppVersion.CAN_PREDICTIVE) {
                            Text(text = "同Activity之间的部分转场可使用跟手的返回手势")
                        } else {
                            Text(text = "需为 Android 13+")
                        }
                    },
                    leadingContent = { Icon(painterResource(R.drawable.swipe_left), contentDescription = "Localized description",) },
                    trailingContent = {
                        Switch(enabled = AppVersion.CAN_PREDICTIVE,checked = enablePredictive, onCheckedChange = { scope.launch { DataStoreManager.savePredict(!enablePredictive) }})
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.savePredict(!enablePredictive) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("启动台") },
                    supportingContent = {
                        Text("从顶栏位置的左边缘向内轻扫唤出(部分界面暂未支持)，可快速切换到最近打开的窗口")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.flash_on),null)
                    },
                    trailingContent = {
                        Switch(checked = controlCenter, onCheckedChange = { scope.launch { DataStoreManager.saveControlCenter(!controlCenter) } })
                    },
                    modifier = Modifier.clickable {
                        scope.launch { DataStoreManager.saveControlCenter(!controlCenter) }
                    }
                )
                PaddingHorizontalDivider()
                TransplantListItem(
                    headlineContent = { Text("交互学习") },
                    supportingContent = {
                        Text("学习APP的手势交互操作")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.gesture),null)
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.GestureStudyScreen.route)
                    }
                )
            }
        }
        DividerTextExpandedWith("对外") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = { Text("自定义Shortcut") },
                    supportingContent = {
                        Text("自定义桌面APP图标长按菜单项目或控制中心按钮")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.menu),null)
                    },
                    modifier = Modifier.clickable {
                        showToast("正在开发")
                    }
                )
            }
        }
        InnerPaddingHeight(innerPaddings,false)
    }
}

