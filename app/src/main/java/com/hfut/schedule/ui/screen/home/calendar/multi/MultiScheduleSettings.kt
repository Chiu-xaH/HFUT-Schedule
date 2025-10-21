package com.hfut.schedule.ui.screen.home.calendar.multi

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.util.MyApiParse.isNextOpen
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.addCourseToEvent
import com.hfut.schedule.logic.util.sys.delAllCourseEvent
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.CustomBottomSheet
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.ColumnVertical
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


enum class CourseType(val code : Int) {
    JXGLSTU(0),
    COMMUNITY(1),
    JXGLSTU2(2),
    ZHI_JIAN(3),
    NEXT(4)
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MultiScheduleSettings(
    ifSaved : Boolean,
    select : Int,
    onSelectedChange : (Int) -> Unit,
    vm : NetWorkViewModel,
) {
    val context = LocalContext.current

    var showBottomSheet_add by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showBottomSheet_add) {
        CustomBottomSheet (onDismissRequest = { showBottomSheet_add = false },
            showBottomSheet = showBottomSheet_add,
            isFullExpand = false
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("添加课程表", isPaddingStatusBar = false)
                }
            ) {innerPadding->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    AddCourseUI(vm)
                }
            }
        }
    }

    var showBottomSheet_loading by remember { mutableStateOf(false) }

    if (showBottomSheet_loading) {
        CustomBottomSheet (
            onDismissRequest = { showBottomSheet_loading = false },
            showBottomSheet = showBottomSheet_loading,
            autoShape = false
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                HazeBottomSheetTopBar("上课提醒(以教务课表为数据源)", isPaddingStatusBar = false)
                EventUI()
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie() ?: ""
    }

    val selectedColor = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    val normalColor = CardDefaults.outlinedCardColors(containerColor = cardNormalColor())

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        HazeBottomSheetTopBar("多课表", isPaddingStatusBar = false) {
            Row() {
                FilledTonalIconButton(onClick = { showBottomSheet_add = true }) {
                    Icon(painterResource(id = R.drawable.add), contentDescription = "")
                }
            }
        }
        val friendList = getFriendsList()

        LazyRow {
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-CARD_NORMAL_DP*2)) }
            // 教务课表
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clickable {
                            onSelectedChange(CourseType.JXGLSTU.code)
                        },
                    colors = if(select == CourseType.JXGLSTU.code) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("教务系统", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(select == CourseType.JXGLSTU.code) FontWeight.Bold else FontWeight.Light)
                    }
                }
            }
            // 社区课表
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clickable {
                            onSelectedChange(CourseType.COMMUNITY.code)
                        },
                    colors = if(select == CourseType.COMMUNITY.code) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("智慧社区", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(select == CourseType.COMMUNITY.code) FontWeight.Bold else FontWeight.Light
                        )
                    }
                }
            }
            // 教务课表2
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clickable {
                            onSelectedChange(CourseType.JXGLSTU2.code)
                        },
                    colors = if(select == CourseType.JXGLSTU2.code) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()){
                        ColumnVertical(modifier = Modifier.align(Alignment.Center)) {
                            Text(
                                "教务备用",
                                fontWeight = if (select == CourseType.JXGLSTU2.code) FontWeight.Bold else FontWeight.Light
                            )
                        }
                    }
                }
            }
            // 指尖工大
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clickable {
                            onSelectedChange(CourseType.ZHI_JIAN.code)
                        },
                    colors = if(select == CourseType.ZHI_JIAN.code) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("指间工大", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(select == CourseType.ZHI_JIAN.code) FontWeight.Bold else FontWeight.Light
                        )
                    }
                }
            }
            // 下学期课表
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            if (isNextOpen()) {
                                if (ifSaved) {
                                    if (prefs.getInt("FIRST", 0) != 0)
                                        onSelectedChange(CourseType.NEXT.code)
                                    else refreshLogin(context)
                                } else onSelectedChange(CourseType.NEXT.code)
                            } else {
                                if (!ifSaved) {
                                    scope.launch {
                                        Starter.startWebView(
                                            context,
                                            url = if (GlobalUIStateHolder.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
                                            title = "教务系统",
                                            cookie = cookie,
                                            icon = AppNavRoute.NextCourse.icon
                                        )
                                    }
                                } else {
                                    showToast("入口暂未开放")
                                }
                            }
                        },
                    colors = if(select == CourseType.NEXT.code) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("下学期", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(select == CourseType.NEXT.code) FontWeight.Bold else FontWeight.Light
                        )
                    }
                }
            }
            // 好友课表
            items(friendList.size) { item ->
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .combinedClickable(
                            onClick = {
                                //点击加载好友课表
                                val studentId = friendList[item]?.userId
                                studentId?.let { getFriendsCourse(it, vm) }
                                if (studentId != null) {
                                    onSelectedChange(studentId.toInt())
                                }
                            },
                            onLongClick = {
                                //s删除
                            }
                        ),
                    colors = if(select.toString() == (friendList[item]?.userId ?: 999)) selectedColor else normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        friendList[item]?.realname?.let {
                            Text(
                                it,
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = if(select.toString() == (friendList[item]?.userId ?: 999)) FontWeight.Bold else FontWeight.Light
                            )
                        }
                    }
                }
            }
            //添加按钮
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = CARD_NORMAL_DP)
                        .clickable {
                            showBottomSheet_add = true
                            //showDialog_Add = true
                            //MyToast("请于文件管理选择他人分享的文件(json,txt)以本应用打开")
                        },
                    colors = normalColor
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(painterResource(id = R.drawable.add), contentDescription = "",modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
        }
        Spacer(Modifier.height(CARD_NORMAL_DP))
        // 第${formatDecimal(week.toDouble(),0)}周
        DividerTextExpandedWith(text = "操作") {
            CustomCard (color = cardNormalColor()){
//                CustomSlider(
//                    value = week,
//                    onValueChange = {
//                        week = it
//                    },
//                    onValueChangeFinished = {
//                        showToast("正在开发")
//                    },
//                    steps = 18,
//                    valueRange = 1f..20f,
//                    showProcessText = true,
//                    processText = "${formatDecimal(week.toDouble(),0)}周"
//                )
//                DashedDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP))
                if(select == CourseType.JXGLSTU.code) {
                    TransplantListItem(
                        headlineContent = { Text(text = "导出教务课表") },
                        supportingContent = {  Text("以文件导出教务课表") },
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.ios_share), contentDescription = "")
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                LargeStringDataManager.read(context, LargeStringDataManager.DATUM)?.let { saveTextToFile("HFUT-Schedule-Share.txt", it) }
                                shareTextFile("HFUT-Schedule-Share.txt")
                            }
                        }
                    )
                    PaddingHorizontalDivider()
                }
                val d =ifSaved && (select == CourseType.JXGLSTU.code || select == CourseType.JXGLSTU2.code)
                TransplantListItem(
                    headlineContent = { Text(text = if(d) "刷新教务课表" else "刷新课程表") },
                    supportingContent = { Text(if(d) "教务登录会失效,无法自动刷新" else "回到聚焦,等待转圈完成即完成刷新") },
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.rotate_right), contentDescription = "")
                    },
                    modifier = Modifier.clickable {
                        if(d)
                            refreshLogin(context)
                        else {
                            showToast("请回到聚焦,等待转圈完成即刷新完成")
                        }
                    }
                )
                if(select == CourseType.JXGLSTU.code) {
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        supportingContent = { Text("将教务课表以日程写入到日历中") },
                        headlineContent = { Text(text = "上课提醒") },
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.calendar_add_on), contentDescription = "")
                        },
                        modifier = Modifier.clickable {
                            showBottomSheet_loading = true
                        }
                    )
                }
