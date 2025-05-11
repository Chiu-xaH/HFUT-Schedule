package com.hfut.schedule.ui.screen.home.search.function.program

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.PlanCoursesSearch
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.RequireInfo
import com.hfut.schedule.logic.model.jxglstu.Type
import com.hfut.schedule.logic.util.network.SimpleUiState
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.StatusUI
import com.hfut.schedule.ui.component.appHorizontalDp
import com.hfut.schedule.ui.component.cardNormalDp
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus.HEFEI
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.RowHorizontal
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

data class ProgramListBean(
    val id : Int,
    val grade : String,
    val name : String,
    val department : String,
    val major : String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramSearch(vm : NetWorkViewModel, ifSaved: Boolean, hazeState: HazeState) {
    var campus by remember { mutableStateOf( getCampus() ) }
    val uiState by vm.programList.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.programList.clear()
        vm.getProgramList(campus)
    }
    LaunchedEffect(campus) {
        refreshNetwork()
    }

    var item by remember { mutableStateOf(ProgramListBean(0,"","培养方案详情","","")) }
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar(item.name)
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    ProgramSearchInfo(vm,item,campus, ifSaved, hazeState =hazeState )
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("全校培养方案") {
                FilledTonalButton(
                    onClick = {
                        campus = when(campus) {
                            HEFEI -> XUANCHENG
                            XUANCHENG -> HEFEI
                        }
                    },
                ) {
                    Text(
                        when(campus) {
                            HEFEI -> "合肥"
                            XUANCHENG -> "宣城"
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            var input by remember { mutableStateOf("") }
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
                    label = { Text("搜索") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.search),
                                contentDescription = "description"
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium,
                    colors = textFiledTransplant()
                )
            }
            Spacer(modifier = Modifier.height(cardNormalDp()))

            CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "若加载过长 请搭外网") {
                val programList = (uiState as SimpleUiState.Success).data
                val searchList = programList.filter {
                    it.name.contains(input) || it.department.contains(input) || it.major.contains(input) || it.grade.contains(input)
                }
                if(searchList.isNotEmpty()) {
                    LazyColumn {
                        items(searchList.size, key = { it }) { index ->
                            val data = searchList[index]
                            var department = data.department
                            val name = data.name
                            department = department.substringBefore("（")
//                            MyCustomCard {
                            AnimationCardListItem(
                                headlineContent = { Text(name) },
                                overlineContent = { Text(data.grade + "级 " + department + " " + data.major) },
                                leadingContent = { DepartmentIcons(department) },
                                modifier = Modifier.clickable {
                                    item = data
                                    showBottomSheet = true
                                },
                                index = index
                            )
//                            }
                        }
                    }
                } else {
                    val campusText = when(campus) {
                        HEFEI -> "合肥"
                        XUANCHENG -> "宣城"
                    }
                    Column {
                        StatusUI(R.drawable.manga,"需要${campusText}校区在读生贡献数据源")
                        Spacer(Modifier.height(5.dp))
                        RowHorizontal {
                            Button(
                                onClick = {
                                    Starter.startWebUrl("https://github.com/${MyApplication.GITHUB_DEVELOPER_NAME}/${MyApplication.GITHUB_REPO_NAME}/blob/main/tools/All-Programs-Get-Python/README.md")
                                }
                            ) {
                                Text("接入指南(Github)")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgramSearchInfo(vm: NetWorkViewModel, item: ProgramListBean, campus: Campus, ifSaved: Boolean, hazeState: HazeState) {
    val uiState by vm.programSearchData.state.collectAsState()
    val refreshNetwork: suspend () -> Unit = {
        vm.programSearchData.clear()
        vm.getProgramListInfo(item.id,campus)
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork, loadingText = "培养方案较大 加载中") {
        SearchProgramUI(ifSaved, hazeState =hazeState,vm)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchProgramUI(ifSaved: Boolean, hazeState: HazeState,vm: NetWorkViewModel) {
//    val sheetState_Program = rememberModalBottomSheetState()
    val uiState by vm.programSearchData.state.collectAsState()
    val bean = (uiState as SimpleUiState.Success).data
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    val listOne = getProgramListOneSearch(bean)
    var title by remember { mutableStateOf("培养方案") }
    var num by remember { mutableStateOf(0) }


    var total = 0.0
    LazyColumn {
        items(listOne.size, key = { it }) {item ->
            total += listOne[item].requiedCredits ?: 0.0
//            MyCustomCard {
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
//            }
        }

        item { BottomTip(str = "总修 $total 学分") }
    }

    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
//            sheetState = sheetState_Program,
//            shape = bottomSheetRound(sheetState_Program),
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Program
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
                    SearchProgramUIInfo(num,bean, ifSaved, hazeState =hazeState,vm )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchProgramUIInfo(num : Int, bean: ProgramSearchItemBean, ifSaved: Boolean, hazeState: HazeState,vm : NetWorkViewModel) {
    val listTwo = getProgramListTwoSearch(num,bean)
    var show by remember { mutableStateOf(true) }
    val sheetState_Program = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("培养方案") }
    var num2 by remember { mutableIntStateOf(0) }
    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            showBottomSheet = showBottomSheet_Program,
            hazeState = hazeState
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
                    SearchProgramUIInfo2(num,num2,bean, ifSaved, hazeState =hazeState ,vm)
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
        SearchProgramUIInfo2(num,num2,bean, ifSaved,hazeState,vm)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchProgramUIInfo2(num1 : Int, num2 : Int, bean: ProgramSearchItemBean, ifSaved : Boolean, hazeState: HazeState,vm: NetWorkViewModel) {
    val listThree = getProgramListThreeSearch(num1,num2,bean)

    var showBottomSheet_Search by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    ApiForCourseSearch(vm,courseName,null,showBottomSheet_Search, hazeState = hazeState) {
        showBottomSheet_Search = false
    }

    var courseInfo by remember { mutableStateOf<ProgramPartThree?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    if(showInfo && courseInfo != null) {
        ProgramInfo(courseInfo = courseInfo!!,vm, hazeState, ifSaved){ showInfo = false }
    }

    if(listThree.size != 0) {
        listThree.sortBy { it.term }
        var input by remember { mutableStateOf("") }

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
        val searchList = mutableListOf<ProgramPartThreeSearch>()
        listThree.forEach { item->
            if(item.name.contains(input) || input.contains(item.name)) {
                searchList.add(item)
            }
        }
        Spacer(modifier = Modifier.height(cardNormalDp()))
        LazyColumn {
            items(searchList.size, key = { it }) {item ->
//                MyCustomCard{
                var department = searchList[item].depart
                if(department.contains("（")) department = department.substringBefore("（")
                val name = searchList[item].name
                AnimationCardListItem(
                        headlineContent = { Text(text = name) },
                        supportingContent = { Text(text = department) },
                        overlineContent = { Text(text = "第" + searchList[item].term + "学期 | 学分 ${searchList[item].credit}")},
                        leadingContent = { DepartmentIcons(name = searchList[item].depart) },
                        modifier = Modifier.clickable {
                            if(!ifSaved) {
                                courseName = name
                                showBottomSheet_Search = true
                            } else {
                                showToast("登录教务后可查询开课")
                                Starter.refreshLogin()
                            }
                        },
                    index = item
                    )
//                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
//            Column(modifier = Modifier.align(Alignment.Center)) {
                StatusUI(R.drawable.manga,"选修课不做显示")
//            }
        }
    }
}


data class ProgramSearchItem(
    val data : ProgramSearchItemBean
)
data class ProgramSearchItemBean(
    val requireInfo : SearchRequireInfo,
    val children : List<ProgramResponseSearch>
)
data class SearchRequireInfo(
    val requiredSubModuleNum : Double,
    val requiredCredits : Double,
    val requiredCourseNum : Double
)

//fun getProgramSearchItem(vm: NetWorkViewModel) : ProgramSearchItemBean? {
//    val json = vm.programSearchData.value
//    try {
//        val data = Gson().fromJson(json,ProgramSearchItem::class.java).data
//        return data
//    } catch (e : Exception) {
//        return null
//    }
//}
data class ProgramResponseSearch(
                           val type : Type?,

                           val requireInfo : RequireInfo?,
                           val planCourses : List<PlanCoursesSearch>,
    val children : List<ProgramResponseSearch>,
                           val remark : String?,
    )


data class ProgramPartOneSearch(val type : String?,
                          val requiedCredits : Double?,
                          val partCourse : List<PlanCoursesSearch>,

                                val children : List<ProgramResponseSearch>, )
data class ProgramPartThreeSearch(val term : Int?,
                            val name : String,
                            val credit : Double?,
                            val depart :String)

data class ProgramPartTwoSearch(val type : String?,
                          val requiedCredits : Double?,
                          val part : List<PlanCoursesSearch>,
                                //方向
    val children : List<ProgramResponseSearch>,)

fun getProgramListOneSearch(bean : ProgramSearchItemBean): MutableList<ProgramPartOneSearch> {
    val list = mutableListOf<ProgramPartOneSearch>()
    return try {
        val children = bean.children

        for(i in children.indices) {
            val type = children[i].type?.nameZh
            val requiedCredits = children[i].requireInfo?.requiredCredits
            val partCourse = children[i].planCourses
            val child = children[i].children
            list.add(ProgramPartOneSearch(type, requiedCredits,partCourse,child))
        }
        list
    } catch (e : Exception) {
        e.printStackTrace()
//        Log.d("eeee",e.toString())
        list
    }
}

fun getProgramListTwoSearch(item : Int, bean: ProgramSearchItemBean): MutableList<ProgramPartTwoSearch> {
    val list = mutableListOf<ProgramPartTwoSearch>()
    try {
        val partl = getProgramListOneSearch(bean)[item]
//        val part = partl.partCourse

        if(partl.children.isEmpty()) {
            val part = partl.partCourse
            list.add(ProgramPartTwoSearch("直接跳转",null, part, emptyList()))
            return list
        } else {
            val part = partl.children
            for(i in part.indices) {
                val partTwo = part[i]
                val type = partTwo.type?.nameZh
                val requiredCredits = partTwo.requireInfo?.requiredCredits
                val course = partTwo.planCourses
                val driections = partTwo.children
                ProgramPartTwoSearch(type,requiredCredits, course,driections).let { list.add(it) }
            }
            return list
        }

//        for(i in part.indices) {
//            val term = part[i].terms[0].substringAfter("_").toIntOrNull() ?: 0
//            val course = part[i].course
//            val courseName = course.nameZh
//            val credit = course.credits
//            val depart = part[i].openDepartment.nameZh
//            list.add(ProgramPartThreeSearch(term,courseName,credit,depart?:""))
//        }
//        return list
    } catch (e : Exception) {
        e.printStackTrace()
        return list
    }
}


fun getProgramListDirectionSearch(children: List<ProgramResponseSearch>): MutableList<ProgramPartOneSearch> {
    val list = mutableListOf<ProgramPartOneSearch>()
    return try {
        for(i in children.indices) {
            val type = children[i].type?.nameZh + " " + children[i].remark
            val requiedCredits = children[i].requireInfo?.requiredCredits
            val partCourse = children[i].planCourses
            val child = children[i].children
            list.add(ProgramPartOneSearch(type, requiedCredits,partCourse,child))
        }
        list
    } catch (e : Exception) {
        e.printStackTrace()
//        Log.d("eeee",e.toString())
        list
    }
}


fun getProgramListThreeSearch(item1 : Int, item2 : Int, bean: ProgramSearchItemBean): MutableList<ProgramPartThreeSearch> {
    val list = mutableListOf<ProgramPartThreeSearch>()
    try {
        val partl = getProgramListTwoSearch(item1,bean)[item2]
//        val partChilren = partl
        val part = partl.part

        for(i in part.indices) {

            val term = part[i].terms[0].substringAfter("_").toIntOrNull() ?: 0
            val course = part[i].course
            val courseName = course.nameZh
            val credit = course.credits
            val depart = part[i].openDepartment.nameZh
            list.add(ProgramPartThreeSearch(term,courseName,credit,depart?:""))
        }
        return list
    } catch (e : Exception) {
        e.printStackTrace()
        return list
    }
}

