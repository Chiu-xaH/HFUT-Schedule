package com.hfut.schedule.ui.screen.home.calendar.communtiy

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.network.repo.hfut.JxglstuRepository
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.getTotalCourse
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.transition.component.TopBarNavigateIcon
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfos(sheet : courseDetailDTOList, isFriend : Boolean = false, vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_totalCourse by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    if (showBottomSheet_totalCourse) {
        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_totalCourse = false
            },
            showBottomSheet = showBottomSheet_totalCourse,
            hazeState = hazeState
        ) {
            CourseDetailApi(courseName = courseName, vm = vm, hazeState = hazeState)
        }
    }

    LazyColumn {
        item{
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Column {
                    CustomCard( color = cardNormalColor()) {
                        TransplantListItem(
                            headlineContent = { sheet.place?.let { Text(it) } },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.near_me),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.classTime ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.schedule),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.teacher) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.person),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text(sheet.campus_dictText ) },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.local_library),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                        TransplantListItem(
                            headlineContent = { Text("周 ${sheet.week} 第 ${sheet.section} 节" ) },
                            supportingContent = { Text(text = "周数 ${sheet.weekCount.toString().replace("[","").replace("]","")} ")},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.calendar),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                    if(!isFriend)
                        CardListItem(
                            headlineContent = { Text( "更多信息") },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {
                                courseName = sheet.name
                                showBottomSheet_totalCourse = true
                            }
                        )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailApiScreen(
    courseName : String,
    id : String,
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val hazeState = remember { HazeState() }
    val numItem by produceState<lessons?>(initialValue = null) {
        val json = LargeStringDataManager.read(
            LargeStringDataManager.getTotalCoursesKey(
                SemesterParser.getSemester()
            )
        ) ?: return@produceState
        val list = withContext(Dispatchers.Default) {
            getTotalCourse(json)
        }
        value = list.find { it.course.nameZh == courseName }
    }
    val courseBookData : Map<Long, CourseBookBean> by produceState(initialValue = emptyMap()) {
        val json = LargeStringDataManager.read(LargeStringDataManager.BOOK_INFO) ?: return@produceState
        value = JxglstuRepository.parseCourseBook(json)
    }

    val route = remember { AppNavRoute.CourseDetail.withArgs(courseName,id) }
    CustomTransitionScaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        route = route,
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(courseName) },
                colors = topBarTransplantColor(),
                navigationIcon = {
                    TopBarNavigateIcon(navController)
                }
            )
        }
    ) { innerPadding ->
        if(numItem == null) {
            CenterScreen {
                EmptyIcon("未找到本门课的信息(尝试切换到这门课所在的学期后再刷新登陆状态)")
            }
            return@CustomTransitionScaffold
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            DetailItems(numItem!!, vm, hazeState =hazeState,courseBookData  )
        }
    }
}
//根据课程名跨接口查找唯一课程信息
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailApi(courseName : String, vm : NetWorkViewModel, hazeState: HazeState) {
    val numItem by produceState<lessons?>(initialValue = null) {
        val json = LargeStringDataManager.read(
            LargeStringDataManager.getTotalCoursesKey(
                SemesterParser.getSemester()
            )
        ) ?: return@produceState
        val list = withContext(Dispatchers.Default) {
            getTotalCourse(json)
        }
        value = list.find { it.course.nameZh == courseName }
    }
    val courseBookData : Map<Long, CourseBookBean> by produceState(initialValue = emptyMap()) {
        val json = LargeStringDataManager.read(LargeStringDataManager.BOOK_INFO) ?: return@produceState
        value = JxglstuRepository.parseCourseBook(json)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar(courseName)
        },
    ) { innerPadding ->
        if(numItem == null) {
            CenterScreen {
                EmptyIcon("未找到本门课的信息(尝试切换到这门课所在的学期后再刷新登陆状态)")
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            DetailItems(numItem!!, vm, hazeState =hazeState,courseBookData )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}