//                if(AppVersion.isPreview()) {
//                    PaddingHorizontalDivider()
//                    TransplantListItem(
//                        headlineContent = { Text(text = "恢复默认状态") },
//                        leadingContent = {
//                            Icon(painterResource(id = R.drawable.delete), contentDescription = "")
//                        },
//                        modifier = Modifier.clickable {
//                            showDialog_Del = true
//                        }
//                    )
//                }
            }
        }
//        Spacer(Modifier.height(APP_HORIZONTAL_DP).navigationBarsPadding())
    }
}

fun saveTextToFile( fileName: String, content: String) {
    val file = File(MyApplication.context.getExternalFilesDir(null), fileName)
    file.writeText(content)
}

fun shareTextFile(fileName: String) {
    val file = File(MyApplication.context.getExternalFilesDir(null), fileName)
    val uri = FileProvider.getUriForFile(MyApplication.context, "${MyApplication.context.packageName}.provider", file)

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    MyApplication.context.startActivity(Intent.createChooser(shareIntent, "分享课表给他人").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}


@Composable
private fun EventUI() {
    val activity = LocalActivity.current
    val context = LocalContext.current
    var time by remember { mutableIntStateOf(20) }
    val cor = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    if(loading) {
        LoadingUI("勿动稍等")
    } else {
        CardListItem(
            overlineContent = {
                Text("提醒时间")
            },
            headlineContent = {
                Text("上课前${time}min")
            },
            trailingContent = {
                Row {
                    FilledTonalIconButton(
                        onClick = {
                            time += 5
                        }
                    ) {
                        Icon(painterResource(R.drawable.add),null)
                    }
                    time.let {
                        if(it >= 5) {
                            FilledTonalIconButton(
                                onClick = {
                                    time -= 5
                                }
                            ) {
                                Icon(painterResource(R.drawable.remove),null)
                            }
                        }
                    }
                }
            },
            leadingContent = { Icon(painterResource(R.drawable.schedule),null) },
            modifier = Modifier.clickable {
                time = 20
            }
        )
        CardListItem(
            headlineContent = {
                Text("清空+导入")
            },
            overlineContent = {
                Text("将目前的教务课表写入到日程")
            },
            leadingContent = { Icon(painterResource(R.drawable.event_upcoming),null) },
            modifier = Modifier.clickable {
                activity?.let {
                    cor.launch {
                        async { loading = true }.await()
                        async { delAllCourseEvent(context,activity = it) }.await()
                        async { addCourseToEvent(context,activity = it,time) }.await()
                        launch { loading = false }
                    }
                }
            }
        )

        CardListItem(
            headlineContent = {
                Text("清空")
            },
            overlineContent = {
                Text("清空导入的日程")
            },
            leadingContent = { Icon(painterResource(R.drawable.event_busy),null) },
            modifier = Modifier.clickable {
                activity?.let {
                    cor.launch {
                        async { loading = true }.await()
                        async { delAllCourseEvent(context,activity = it) }.await()
                        launch { loading = false }
                    }
                }
            }
        )
    }
}
