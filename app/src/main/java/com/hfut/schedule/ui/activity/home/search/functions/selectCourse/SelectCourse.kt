package com.hfut.schedule.ui.activity.home.search.functions.selectCourse

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.enums.SelectType
import com.hfut.schedule.logic.beans.jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.beans.jxglstu.SelectPostResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.activity.home.main.saved.UpdateCourses
import com.hfut.schedule.ui.activity.home.search.functions.failRate.ApiToFailRate
import com.hfut.schedule.ui.activity.home.search.functions.failRate.permit
import com.hfut.schedule.ui.activity.home.search.functions.teacherSearch.ApiToTeacherSearch
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.components.LittleDialog
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourse(ifSaved : Boolean, vm : NetWorkViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showBottomSheet_info by remember { mutableStateOf(false) }
    val sheetState_info = rememberModalBottomSheetState()

    var showDialog by remember { mutableStateOf(false) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")
    WebDialog(
        showDialog,
        { showDialog = false },
        url = if(vm.webVpn) MyApplication.JxglstuWebVpnURL else MyApplication.JxglstuURL + "for-std/course-table",
        title = "教务系统",
        cookie = cookie
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("选课") },
                        actions = {
                            Row(modifier = Modifier.padding(horizontal = AppHorizontalDp())) {
                                FilledTonalIconButton(onClick = { showBottomSheet_info = true }, ) {
                                    Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")
                                }
                                FilledTonalButton(onClick = {
                                    showDialog = true
                                }) {
                                    Text(text = "冲突预览")
                                }
                                Spacer(modifier = Modifier.width(5.dp))
                                FilledTonalButton(onClick = {
                                    UpdateCourses(vm)
                                    MyToast("已刷新课表与课程汇总")
                                }) {
                                    Text(text = "刷新课表")
                                }
                            }
                        }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseListLoading(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_info = false
                UpdateCourses(vm)
                               },
            sheetState = sheetState_info,
            shape = Round(sheetState_info)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("功能说明") }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()) {
                    SelectShuoming()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    TransplantListItem(
        headlineContent = { Text(text = "选课") },
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.ads_click), contentDescription = "")
        },
        modifier = Modifier.clickable {
            if(!ifSaved) showBottomSheet = true
            else refreshLogin()
        }
    )
}
@Composable
fun SelectShuoming() {
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "免责声明") },
            supportingContent = { Text(text = "本应用不承担选课失败造成的后果\n请登录官方系统,确保一定选课成功")}
        )
//    }
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text(text = "功能说明") },
            supportingContent = { Text(text = "点击右侧按钮选退课\n点击卡片查看详细课程信息\n退课后列表并不会更新,请手动刷新\n选退课时请核对课程代码\n若持续加载可能为教务服务器问题\n选退课完成后前往课表与课程汇总,会显示出新课程(也可在右上角手动刷新)")}
        )
//    }
}

