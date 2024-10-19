package com.hfut.schedule.ui.Activity.success.main.saved

import android.content.ContentValues
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.dao.dataBaseSchedule
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.main.AddCourseUI
import com.hfut.schedule.ui.Activity.success.main.getFriendsList
import com.hfut.schedule.ui.Activity.success.main.getFriendsCourse
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.Round
import java.io.File


val JXGLSTU = 0
val COMMUNITY = 1
val NEXT = 2
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiScheduleSettings(
    ifSaved : Boolean,
    select : Int,
    onSelectedChange : (Int) -> Unit,
    vm : LoginSuccessViewModel,
    onFriendChange : (Boolean) -> Unit,
) {

    var num  by remember { mutableStateOf(getNum()) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialog_Add by remember { mutableStateOf(false) }
    var showDialog_Del by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val sheetState_add = rememberModalBottomSheetState()
    var showBottomSheet_add by remember { mutableStateOf(false) }

    var isFriendMode by remember { mutableStateOf(false) }

    //已选择
    var selected  by remember { mutableStateOf(select) }
    //长按要删除的
    var selectedDel  by remember { mutableStateOf(select) }
    LaunchedEffect(selected,isFriendMode) {
        onSelectedChange(selected)
        onFriendChange(isFriendMode)
    }
    num = getNum()
    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showDialog = false
                Remove(selectedDel)
            },
            dialogTitle = "提示",
            dialogText = "要删除课表 ${getIndex(num)} 吗",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }
    if(showDialog_Add) {
        LittleDialog(
            onDismissRequest = { showDialog_Add = false },
            onConfirmation = {
                Add("课表"+(getNum()+4).toString())
                showDialog_Add = false
            },
            dialogTitle = "添加",
            dialogText = "确定新的课表",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }
    if(showDialog_Del) {
        LittleDialog(
            onDismissRequest = { showDialog_Del = false },
            onConfirmation = {
                showDialog_Del = false
                DeleteAll()
            },
            dialogTitle = "提示",
            dialogText = "要删除自定义添加的全部课表吗",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }
    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, modifier = Modifier,
             shape = Round(sheetState)
        ) {
            Scaffold(
                topBar = {
                    androidx.compose.material3.TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("说明") },
                    )
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
        ModalBottomSheet(onDismissRequest = { showBottomSheet_add = false }, sheetState = sheetState_add, modifier = Modifier,
            shape = Round(sheetState_add)
        ) {
            Scaffold(
                topBar = {
                    androidx.compose.material3.TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("添加课程表") },
                    )
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
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        androidx.compose.material3.TopAppBar(
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = { Text("课程表") },
            actions = {
                Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                    FilledTonalIconButton(onClick = { showBottomSheet_add = true }) {
                        Icon(painterResource(id = R.drawable.add), contentDescription = "")
                    }
                    FilledTonalIconButton(onClick = { showBottomSheet = true }) {
                        Icon(painterResource(id = R.drawable.info), contentDescription = "")
                    }
                }
            }
        )
        val friendList = getFriendsList()
        LazyRow {
            //教务课表
            item {
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            isFriendMode = false
                            selected = JXGLSTU
                        },
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("教务系统", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(selected == JXGLSTU) FontWeight.Bold else FontWeight.Thin)
                    }
                }
            }
            //社区课表
            item {
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            isFriendMode = false
                            selected = COMMUNITY
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("智慧社区", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(selected == COMMUNITY) FontWeight.Bold else FontWeight.Thin
                        )
                    }
                }
            }
            //下学期课表
            item {
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            isFriendMode = false
                            if (isNextOpen()) {
                                selected = NEXT
                            } else {
                                MyToast("入口暂未开放")
                            }
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text("下学期", modifier = Modifier.align(Alignment.Center)
                            , fontWeight = if(selected == NEXT) FontWeight.Bold else FontWeight.Thin
                        )
                    }
                }
            }
            //好友课表
            items(friendList.size) { item ->
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .combinedClickable(
                            onClick = {
                                //点击加载好友课表
                                val studentId = friendList[item]?.userId
                                studentId?.let { getFriendsCourse(it, vm) }
                                isFriendMode = true
                                if (studentId != null) {
                                    selected = studentId.toInt()
                                }
                            },
                            onLongClick = {
                                //s删除

                            }
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        friendList[item]?.realname?.let {
                            Text(
                                it,
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = if(selected.toString() == (friendList[item]?.userId ?: 999)) FontWeight.Bold else FontWeight.Thin
                            )
                        }
                    }
                }
            }
            //文件导入课表
            items(num) { item ->
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .combinedClickable(
                            onClick = {
                                isFriendMode = false
                                selected = item + 1 + 2
                            },
                            //  onDoubleClick = {},
                            onLongClick = {
                                //s删除
                                selectedDel = item + 1
                                showDialog = true
                            }
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(getIndex(item+1).toString(), modifier = Modifier.align(Alignment.Center),
                            fontWeight = if(selected == item+1+2) FontWeight.Bold else FontWeight.Thin)
                    }
                }
            }
            //添加按钮
            item {
                Card(
                    modifier = Modifier
                        .size(width = 100.dp, height = 70.dp)
                        .padding(horizontal = 4.dp)
                        .clickable {
                            showBottomSheet_add = true
                            //showDialog_Add = true
                            //MyToast("请于文件管理选择他人分享的文件(json,txt)以本应用打开")
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Icon(painterResource(id = R.drawable.add), contentDescription = "",modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
        DividerText(text = "操作")
        ListItem(
            headlineContent = { Text(text = "导出教务课表") },
            leadingContent = {
                Icon(painterResource(id = R.drawable.ios_share), contentDescription = "")
            },
            modifier = Modifier.clickable {
                prefs.getString("json","")?.let { saveTextToFile("HFUT-Schedule-Share.txt", it) }
                shareTextFile("HFUT-Schedule-Share.txt")
            }
        )
        ListItem(
            headlineContent = { Text(text = "刷新教务课表") },
            leadingContent = {
                Icon(painterResource(id = R.drawable.rotate_right), contentDescription = "")
            },
            modifier = Modifier.clickable {
                if(ifSaved)
                Login()
                else MyToast("目前已是登陆状态")
            }
        )
        ListItem(
            headlineContent = { Text(text = "写入日历日程") },
            leadingContent = {
                Icon(painterResource(id = R.drawable.calendar), contentDescription = "")
            },
            modifier = Modifier.clickable { MyToast("正在开发") }
        )
        ListItem(
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


fun Add(title : String) {
    val dbwritableDatabase =  dataBaseSchedule.writableDatabase
    dataBaseSchedule.writableDatabase
    val values1 = ContentValues().apply {
        put("title", title)
    }
    dbwritableDatabase.insert("Schedule", null, values1)
}

fun Remove(id : Int) {
    Save("SCHEDULE" + getIndex(id),null)
    val dbwritableDatabase = dataBaseSchedule.writableDatabase
    // 执行删除操作
    dbwritableDatabase.delete("Schedule", "id = ?", arrayOf(id.toString()))
}

fun getNum() : Int {
    return try {
        val db =  dataBaseSchedule.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM Schedule", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        count
    } catch (_:Exception) {
        0
    }
}

fun getIndex(id : Int) : String? {
    return try {
        val db = dataBaseSchedule.readableDatabase
        val cursor = db.rawQuery("SELECT title FROM Schedule WHERE id = ?", arrayOf(id.toString()))
        var title: String? = null
        if (cursor.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
        }
        cursor.close()
        db.close()
        return title
    } catch (_:Exception) {
        null
    }
}

fun isNextOpen() : Boolean {
    return try {
        Gson().fromJson(prefs.getString("my", MyApplication.NullMy), MyAPIResponse::class.java).Next
    } catch (_:Exception) {
        false
    }
}

fun DeleteAll() {
    MyApplication.context.deleteDatabase("Schedule.db")
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
fun InfoUI() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "数据说明") },
            supportingContent = { Text(text = "每个课表都是独立的数据源,用户可以自行切换,也可自行导入好友分享或者自行从教务系统提取到的文件")}
        )
    }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "教务系统课表") },
            supportingContent = { Text(text = "此课表随用户每次登录更新,须由用户手动刷新(刷新登陆状态 选项),此课表的数据也是最权威的,选退调课后刷新教务课表会立刻变化")}
        )
    }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "智慧社区课表") },
            supportingContent = { Text(text = "此课表自动刷新,自动跟随学期,只要用户登陆过就会记住登陆状态,但是此课表的数据更新稍微有延迟,退选调课之后大概次日才会更新")}
        )
    }
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        ListItem(
            headlineContent = { Text(text = "下学期课表") },
            supportingContent = { Text(text = "在每学期末尾时教务系统会排出下学期的课表,但此时学期仍未变化,可以从这里预先查看下学期安排")}
        )
    }
}

