package com.hfut.schedule.ui.Activity.success.search.Search.SelectCourse

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.Enums.SelectType
import com.hfut.schedule.logic.datamodel.Jxglstu.SelectCourseInfo
import com.hfut.schedule.logic.datamodel.Jxglstu.SelectPostResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.Click
import com.hfut.schedule.ui.Activity.success.search.Search.More.Login
import com.hfut.schedule.ui.UIUtils.LittleDialog
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectCourse(ifSaved : Boolean,vm : LoginSuccessViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


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
    ListItem(
        headlineContent = { Text(text = "选课") },
        leadingContent = {
            Icon(painter = painterResource(id = R.drawable.ads_click), contentDescription = "")
        },
        modifier = Modifier.clickable {
            if(!ifSaved) showBottomSheet = true
            else Login()
        }
    )
}

@Composable
fun selectCourseListLoading(vm : LoginSuccessViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")


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


    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            CircularProgressIndicator()
        }
    }


    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        selectCourseList(vm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectCourseList(vm: LoginSuccessViewModel) {
    val list = getSelectCourseList(vm)
    var courseId by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("选课") }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    var showBottomSheet_selected by remember { mutableStateOf(false) }
    val sheetState_selected = rememberModalBottomSheetState()

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
                    haveSelectedCourseLoad(vm, courseId)
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
                            }, modifier = Modifier.padding(horizontal = 15.dp)) {
                                Text(text = "退课")
                            }
                        }
                    )
                },) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    selectCourseInfoLoad(courseId,vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    LazyColumn {
        items(list.size) { item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = list[item].name) },
                    overlineContent = { Text(text = list[item].selectDateTimeText)},
                    supportingContent = { Text(text = "选课公告：${list[item].bulletin}\n选课规则：${list[item].addRulesText}") },
                    trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "")},
                    modifier = Modifier.clickable {
                        courseId = list[item].id
                        SharePrefs.Save("courseIDS",list[item].id.toString())
                        name = list[item].name
                        showBottomSheet = true
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectCourseInfoLoad(courseId : Int, vm: LoginSuccessViewModel) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")


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


    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            CircularProgressIndicator()
        }
    }

    var input by remember { mutableStateOf("") }

    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 15.dp),
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    label = { Text("搜素 名称 代码 类型" ) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {

                            }) {
                            Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // 有焦点时的颜色，透明
                        unfocusedIndicatorColor = Color.Transparent, // 无焦点时的颜色，绿色
                    ),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            selectCourseInfo(vm,courseId,input)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectCourseInfo(vm: LoginSuccessViewModel,courseId : Int,search : String = "") {
    val list = getSelectCourseInfo(vm)
    val cookie = SharePrefs.prefs.getString("redirect", "")
    var lessonId by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableStateOf(0) }
    val sheetState_info = rememberModalBottomSheetState()
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

    if (showBottomSheet_info) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_info = false },
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
                        title = { ScrollText(text = name) }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    courseInfo(num,vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    val searchList = mutableListOf<SelectCourseInfo>()
    list.forEach { item ->
        if(item.code.contains(search) || item.course.nameZh.contains(search) || item.nameZh.contains(search)) {
            searchList.add(item)
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
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = lists.course.nameZh) },
                    overlineContent = { Text(text =   "代码 ${lists.code}\n"+"已选 " + stdCount + " / " +lists.limitCount)},
                    supportingContent = { Text(text = lists.nameZh  + if(lists.remark != null) "\n${lists.remark}" else "")},
                    trailingContent = {  FilledTonalIconButton(onClick = {
                        lessonId = lists.id
                        showBottomSheet = true
                    }) { Icon(painter = painterResource(id = R.drawable.add_task), contentDescription = "") }},
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        num = item
                    }
                )
            }
        }
    }
}

fun parseDynamicJson(jsonString: String): Map<String, Int> {
    val gson = Gson()
    val mapType = object : TypeToken<Map<String, Int>>() {}.type
    return gson.fromJson(jsonString, mapType)
}
@Composable
fun selectCourseResultLoad(vm : LoginSuccessViewModel,courseId : Int,lessonId : Int,type : String) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    var statusText by remember { mutableStateOf("提交中") }
    var statusBoolean by remember { mutableStateOf(false) }
    val cookie = SharePrefs.prefs.getString("redirect", "")

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


    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            CircularProgressIndicator()
        }
    }


    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Icon( if(statusBoolean) Icons.Filled.Check else Icons.Filled.Close, contentDescription = "",Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                Text(text = statusText, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun courseInfo(num : Int,vm: LoginSuccessViewModel) {
    val data = getSelectCourseInfo(vm)[num]
    Row {
        ListItem(
            overlineContent = { Text(text = "学分")},
            headlineContent = { Text(text = data.course.credits.toString()) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.hive), contentDescription = "")},
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
            overlineContent = { Text(text = "教师(仅展示第一位)")},
            headlineContent = { Text(text = data.teachers[0].nameZh) },
            leadingContent = { Icon(painter = painterResource(id = R.drawable.person), contentDescription = "")},
            modifier = Modifier.weight(.5f)
        )
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
fun haveSelectedCourseLoad(vm: LoginSuccessViewModel,courseId: Int) {
    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = SharePrefs.prefs.getString("redirect", "")


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


    AnimatedVisibility(
        visible = loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            CircularProgressIndicator()
        }
    }


    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        haveSelectedCourse(vm, courseId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun haveSelectedCourse(vm: LoginSuccessViewModel,courseId : Int) {
    val lists = getSelectedCourse(vm)
    var name by remember { mutableStateOf("课程详情") }
    var num by remember { mutableStateOf(0) }
    val sheetState_info = rememberModalBottomSheetState()
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
                        title = { ScrollText(text = name) }
                    )
                },
            ) { innerPadding ->
                Column(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()) {
                    courseInfo(num,vm)
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
            dialogText = "请再次确定;是培养计划内的必修课,退课会造成严重后果!",
            conformtext = "确定",
            dismisstext = "取消"
        )
    }

    LazyColumn {
        items(lists.size) {item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                ListItem(
                    headlineContent = { Text(text = lists[item].course.nameZh)  },
                    leadingContent = { Icon(painter = painterResource(id = R.drawable.category), contentDescription = "")},
                    trailingContent = { FilledTonalIconButton(onClick = {
                        lessonId = lists[item].id
                        showDialog = true
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "")
                    }},
                    modifier = Modifier.clickable {
                        showBottomSheet_info = true
                        num = item
                        name = lists[item].course.nameZh
                        lessonId = lists[item].id
                    }
                )
            }
        }
    }
}