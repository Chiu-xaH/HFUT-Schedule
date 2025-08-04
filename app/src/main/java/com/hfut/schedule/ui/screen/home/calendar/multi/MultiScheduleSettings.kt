package com.hfut.schedule.ui.screen.home.calendar.multi

import android.app.Activity
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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.database.DataBaseManager
import com.hfut.schedule.logic.database.entity.CustomCourseTableSummary
import com.hfut.schedule.logic.util.network.ParseJsons.isNextOpen
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.addCourseToEvent
import com.hfut.schedule.logic.util.sys.delCourseEvents
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.LittleDialog
import com.hfut.schedule.ui.component.status.LoadingUI
import com.hfut.schedule.ui.component.text.BottomSheetTopBar
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute

import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


enum class CourseType(val code : Int) {
    JXGLSTU(0),
    COMMUNITY(1),
    NEXT(2)
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MultiScheduleSettings(
    ifSaved : Boolean,
    select : Int,
    onSelectedChange : (Int) -> Unit,
    vm : NetWorkViewModel,
    vmUI : UIViewModel,
    hazeState: HazeState,
) {
    val context = LocalActivity.current
    var customList by remember { mutableStateOf<List<CustomCourseTableSummary>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialog_Del by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    var showBottomSheet_add by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    //长按要删除的
    var selectedDelTitle  by remember { mutableStateOf("课表") }
    var selectedDelId  by remember { mutableIntStateOf(0) }
    LaunchedEffect(showDialog,showDialog_Del) {
        customList = DataBaseManager.customCourseTableDao.get()
    }
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                scope.launch {
                    async { DataBaseManager.customCourseTableDao.del(selectedDelId) }.await()
                    launch { showDialog = false }
                }
            },
            dialogText = "要删除 $selectedDelTitle 吗",
            hazeState = hazeState
        )
    }

    if(showDialog_Del) {
        LittleDialog(
            onDismissRequest = { showDialog_Del = false },
            onConfirmation = {
                scope.launch {
                    async { DataBaseManager.customCourseTableDao.clearAll() }.await()
                    launch { showDialog_Del = false }
                }
            },
            dialogText = "要删除自定义添加的全部课表吗",
            hazeState = hazeState
        )
    }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            isFullExpand = false
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("说明")
                }
            ) {innerPadding->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    InfoUI()
                }
            }
        }
    }
    if (showBottomSheet_add) {
        HazeBottomSheet (onDismissRequest = { showBottomSheet_add = false },
            showBottomSheet = showBottomSheet_add,
            hazeState = hazeState,
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
                    AddCourseUI(vm,hazeState)
                }
            }
        }
    }

    var showBottomSheet_loading by remember { mutableStateOf(false) }

    if (showBottomSheet_loading) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_loading = false },
            showBottomSheet = showBottomSheet_loading,
            hazeState = hazeState,
            autoShape = false
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                HazeBottomSheetTopBar("写入日历日程(以本学期教务课表为数据源)", isPaddingStatusBar = false)
                EventUI(vmUI,context)
                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP))
            }
        }
    }

    val cookie by produceState(initialValue = "") {
        value = getJxglstuCookie(vm) ?: ""
    }

    val selectedColor = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    val normalColor = CardDefaults.outlinedCardColors(containerColor = cardNormalColor())

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        BottomSheetTopBar("多课表") {
            Row() {
                FilledTonalIconButton(onClick = { showBottomSheet_add = true }) {
                    Icon(painterResource(id = R.drawable.add), contentDescription = "")
                }
                FilledTonalIconButton(onClick = { showBottomSheet = true }) {
                    Icon(painterResource(id = R.drawable.info), contentDescription = "")
                }
            }
        }
        val friendList = getFriendsList()

        LazyRow {
            //教务课表
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
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
            //社区课表
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
            //下学期课表
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
                                    else refreshLogin()
                                } else onSelectedChange(CourseType.NEXT.code)
                            } else {
                                if(!ifSaved) {
                                    Starter.startWebView(
                                        url = if(vm.webVpn) MyApplication.JXGLSTU_WEBVPN_URL else MyApplication.JXGLSTU_URL + "for-std/course-table",
                                        title = "教务系统",
                                        cookie = cookie,
                                        icon = AppNavRoute.NextCourse.icon
                                    )
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
            //好友课表
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
//            //文件导入课表
//            items(customList.size) { index ->
//                val item = customList[index]
//                val indexOffset = index + 4
//                OutlinedCard (
//                    modifier = Modifier
//                        .size(width = 100.dp, height = 70.dp)
//                        .padding(horizontal = 4.dp)
//                        .combinedClickable(
//                            onClick = {
//                                indexOffset
//                            },
//                            onLongClick = {
//                                //s删除
//                                selectedDelTitle = item.title
//                                selectedDelId = item.id
//                                showDialog = true
//                            }
//                        ),
//                    colors = if(selected == indexOffset) selectedColor else normalColor
//                ) {
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        Text(
//                            item.title, modifier = Modifier.align(Alignment.Center),
//                            fontWeight = if(selected == indexOffset) FontWeight.Bold else FontWeight.Thin)
//                    }
//                }
//            }
            //添加按钮
            item {
                Card (
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
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
            item { Spacer(Modifier.width(APP_HORIZONTAL_DP-3.dp)) }
        }
        Spacer(Modifier.height(10.dp))
        DividerTextExpandedWith(text = "操作") {
            TransplantListItem(
                headlineContent = { Text(text = "导出教务课表") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.ios_share), contentDescription = "")
                },
                modifier = Modifier.clickable {
                    prefs.getString("json","")?.let { saveTextToFile("HFUT-Schedule-Share.txt", it) }
                    shareTextFile("HFUT-Schedule-Share.txt")
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "刷新教务课表") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.rotate_right), contentDescription = "")
                },
                modifier = Modifier.clickable {
                    if(ifSaved)
                        refreshLogin()
                    else showToast("目前已是登陆状态")
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "写入日历日程") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.calendar), contentDescription = "")
                },
                modifier = Modifier.clickable {
                    showBottomSheet_loading = true
                }
            )
            TransplantListItem(
                headlineContent = { Text(text = "恢复默认状态") },
                leadingContent = {
                    Icon(painterResource(id = R.drawable.delete), contentDescription = "")
                },
                modifier = Modifier.clickable {
                    showDialog_Del = true
                }
            )
        }

    }
}


