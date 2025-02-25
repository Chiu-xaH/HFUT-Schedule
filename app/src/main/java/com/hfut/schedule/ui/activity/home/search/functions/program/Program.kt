package com.hfut.schedule.ui.activity.home.search.functions.program

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.beans.jxglstu.ProgramPartOne
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.beans.jxglstu.ProgramShow
import com.hfut.schedule.logic.utils.ReservDecimal
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.activity.home.search.functions.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.activity.home.search.functions.dormitoryScore.DormitoryScoreUI
import com.hfut.schedule.ui.activity.home.search.functions.life.countFunc
import com.hfut.schedule.ui.activity.home.search.functions.person.getPersonInfo
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.CampusId
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.CampusId.*
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.getCampus
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.components.DividerText
import com.hfut.schedule.ui.utils.components.DividerTextExpandedWith
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.DepartmentIcons
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.StyleCardListItem
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.statusUI
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : NetWorkViewModel, ifSaved : Boolean) {
    val sheetState_Program = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Program by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                showBottomSheet_Program = true
            }
            else refreshLogin()
        }
    )

    val sheetState_search = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_search by remember { mutableStateOf(false) }


    if (showBottomSheet_search) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_search = false },
            sheetState = sheetState_search,
            shape = Round(sheetState_search)
        ) {
            ProgramSearch(vm)
        }
    }


    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("培养方案") },
                        actions = {
                            Row (modifier = Modifier.padding(horizontal = AppHorizontalDp())){
//                                FilledTonalIconButton(
//                                    onClick = { showBottomSheet_info = true },
//                                ) {
//                                    Icon(painterResource(id = R.drawable.info), contentDescription = "")
//                                }
                                FilledTonalButton(
                                    onClick = {
                                        showBottomSheet_search = true
                                    }
                                ) {
                                    Text("全校培养方案")
                                }
                            }
                        }
                    )
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUI2(vm,ifSaved)
                    //ProgramPerformance(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProgramUI(vm: NetWorkViewModel,ifSaved: Boolean) {
  //  val list =
        val json = prefs.getString("programJSON","")
       // try {
            val listType = object : TypeToken<List<ProgramShow>>() {}.type
            val list: List<ProgramShow> = Gson().fromJson(json,listType)
      //  }

    var sum = 0.0
    for(i in list.indices) {
        val credits = list[i].credit
        if (credits != null) { sum += credits }
    }
    var showBottomSheet_Search by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    ApiForCourseSearch(vm,courseName,null,showBottomSheet_Search) {
        showBottomSheet_Search = false
    }
    LazyColumn {
        item {
//            MyCustomCard{
                StyleCardListItem(
                    headlineContent = { Text(text = "合计 ${list.size} 门 $sum 学分") },
                    supportingContent = { Text(text = "不含选修课!")},
                 //   overlineContent = { },
                 //   trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    color = MaterialTheme.colorScheme.primaryContainer,
                    leadingContent = { Icon(painterResource(id = R.drawable.conversion_path), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {},
                )
//            }
        }
        items(list.size) { item ->
            val name = list[item].name
//            MyCustomCard{
                StyleCardListItem(
                    headlineContent = { Text(text = name) },
                    supportingContent = { Text(text = list[item].school + "  第" + list[item].term[0] + "学期")},
                    overlineContent = { list[item].type?.let { Text(text = it) } },
                    trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {
                        courseName = name
                        showBottomSheet_Search = true
                    },
                )
//            }
        }
    }
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUI2(vm: NetWorkViewModel, ifSaved: Boolean) {

    val sheetState_Performance = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Performance by remember { mutableStateOf(false) }


    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val listOne = getProgramListOne(vm,ifSaved)
    var title by remember { mutableStateOf("培养方案") }
    var num by remember { mutableStateOf(0) }
    var loadingCard by remember { mutableStateOf(true) }
    val scale = animateFloatAsState(
        targetValue = if (loadingCard) 0.9f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )
    val scale2 = animateFloatAsState(
        targetValue = if (loadingCard) 0.97f else 1f, // 按下时为0.9，松开时为1
        //animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )

    val blurSize by animateDpAsState(
        targetValue = if (loadingCard) 10.dp else 0.dp, label = ""
        ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
    )
    val completion = getProgramCompletion(vm)


    if (showBottomSheet_Performance ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Performance = false },
            sheetState = sheetState_Performance,
            shape = Round(sheetState_Performance)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("培养方案 完成情况") },
                    )
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    countFunc = 0
                    ProgramPerformance(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if(ifSaved) {
        loading = false
        refresh = false
        loadingCard = true
    } else {
        if(refresh) {
            loading = true
            loadingCard = true
            CoroutineScope(Job()).launch{
                async{ cookie?.let { vm.getProgramCompletion(it)} }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.ProgramCompletionData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("总学分")) {
                                    loadingCard = false
                                }
                            }
                        }
                    }
                }
            }
            CoroutineScope(Job()).launch{
                async{ cookie?.let { vm.getProgram(it)} }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.ProgramData.observeForever { result ->
                            if (result != null) {
                                if(result.contains("children")) {
                                    loading = false
                                    refresh = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    DividerTextExpandedWith(text = if(ifSaved)"完成情况(登录后可查看)" else "完成情况",openBlurAnimation = false) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = AppHorizontalDp()),
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale2.value)
                .padding(horizontal = AppHorizontalDp(), vertical = 5.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardForListColor()
        ) {
            Column (modifier = Modifier
                .blur(blurSize)
                .scale(scale.value)){
                val res = completion.total.actual/completion.total.full * 100.0
                ListItem(
                    headlineContent = { Text(text = "已修 ${completion.total.actual}/${completion.total.full}",fontSize = 28.sp) },
                    trailingContent = {
                        Text(text = "${ReservDecimal.reservDecimal(res,1)} %")
                    }
                )
                for(i in 0 until completion.other.size step 2)
                    Row {
                        ListItem(
                            headlineContent = { Text(text = completion.other[i].name) },
                            overlineContent = {Text(text = "${completion.other[i].actual}/${completion.other[i].full}", fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(.5f)
                        )
                        if(i+1 < completion.other.size)
                            ListItem(
                                headlineContent = { Text(text = completion.other[i+1].name) },
                                overlineContent = {Text(text = "${completion.other[i+1].actual}/${completion.other[i+1].full}",fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(.5f)
                            )
                    }
            }
        }

        Button(
            onClick = {
                if(ifSaved) refreshLogin()
                else showBottomSheet_Performance = true
            },
            modifier = Modifier
                .fillMaxWidth().scale(scale2.value)
                .padding(horizontal = AppHorizontalDp()),
        ) {
            Text(text = "培养方案进度")
        }
    }



    DividerTextExpandedWith(text = "课程安排") {
        Box {
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
                    LoadingUI()
                }
            }
            AnimatedVisibility(
                visible = !loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                var total = 0.0
                LazyColumn {
                    items(listOne.size) {item ->
                        total += listOne[item].requiedCredits ?: 0.0
//                        MyCustomCard {
                            StyleCardListItem(
                                headlineContent = { Text(text = listOne[item].type + " | 学分要求 " + listOne[item].requiedCredits) },
                                trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                                //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                                modifier = Modifier.clickable {
                                    showBottomSheet_Program = true
                                    num = item
                                    title = listOne[item].type.toString()
                                },
                            )
//                        }
                    }

                    item {
                        val scrollState = rememberScrollState()
                        val text = getPersonInfo().program
                        LaunchedEffect(key1 = text ) {
                            delay(500L)
                            scrollState.animateScrollTo(scrollState.maxValue)
                            delay(4000L)
                            scrollState.animateScrollTo(0)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            text?.let { Text(
                                text = it,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.horizontalScroll(scrollState)
                            ) }
                        }
                    }
                    item { BottomTip(str = "总修 $total 学分") }
                }
            }


        }
    }



    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program),
//            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(title) },
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo(num,vm, ifSaved)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo(num : Int, vm : NetWorkViewModel, ifSaved : Boolean) {
    val listTwo = getProgramListTwo(num,vm,ifSaved)
    var show by remember { mutableStateOf(true) }
    val sheetState_Program = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("培养方案") }
    var num2 by remember { mutableStateOf(0) }
    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program,
            shape = Round(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text(title) }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo2(num,num2,vm, ifSaved)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
    listTwo.forEach{ item->
        show = item.type != "直接跳转"
    }
    if(show) {
        LazyColumn {
            items(listTwo.size) {item ->
//                MyCustomCard{
                    StyleCardListItem(
                        headlineContent = { Text(text = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits) },
                        trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                        //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                        modifier = Modifier.clickable {
                            showBottomSheet_Program = true
                            num2 = item
                            title = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits
                        },
                    )
//                }
            }
        }
    } else {
       ProgramUIInfo2(num,num2,vm, ifSaved)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo2(num1 : Int, num2 : Int, vm : NetWorkViewModel, ifSaved : Boolean) {
    val listThree = getProgramListThree(num1,num2,vm, ifSaved)
    if(listThree.size != 0) {
        listThree.sortBy { it.term }
        var input by remember { mutableStateOf("") }

        var showBottomSheet_Search by remember { mutableStateOf(false) }
        var courseName by remember { mutableStateOf("") }
        ApiForCourseSearch(vm,courseName,null,showBottomSheet_Search) {
            showBottomSheet_Search = false
        }

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
                label = { Text("搜索课程" ) },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = {}) {
                        Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = textFiledTransplant(),
            )
        }
        val searchList = mutableListOf<ProgramPartThree>()
        listThree.forEach { item->
            if(item.name.contains(input) || input.contains(item.name)) {
                searchList.add(item)
            }
        }
        Spacer(modifier = Modifier.height(CardNormalDp()))
        LazyColumn {
            items(searchList.size) {item ->
                val listItem = searchList[item]
                val name = listItem.name
//                MyCustomCard{
                    var department = listItem.depart
                    if(department.contains("（")) department = department.substringBefore("（")
                    StyleCardListItem(
                        headlineContent = { Text(text = name) },
                        supportingContent = { Text(text = department) },
                        overlineContent = { Text(text = "第" + listItem.term + "学期 | 学分 ${listItem.credit}")},
                        leadingContent = { DepartmentIcons(name = listItem.depart) },
                        modifier = Modifier.clickable {
                            if(!ifSaved) {
                                courseName = name
                                showBottomSheet_Search = true
                            } else {
                                MyToast("登录教务后可查询开课")
                                Starter.refreshLogin()
                            }
                        },
                    )
//                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                statusUI(R.drawable.manga,"选修课不做显示")
            }
        }
    }
}

@Composable
fun ProgramTips() {
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text("选修课不显示") },
            supportingContent = { Text("选修课多而杂,没必要都显示在培养方案里,按时按通知选课即可") },
        )
//    }
//    MyCustomCard {
        StyleCardListItem(
            headlineContent = { Text("查询全校其他专业培养方案") },
            supportingContent = { Text("请前往 合工大教务 公众号") },
        )
//    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestProgram(vm: NetWorkViewModel) {
    val sheetState_search = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_search by remember { mutableStateOf(false) }


    if (showBottomSheet_search) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_search = false },
            sheetState = sheetState_search,
            shape = Round(sheetState_search)
        ) {
            ProgramSearch(vm)
        }
    }
    ListItem(
        headlineContent = { Text(text = "全校培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet_search = true
        }
    )
}