package com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.addToCalendars
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.calendar.jxglstu.next.CourseDetailOrigin
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAppNavController
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.align.CenterScreen
import com.xah.uicommon.style.color.topBarTransplantColor
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.xah.uicommon.util.LogUtil
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun Exam(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Exam.withArgs() }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Exam.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Exam.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Exam,route)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    navController : NavHostController,
    origin : String?
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Exam.withArgs(origin) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(stringResource(AppNavRoute.Exam.label)) },
                actions = {
                    LiquidButton(
                        modifier = Modifier
                            .containerShare(AppNavRoute.ExamNews.route, MaterialTheme.shapes.extraLarge)
                            .padding(horizontal = APP_HORIZONTAL_DP),
                        backdrop = backdrop,
                        onClick = {
                            navController.navigateForTransition(AppNavRoute.ExamNews, AppNavRoute.ExamNews.route)
                        },
                    ) {
                        Text("全校考试安排", maxLines = 1)
                    }
                },
                navigationIcon = {
                    TopBarNavigationIcon(route, AppNavRoute.Exam.icon)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            val list by produceState(initialValue = emptyList()) {
                value = getExamFromCache()
            }
            if(list.isEmpty()) {
                CenterScreen {
                    EmptyIcon()
                }
            } else {
                LazyColumn {
                    item { InnerPaddingHeight(innerPadding,true) }
                    items(list.size) { index -> JxglstuExamUI(list[index],true)}
                    item { InnerPaddingHeight(innerPadding,false) }
                }
            }
        }
    }
//    }
}

@Composable
private fun ExamItems(item : Int,status : Boolean) {

    var date = DateTimeManager.Date_yyyy_MM_dd
    val todayDate = date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10)
    val get = getExam()[item].formatEndTime
    //判断考完试不显示信息

    val examDate = (get?.substring(0,4)+ get?.substring(5, 7) ) + get?.substring(8, 10)

    val st = getExam()[item].formatStartTime

    @Composable
    fun Item() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column() {
//                MyCustomCard {
                    CardListItem(
                        headlineContent = { getExam()[item].courseName?.let { Text(text = it) } },
                        overlineContent = { Text(text = st?.substring(5,st.length - 3) + "~" + get?.substring(11,get.length-3)) },
                        supportingContent = { getExam()[item].place?.let { Text(text = it) } },
                        leadingContent = {
                            if(status) Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
                            else if(examDate.toInt() >= todayDate.toInt()) Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                            else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
                        },
                        trailingContent = {
                            if(examDate.toInt() < todayDate.toInt()) Text(text = "已结束")
                            else if(examDate.toInt() == todayDate.toInt()) Text(text = "今日")
                            else if(examDate.toInt() > todayDate.toInt()) Text(text = "待考")
                                          },
                        color =  if(examDate.toInt() >= todayDate.toInt())
                            MaterialTheme.colorScheme.errorContainer
                        else null,
                        modifier = Modifier.clickable {},
                    )
//                }
            }
        }
    }

    if(status){
        if(examDate.toInt() >= todayDate.toInt()) Item()
    } else Item()

}