@Composable
fun selectCourseListLoading(vm : NetWorkViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")



    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { cookie?.let { vm.verify(cookie) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.verifyData.observeForever { result ->
                       // Log.d("sss",result.toString())
                        if (result != null) {
                            if (result.contains("302")) {
                                cookie?.let { vm.getSelectCourse(it) }
                            }
                        }
                    }
                }
            }
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectCourseData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("name")) {
                               loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }


    if(loading) {
        LoadingUI()
    } else {
        SelectCourseList(vm)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseList(vm: NetWorkViewModel) {
    val list = getSelectCourseList(vm)
    var courseId by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("选课") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var showBottomSheet_selected by remember { mutableStateOf(false) }
    val sheetState_selected = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showBottomSheet_selected) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_selected = false },
            sheetState = sheetState_selected,
            shape = Round(sheetState_selected)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("已选课程") },
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    HaveSelectedCourseLoad(vm, courseId)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { ScrollText(name) },
                        actions = {
                            FilledTonalButton(onClick = {
                                showBottomSheet_selected = true
                            }, modifier = Modifier.padding(horizontal = AppHorizontalDp())) {
                                Text(text = "退课")
                            }
                        }
                    )
                },) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    SelectCourseInfoLoad(courseId,vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    LazyColumn {
        items(list.size) { item ->
            var expand by remember { mutableStateOf(false) }

//            Card(
//                elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = AppHorizontalDp(), vertical = 5.dp)
//                    .clickable {
//                        courseId = list[item].id
//                        SharePrefs.Save("courseIDS", list[item].id.toString())
//                        name = list[item].name
//                        showBottomSheet = true
//                    },
//                shape = MaterialTheme.shapes.medium,
//            )
            MyCustomCard(Modifier.fillMaxWidth()
                .padding(horizontal = AppHorizontalDp(), vertical = CardNormalDp())
                .clickable {
                    courseId = list[item].id
                    SharePrefs.saveString("courseIDS", list[item].id.toString())
                    name = list[item].name
                    showBottomSheet = true
                },
                hasElevation = false,
                containerColor = CardNormalColor()
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = list[item].name) },
                    overlineContent = { Text(text = list[item].selectDateTimeText)},
                    trailingContent = { FilledTonalIconButton(onClick = { expand = !expand }) {
                        Icon(painter = painterResource(id = if(expand) R.drawable.collapse_content else R.drawable.expand_content), contentDescription = "")
                    }},
                )
                AnimatedVisibility(
                    visible = expand,
                    enter = slideInVertically(
                        initialOffsetY = { -40 }
                    ) + expandVertically(
                        expandFrom = Alignment.Top
                    ) + scaleIn(
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                ) {
                    Column {
                        TransplantListItem(
                            headlineContent = { Text(text = "选课公告") },
                            supportingContent = {
                                Text(text = list[item].bulletin)
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(text = "选课规则") },
                            supportingContent = {
                                for (i in list[item].addRulesText) {
                                    Text(text = "$i ")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseInfoLoad(courseId : Int, vm: NetWorkViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")



    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { cookie?.let { vm.getSelectCourseInfo(it,courseId) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectCourseInfoData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("nameZh")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }




    var input by remember { mutableStateOf("") }

    if(!loading) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = AppHorizontalDp()),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜索 名称、代码、类型" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {

                            }) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant(),
                )
            }
            Spacer(modifier = Modifier.height(CardNormalDp()))
            SelectCourseInfo(vm,courseId,input)
        }
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCourseInfo(vm: NetWorkViewModel, courseId : Int, search : String = "") {
    val list = getSelectCourseInfo(vm)
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")

    var lessonId by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableStateOf(0) }
    val sheetState_info = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_info by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = "选课结果") }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseResultLoad(vm,courseId,lessonId, SelectType.add.name)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    val searchList = mutableListOf<SelectCourseInfo>()
    list.forEach { item ->
        if(item.code.contains(search) || item.course.nameZh.contains(search) || item.nameZh.contains(search) || item.remark?.contains(search) == true) {
            searchList.add(item)
        }
    }


    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_info = false },
            sheetState = sheetState_info,
//            shape = Round(sheetState_info)
        ) {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { ScrollText(text = name) },
                    actions = {
                        FilledTonalButton(onClick = {
                            showBottomSheet = true
                        }, modifier = Modifier.padding(horizontal = AppHorizontalDp())) {
                            Text(text = "选课")
                        }
                    }
                )
                courseInfo(num,searchList,vm)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn {
        items(searchList.size) {item ->
            val lists = searchList[item]
            var stdCount by remember { mutableStateOf("0") }
            LaunchedEffect(lists.id) {
                async { cookie?.let { vm.getSCount(it,lists.id) } }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.stdCountData.observeForever { result ->
                            if (result != null) {
                                if (result.contains(lists.id.toString())) {
                                    val data = parseDynamicJson(result)
                                    stdCount = data.toString().substringAfter("=").substringBefore("}")
                                }
                            }
                        }
                    }
                }
            }