//fun Add(title : String) {
//    val dbwritableDatabase =  dataBaseSchedule.writableDatabase
//    dataBaseSchedule.writableDatabase
//    val values1 = ContentValues().apply {
//        put("title", title)
//    }
//    dbwritableDatabase.insert("Schedule", null, values1)
//}

//fun Remove(id : Int) {
//    saveString("SCHEDULE" + getIndex(id),null)
//    val dbwritableDatabase = dataBaseSchedule.writableDatabase
//    // 执行删除操作
//    dbwritableDatabase.delete("Schedule", "id = ?", arrayOf(id.toString()))
//}

//fun getNum() : Int {
//    return try {
//        val db =  dataBaseSchedule.readableDatabase
//        val cursor = db.rawQuery("SELECT COUNT(*) FROM Schedule", null)
//        cursor.moveToFirst()
//        val count = cursor.getInt(0)
//        cursor.close()
//        db.close()
//        count
//    } catch (_:Exception) {
//        0
//    }
//}

//fun getIndex(id : Int) : String? {
//    return try {
//        val db = dataBaseSchedule.readableDatabase
//        val cursor = db.rawQuery("SELECT title FROM Schedule WHERE id = ?", arrayOf(id.toString()))
//        var title: String? = null
//        if (cursor.moveToFirst()) {
//            title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
//        }
//        cursor.close()
//        db.close()
//        return title
//    } catch (_:Exception) {
//        null
//    }
//}

//fun isNextOpen() : Boolean {
//    return try {
//        getMy()!!.Next
//    } catch (_:Exception) {
//        false
//    }
//}

//fun DeleteAll() {
//    MyApplication.context.deleteDatabase("Schedule.db")
//}


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
fun InfoUI() {
//    MyCustomCard {
    StyleCardListItem(
            headlineContent = { Text(text = "数据说明") },
            supportingContent = { Text(text = "每个课表都是独立的数据源,用户可以自行切换,也可自行导入好友分享或者自行从教务系统提取到的文件")}
        )
//    }
//    MyCustomCard{
    StyleCardListItem(
            headlineContent = { Text(text = "教务系统课表") },
            supportingContent = { Text(text = "此课表随用户每次登录更新,须由用户手动刷新(刷新登陆状态 选项),此课表的数据也是最权威的,选退调课后刷新教务课表会立刻变化")}
        )
//    }
//    MyCustomCard {
    StyleCardListItem(
            headlineContent = { Text(text = "智慧社区课表") },
            supportingContent = { Text(text = "此课表自动刷新,自动跟随学期,只要用户登陆过就会记住登陆状态,但是此课表的数据更新稍微有延迟,退选调课之后大概次日才会更新")}
        )
//    }
//    MyCustomCard {
    StyleCardListItem(
            headlineContent = { Text(text = "下学期课表") },
            supportingContent = { Text(text = "在每学期末尾时教务系统会排出下学期的课表,但此时学期仍未变化,可以从这里预先查看下学期安排")}
        )
//    }
}


@Composable
private fun EventUI(vmUI: UIViewModel,context : Activity?) {
    var time by remember { mutableIntStateOf(20) }
    val cor = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = {
            Text("可将课表导入系统日历，由日历定时提醒")
        },
        leadingContent = {
            Icon(painterResource(R.drawable.info),null)
        }
    )
    if(loading) {
        LoadingUI("勿动稍等")
    } else {
        StyleCardListItem(
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
        StyleCardListItem(
            headlineContent = {
                Text("更新日程(清空+导入)")
            },
            leadingContent = { Icon(painterResource(R.drawable.event_repeat),null) },
            modifier = Modifier.clickable {
                context?.let {
                    cor.launch {
                        async { loading = true }.await()
                        async { delCourseEvents(vmUI,activity = it) }.await()
                        async { addCourseToEvent(vmUI,activity = it,time) }.await()
                        launch { loading = false }
                    }
                }
            }
        )
        StyleCardListItem(
            headlineContent = {
                Text("导入")
            },
            overlineContent = {
                Text("跳过已导入日程")
            },
            leadingContent = { Icon(painterResource(R.drawable.event_upcoming),null) },
            modifier = Modifier.clickable {
                context?.let {
                    cor.launch {
                        async { loading = true }.await()
                        async { addCourseToEvent(vmUI,activity = it,time) }.await()
                        launch { loading = false }
                    }
                }
            }
        )
        StyleCardListItem(
            headlineContent = {
                Text("清空")
            },
            overlineContent = {
                Text("只清空上面导入日程")
            },
            leadingContent = { Icon(painterResource(R.drawable.event_busy),null) },
            modifier = Modifier.clickable {
                context?.let {
                    cor.launch {
                        async { loading = true }.await()
                        async { delCourseEvents(vmUI,activity = it) }.await()
                        launch { loading = false }
                    }
                }
            }
        )
    }
}