//status参数判断是聚焦还是界面，若为聚焦则解析显示未考的，若为界面都显示
@Composable
fun JxglstuExamUI(item : JxglstuExam,status : Boolean) {
    //时隔一年修补这里的Bug
    val newDate = DateTimeManager.Date_yyyy_MM_dd
    val newToday = newDate.replace("-","").toLongOrNull() ?: 0
    val examDate = item.dateTime
    val examDateNum = examDate.substringBefore(" ").replace("-","").toLongOrNull() ?: 0

    val activity = LocalActivity.current

    val course = item.name
    val time = item.dateTime
    val place  = item.place

    val year = time.substringBefore("-")
    val month = time.substring(5,7)
    val day = time.substring(8,10)
    val startTimeHour = time.substringAfter(" ").substringBefore(":")
    val startTimeMinute = time.substringAfter(":").substringBefore("~")
    val endTimeHour = time.substringAfter("~").substringBefore(":")
    val endTimeMinute = time.substringAfter("~").substringAfter(":")

    if(status) {
        // 判断是否考完
//        val endTime = time?.substringBefore(" ") ?: return
        val isFinished = examDateNum < newToday
//            DateTimeUtils.compareTimeDate(endTime = endTime) == DateTimeUtils.TimeState.ENDED
        CardListItem(
            headlineContent = {  Text(text = course.toString() + (item.type?.let { "-$it" } ?: ""), textDecoration = if(isFinished) TextDecoration.LineThrough else TextDecoration.None) },
            overlineContent = { Text(text = examDate,textDecoration = if(isFinished) TextDecoration.LineThrough else TextDecoration.None) },
            supportingContent = { place?.let { Text(text = it,textDecoration = if(isFinished) TextDecoration.LineThrough else TextDecoration.None) } },
            leadingContent = {
                if(!isFinished)
                    Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
            },
            trailingContent = {
                if(isFinished) Text(text = "已结束")
                else {
                    if(examDateNum == newToday) Text("今日")
                    else {
                        Text("${DateTimeManager.daysBetween(time.substringBefore(" "))}天")
                    }
                }
            },
        )
    } else {
        if(examDateNum >= newToday) {
            //如果是今天考试，那么判断考试结束后不显示 待做
            val scope = rememberCoroutineScope()
            val navController = LocalAppNavController.current
            //今天 && 已经考完
            if(
                "$month-$day" == DateTimeManager.Date_MM_dd && DateTimeManager.compareTime("$endTimeHour:$endTimeMinute") == DateTimeManager.TimeState.ENDED) {
            } else {
                val route = AppNavRoute.Exam.withArgs(CourseDetailOrigin.FOCUS_TODAY.t + "@${item.hashCode()}")
                CardListItem(
                    headlineContent = {  Text(text = course) },
                    overlineContent = { Text(text = time.substringAfter("-")) },
                    supportingContent = { Text(text = "$place") },
                    leadingContent = {
//                                if("$month-$day" == DateTimeManager.Date_MM_dd) {
//                                    Icon(painterResource(R.drawable.warning), contentDescription = "Localized description",)
//                                } else {
//
//                                }
                        Icon(painterResource(R.drawable.draw), contentDescription = "Localized description",)
                    },
                    cardModifier = Modifier.containerShare(route, MaterialTheme.shapes.medium),
                    modifier = Modifier.clickable {
                        navController.navigateForTransition(AppNavRoute.Exam,route)
                    },
                    trailingContent = {
                        if("$month-$day" == DateTimeManager.Date_MM_dd) {
                            Text("今日")
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                FilledTonalIconButton(
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val startDateList =
                                                    listOf(year.toInt(), month.toInt(), day.toInt(), startTimeHour.toInt(), startTimeMinute.toInt())
                                                val endDateList =
                                                    listOf(
                                                        year.toInt(),
                                                        month.toInt(),
                                                        day.toInt(),
                                                        endTimeHour.toInt(),
                                                        endTimeMinute.toInt()
                                                    )
                                                activity?.let { it0 ->
                                                    course.let {
                                                        place?.let { it1 ->
                                                            addToCalendars(startDateList, endDateList, it1, it,"考试", it0, remind = true)
                                                        }
                                                    }
                                                }
                                            } catch (e : Exception) {
                                                LogUtil.error(e)
                                            }
                                        }
                                    }) {
                                    Icon(painter = painterResource(id = R.drawable.event_upcoming), contentDescription = "")
                                }
                                Text("${DateTimeManager.daysBetween(time.substringBefore(" "))}天")
                            }
                        }
                    },
                    color = MaterialTheme.colorScheme.errorContainer
                )
            }
        }
    }
}