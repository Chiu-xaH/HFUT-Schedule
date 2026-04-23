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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.community.courseDetailDTOList
import com.hfut.schedule.logic.model.jxglstu.CourseBookBean
import com.hfut.schedule.logic.model.jxglstu.CourseSearchResponse
import com.hfut.schedule.logic.model.jxglstu.lessonResponse
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.logic.network.repo.JxglstuRepository
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.nav.destination.CourseDetailApiDestination
import com.hfut.schedule.ui.screen.home.calendar.multi.CourseType
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.DetailItems
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.style.align.CenterScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.navigation.util.LocalNavController
import com.xah.shared.LogUtil
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfos(sheet : courseDetailDTOList, isFriend : Boolean = false) {
    val navController = LocalNavController.current

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
                                    painterResource(R.drawable.arrow_forward),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {
                                navController.push(CourseDetailApiDestination(sheet.name, CourseType.COMMUNITY.name,sheet.place))
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
    classroom : String?,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)

    // fixme:改成哈希表性能优化
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
        val json = LargeStringDataManager.read(LargeStringDataManager.getBookKey(SemesterParser.getSemester())) ?: return@produceState
        value = withContext(Dispatchers.Default) {
            JxglstuRepository.parseCourseBook(json)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.topBarBlur(hazeState),
                scrollBehavior = scrollBehavior,
                title = { Text(courseName) },
                colors = topBarTransplantColor(),
                navigationIcon = {
                    TopBarNavigationIcon()
                }
            )
        }
    ) { innerPadding ->
        if(numItem == null) {
            CenterScreen {
                EmptyIcon("未找到本门课的信息(尝试切换到这门课所在的学期后再刷新登陆状态)")
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
        ){
            DetailItems(innerPadding,numItem!!,courseBookData ,classroom )
        }
    }
}


private fun getTotalCourse(json : String?): MutableList<lessons>  {
    val list = mutableListOf<lessons>()

    try {
        if (json != null) {
            if(json.contains("lessonIds")) {
                val result = Gson().fromJson(json,lessonResponse::class.java).lessons
                return result.toMutableList()
            }
            else {
                val result = Gson().fromJson(json,CourseSearchResponse::class.java).data
                for (i in result.indices) {
                    val courses = result[i].lesson
                    list.add(courses)
                }
                return list
            }
        } else return list
    } catch (e : Exception) {
        LogUtil.error(e)
        return list
    }
}
