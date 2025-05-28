package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramShow
import com.hfut.schedule.logic.model.jxglstu.item
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.sys.ClipBoard
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.other.life.countFunc
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.StyleCardListItem
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    val sheetState_Program = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val sheetState_search = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_search by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            FilledTonalIconButton(
                onClick = {
                    showBottomSheet_search = true
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                showBottomSheet_Program = true
            }
            else refreshLogin()
        }
    )



    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            showBottomSheet = showBottomSheet_search,
            hazeState = hazeState,
//            sheetState = sheetState_search,
//            shape = bottomSheetRound(sheetState_search)
        ) {
            ProgramSearch(vm,ifSaved,hazeState)
        }
    }


    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Program
//            sheetState = sheetState_Program,
//            shape = bottomSheetRound(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("培养方案") {
                        Row (){
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
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUI2(vm,ifSaved,hazeState)
                    //ProgramPerformance(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun ProgramUI(vm: NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState) {
  //  val list =
        val json = prefs.getString("programJSON","")
       // try {
            val listType = object : TypeToken<List<ProgramShow>>() {}.type
            val list: List<ProgramShow> = try {
                Gson().fromJson(json,listType)
            } catch (_:Exception) { emptyList() }
      //  }

    var sum = 0.0
    for(i in list.indices) {
        val credits = list[i].credit
        if (credits != null) { sum += credits }
    }
    var showBottomSheet_Search by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    ApiForCourseSearch(vm,courseName,null,showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
    }
    LazyColumn {
        item {
//            MyCustomCard{
                AnimationCardListItem(
                    headlineContent = { Text(text = "合计 ${list.size} 门 $sum 学分") },
                    supportingContent = { Text(text = "不含选修课!")},
                 //   overlineContent = { },
                 //   trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    color = MaterialTheme.colorScheme.primaryContainer,
                    leadingContent = { Icon(painterResource(id = R.drawable.conversion_path), contentDescription = "Localized description") },
                    index = 0
                )
//            }
        }
        items(list.size, key = { it }) { item ->
            val name = list[item].name
//            MyCustomCard{
                AnimationCardListItem(
                    headlineContent = { Text(text = name) },
                    supportingContent = { Text(text = list[item].school + "  第" + list[item].term[0] + "学期")},
                    overlineContent = { list[item].type?.let { Text(text = it) } },
                    trailingContent = { Text(text = "学分 " +  list[item].credit)},
                    leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                    modifier = Modifier.clickable {
                        courseName = name
                        showBottomSheet_Search = true
                    },
                    index = item
                )
//            }
        }
    }
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUI2(vm: NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState) {

    var showBottomSheet_Performance by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }
    val cookie = if (!vm.webVpn) prefs.getString(
        "redirect",
        ""
    ) else "wengine_vpn_ticketwebvpn_hfut_edu_cn=" + prefs.getString("webVpnTicket", "")


    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val listOne = getProgramListOne(vm,ifSaved)
    var title by remember { mutableStateOf("培养方案") }
    var num by remember { mutableStateOf(0) }
//    var loadingCard by remember { mutableStateOf(true) }
    val uiState by vm.programCompletionData.state.collectAsState()
    var loadingCard = uiState !is UiState.Success
    val scale2 = animateFloatAsState(
        targetValue = if (loadingCard) 0.97f else 1f, // 按下时为0.9，松开时为1
        animationSpec = tween(MyApplication.ANIMATION_SPEED / 2, easing = LinearOutSlowInEasing),
        label = "" // 使用弹簧动画
    )



    var completion by remember { mutableStateOf(
        item("培养方案课程",0.0,0.0).let { nilItem -> ProgramCompletionResponse(nilItem, listOf(nilItem,nilItem,nilItem)) }
    ) }

    LaunchedEffect(Unit) {
        if(!ifSaved) {
            cookie?.let {
                vm.programCompletionData.clear()
                vm.getProgramCompletion(it)
            }
        }
    }

    if (showBottomSheet_Performance ) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet_Performance = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Performance
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("培养方案 完成情况")
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    countFunc = 0
                    ProgramPerformance(vm,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if(ifSaved) {
        loading = false
        refresh = false
//        loadingCard = true
    } else {
        if(refresh) {
            loading = true
//            loadingCard = true
//            CoroutineScope(Job()).launch{
//                async{ cookie?.let { vm.getProgramCompletion(it)} }.await()
//                async {
//                    Handler(Looper.getMainLooper()).post{
//                        vm.ProgramCompletionData.observeForever { result ->
//                            if (result != null) {
//                                if(result.contains("总学分")) {
//                                    loadingCard = false
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            CoroutineScope(Job()).launch{
                async{ cookie?.let { vm.getProgram(it)} }.await()
                async {
                    Handler(Looper.getMainLooper()).post{
                        vm.programData.observeForever { result ->
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
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val response = (uiState as UiState.Success).data
            response.let {
                completion = it
            }
        }
    }

    DividerTextExpandedWith(text = if(ifSaved)"完成情况(登录后可查看)" else "完成情况",openBlurAnimation = false) {
        LoadingLargeCard(
            title = "已修 ${completion.total.actual}/${completion.total.full}",
            rightTop = {
                val res = completion.total.actual/completion.total.full * 100.0
                Text(text = "${formatDecimal(res,1)} %")
            },
            loading = loadingCard
        ) {
            for(i in 0 until completion.other.size step 2)
                Row {
                    TransplantListItem(
                        headlineContent = { Text(text = completion.other[i].name) },
                        overlineContent = {Text(text = "${completion.other[i].actual}/${completion.other[i].full}", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(.5f)
                    )
                    if(i+1 < completion.other.size)
                        TransplantListItem(
                            headlineContent = { Text(text = completion.other[i+1].name) },
                            overlineContent = {Text(text = "${completion.other[i+1].actual}/${completion.other[i+1].full}",fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(.5f)
                        )
                }
        }
        Button(
            onClick = {
                if(ifSaved) refreshLogin()
                else showBottomSheet_Performance = true
            },
            modifier = Modifier
                .fillMaxWidth().scale(scale2.value)
                .padding(horizontal = appHorizontalDp(), vertical = 5.dp),
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
                    items(listOne.size, key = { it }) {item ->
                        total += listOne[item].requiedCredits ?: 0.0
//                        MyCustomCard {
                            AnimationCardListItem(
                                headlineContent = { Text(text = listOne[item].type + " | 学分要求 " + listOne[item].requiedCredits) },
                                trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                                //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                                modifier = Modifier.clickable {
                                    showBottomSheet_Program = true
                                    num = item
                                    title = listOne[item].type.toString()
                                },
                                index = item
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
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            showBottomSheet = showBottomSheet_Program,
            hazeState = hazeState
//            sheetState = sheetState_Program,
//            shape = bottomSheetRound(sheetState_Program),
//            modifier = Modifier.nestedScroll(nestedScrollConnection)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(title)
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo(num,vm, ifSaved, hazeState =hazeState )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo(num : Int, vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    val listTwo = getProgramListTwo(num,vm,ifSaved)
    var show by remember { mutableStateOf(true) }
    val sheetState_Program = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("培养方案") }
    var num2 by remember { mutableStateOf(0) }
    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Program
//            sheetState = sheetState_Program,
//            shape = bottomSheetRound(sheetState_Program)
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(title)
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramUIInfo2(num,num2,vm, ifSaved, hazeState = hazeState)
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
            items(listTwo.size, key = { it }) {item ->
//                MyCustomCard{
                    AnimationCardListItem(
                        headlineContent = { Text(text = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits) },
                        trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "")},
                        //   leadingContent = { Icon(painterResource(id = R.drawable.calendar), contentDescription = "Localized description") },
                        modifier = Modifier.clickable {
                            showBottomSheet_Program = true
                            num2 = item
                            title = listTwo[item].type + " | 学分要求 " + listTwo[item].requiedCredits
                        },
                        index = item
                    )
//                }
            }
        }
    } else {
       ProgramUIInfo2(num,num2,vm, ifSaved,hazeState)
    }
}
@Composable
fun ProgramInfo(courseInfo : ProgramPartThree,vm: NetWorkViewModel,hazeState: HazeState,ifSaved: Boolean,onDismissRequest : () -> Unit) {
    var showBottomSheet_Search by remember { mutableStateOf(false) }

    ApiForCourseSearch(vm,null, courseInfo.code,showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
    }
    HazeBottomSheet(
        showBottomSheet = true,
        isFullExpand = true,
        autoShape = false,
        hazeState = hazeState,
        onDismissRequest = onDismissRequest
    ){
        Column(modifier = Modifier.navigationBarsPadding()) {
            HazeBottomSheetTopBar(courseInfo.name , isPaddingStatusBar = false) {
                FilledTonalButton(
                    onClick = {
                        if(!ifSaved) {
                            showBottomSheet_Search = true
                        } else {
                            showToast("登录教务后可查询开课")
                            refreshLogin()
                        }
                    }
                ) {
                    Text("开课查询")
                }
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text(courseInfo.courseType) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.hotel_class),null)
                    },
                    overlineContent = { Text("类型") },
                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { Text(if(courseInfo.isCompulsory) "必修" else "选修") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.check),null)
                    },
                    overlineContent = { Text("真实选修性") },
                    modifier = Modifier.weight(.5f)
                )
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text(courseInfo!!.credit.toString()) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.filter_vintage),null)
                    },
                    overlineContent = { Text("学分") },

                    modifier = Modifier.weight(.5f)
                )
                courseInfo!!.code.let {
                    TransplantListItem(
                        headlineContent = { Text(it) },
                        leadingContent = {
                            Icon(painterResource(R.drawable.tag),null)
                        },
                        overlineContent = { Text("课程代码") },
                        modifier = Modifier.weight(.5f).clickable {
                            ClipBoard.copy(it)
                        }
                    )
                }
            }
            Row {
                TransplantListItem(
                    headlineContent = { Text("第" + courseInfo!!.term +"学期") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.schedule),null)
                    },
                    overlineContent = { Text("类型") },

                    modifier = Modifier.weight(.5f)
                )
                TransplantListItem(
                    headlineContent = { Text("共"+courseInfo!!.week+"周") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.calendar),null)
                    },
                    overlineContent = { Text("上课周数") },

                    modifier = Modifier.weight(.5f)
                )
            }

            courseInfo.depart.let {
                TransplantListItem(
                    headlineContent = { Text(it) },
                    leadingContent = {
                        DepartmentIcons(it)
                    },
                    overlineContent = { Text("开设学院") },
                )
            }
            courseInfo.remark?.let {
                TransplantListItem(
                    headlineContent = { Text(it) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.info),null)
                    },
                    overlineContent = { Text("备注") },
                )
            }
            Spacer(Modifier.height(appHorizontalDp()))
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramUIInfo2(num1 : Int, num2 : Int, vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    val listThree = getProgramListThree(num1,num2,vm, ifSaved)
    if(listThree.size != 0) {
        listThree.sortBy { it.term }
        var input by remember { mutableStateOf("") }

        var courseInfo by remember { mutableStateOf<ProgramPartThree?>(null) }
        var showInfo by remember { mutableStateOf(false) }
        if(showInfo && courseInfo != null) {
            ProgramInfo(courseInfo = courseInfo!!,vm, hazeState, ifSaved){ showInfo = false }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = appHorizontalDp()),
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
        Spacer(modifier = Modifier.height(cardNormalDp()))
        LazyColumn {
            items(searchList.size, key = { it }) {item ->
                val listItem = searchList[item]
                val name = listItem.name
//                MyCustomCard{
                    var department = listItem.depart
//                    if(department.contains("（")) department = department.substringBefore("（")
                    AnimationCardListItem(
                        headlineContent = { Text(text = name) },
                        supportingContent = { Text(text = department) },
                        overlineContent = { Text(text = "第" + listItem.term + "学期 | 学分 ${listItem.credit}")},
                        leadingContent = { DepartmentIcons(name = listItem.depart) },
                        trailingContent = if(!listItem.isCompulsory){{ Text("选修") }} else null,
                        modifier = Modifier.clickable {
                            courseInfo = listItem
                            showInfo = true
                        },
                        index = item
                    )
//                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                StatusUI(R.drawable.manga,"选修课不做显示")
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
fun GuestProgram(vm: NetWorkViewModel, hazeState: HazeState) {
    val sheetState_search = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_search by remember { mutableStateOf(false) }


    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_search
//            sheetState = sheetState_search,
//            shape = bottomSheetRound(sheetState_search)
        ) {
            ProgramSearch(vm, true, hazeState )
        }
    }
    TransplantListItem(
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