//            MyCustomCard {
                val limit = lists.limitCount
                val isFull = stdCount.toInt() >= lists.limitCount
                val remark = lists.remark
                StyleCardListItem(
                    headlineContent = { Text(text = lists.course.nameZh, fontWeight = FontWeight.Bold) },
                    overlineContent = { Text(text =   "已选 " + stdCount + " / " + limit + " | ${lists.code}")},
                    supportingContent = { Text(text = lists.nameZh  + if(remark != null && remark != "") "\n${remark}" else "")},
                    trailingContent = {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            FilledTonalIconButton(
                                onClick = {
                                    lessonId = lists.id
                                    showBottomSheet = true
                                },
                                colors = if(!isFull) IconButtonDefaults.filledTonalIconButtonColors() else IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                            ) { Icon(painter = painterResource(id = R.drawable.add_2), contentDescription = "") }
                            if(isFull) {
                                Text("已满")
                            }
                        }
                                      },
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        name = lists.course.nameZh
                        num = item
                        lessonId = lists.id
                    },
                    color = if(isFull) {
                         MaterialTheme.colorScheme.errorContainer
                    } else {
                        null
                    }
                )
//            }
        }
    }
}

fun parseDynamicJson(jsonString: String): Map<String, Int> {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Int>>() {}.type
    return gson.fromJson(jsonString, mapType)
}
@Composable
fun selectCourseResultLoad(vm : NetWorkViewModel, courseId : Int, lessonId : Int, type : String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var statusText by remember { mutableStateOf("提交中") }
    var statusBoolean by remember { mutableStateOf(false) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { cookie?.let { vm.getRequestID(it,lessonId.toString(),courseId.toString(), type) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.requestIdData.observeForever { result ->
                        if (result != null) {
                            cookie?.let { vm.postSelect(it,result) }
                        }
                    }
                }
            }
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectResultData.observeForever { result ->
                        if (result != null) {
                            Log.d("e",result)
                            if (result.contains("{")) {
                                loading = false
                                refresh = false
                                val data = Gson().fromJson(result, SelectPostResponse::class.java)
                                val status = data.success
                                statusBoolean = status
                                if(status) {
                                    statusText = "成功"
                                } else {
                                    val errorText = data.errorMessage?.textZh
                                    statusText = if (errorText != null) {
                                        errorText
                                    } else "失败"
                                }
                            }
                        }
                    }
                }
            }
        }
    }




    if(!loading) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Icon( if(statusBoolean) Icons.Filled.Check else Icons.Filled.Close, contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = statusText, color = MaterialTheme.colorScheme.primary)
            }
        }
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun courseInfo(num : Int,lists : List<SelectCourseInfo>,vm: NetWorkViewModel) {
    val data = lists[num]

    val sheetState_FailRate = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_FailRate by remember { mutableStateOf(false) }

    if (showBottomSheet_FailRate) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_FailRate = false },
            sheetState = sheetState_FailRate,
            shape = Round(sheetState_FailRate)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("挂科率 ${data.course.nameZh}") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToFailRate(data.course.nameZh,vm)
                }
            }
        }
    }

    val sheetState_Teacher = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Teacher by remember { mutableStateOf(false) }

    var teacherTitle by remember { mutableStateOf("") }

    if (showBottomSheet_Teacher) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Teacher = false },
            sheetState = sheetState_Teacher,
            shape = Round(sheetState_Teacher)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("教师检索 $teacherTitle") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ApiToTeacherSearch(teacherTitle,vm)
                }
            }
        }
    }


    Row {
        ListItem(
            overlineContent = { Text(text = "学分")},
            headlineContent = { Text(text = data.course.credits.toString()) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.filter_vintage), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
        ListItem(
            overlineContent = { Text(text = "考试形式")},
            headlineContent = { Text(text = data.examMode.nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.draw), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
    }
    Row {
        ListItem(
            overlineContent = { Text(text = data.nameZh)},
            headlineContent = { Text(text = data.courseType.nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar_view_month), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
        ListItem(
            headlineContent = { Text(text = "挂科率查询") },
            leadingContent = { Icon(painterResource(R.drawable.monitoring), contentDescription = "Localized description",) },
            modifier = Modifier
                .clickable {
                    permit = 1
                    showBottomSheet_FailRate = true
                }
                .weight(.5f),
        )
    }
    val teachers = data.teachers
    for(i in teachers.indices step 2) {
        val item = teachers[i]
        Row {
            ListItem(
                overlineContent = { Text(text = "教师 ${i+1}")},
                headlineContent = { Text(text = item.nameZh) },
                leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                modifier = Modifier.weight(.5f).clickable {
                    teacherTitle = item.nameZh
                    showBottomSheet_Teacher = true
                }
            )
            if(i+1 < teachers.size) {
                val item2 = teachers[i+1]
                ListItem(
                    overlineContent = { Text(text = "教师 ${i+2}")},
                    headlineContent = { Text(text = item2.nameZh) },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
                    modifier = Modifier.weight(.5f).clickable {
                        teacherTitle = item2.nameZh
                        showBottomSheet_Teacher = true
                    }
                )
            }
        }
    }

    Row {
        ListItem(
            overlineContent = { Text(text = "代码")},
            headlineContent = { Text(text = data.code) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.tag), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
    }
    data.dateTimePlace.textZh?.let {
        ListItem(
            overlineContent = { Text(text = "安排")},
            headlineContent = { Text(text = it)  },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.calendar), contentDescription = "")}
        )
    }
}

@Composable
fun HaveSelectedCourseLoad(vm: NetWorkViewModel, courseId: Int) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


    if(refresh) {
        loading = true
        CoroutineScope(Job()).launch{
            async { cookie?.let { vm.getSelectedCourse(it, prefs.getString("courseIDS",null).toString())} }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.selectedData.observeForever { result ->
                        if (result != null) {
                            if (result.contains("{")) {
                                loading = false
                                refresh = false
                            }
                        }
                    }
                }
            }
        }
    }





    if(!loading) {
        haveSelectedCourse(vm, courseId)
    } else {
        LoadingUI()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun haveSelectedCourse(vm: NetWorkViewModel, courseId : Int) {
    val lists = getSelectedCourse(vm)
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableStateOf(0) }
    val sheetState_info = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_info by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
 //   var courseId by remember { mutableStateOf(0) }
    var lessonId by remember { mutableStateOf(0) }

    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_info = false },
            sheetState = sheetState_info,
//            shape = Round(sheetState_info)
        ) {
            Column {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = { ScrollText(text = name) },
                    actions = {
                        FilledTonalButton(onClick = { showDialog = true }, modifier = Modifier.padding(horizontal = AppHorizontalDp())) {
                            Text(text = "退课")
                        }
                    }
                )
                courseInfo(num,lists,vm)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = Round(sheetState)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(text = "调课结果") }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseResultLoad(vm,courseId,lessonId, SelectType.drop.name)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if(showDialog) {
        LittleDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                showBottomSheet = true
                showDialog = false
                             },
            dialogTitle = "警告",
            dialogText = "请再次确定,是否退掉 ${name}\n若为培养计划内的公共或专业课,如需再选需在教务完全关闭之前选择!否则无法再修改",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }

    LazyColumn {
        items(lists.size) {item ->
            val names =  lists[item].course.nameZh
//            MyCustomCard {
            StyleCardListItem(
                    headlineContent = { Text(text = names)  },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.category), contentDescription = "")},
                    trailingContent = { FilledTonalIconButton(onClick = {
                        lessonId = lists[item].id
                        name = names
                        showDialog = true
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "")
                    }},
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        num = item
                        name = names
                        lessonId = lists[item].id
                    }
                )
//            }
        }
    }
}