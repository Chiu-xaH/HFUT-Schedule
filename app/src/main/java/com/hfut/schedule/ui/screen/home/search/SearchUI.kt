package com.hfut.schedule.ui.screen.home.search

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.AppNavRoute
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.search.function.huiXin.Huixin
import com.hfut.schedule.ui.screen.home.search.function.community.bus.SchoolBus
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.SchoolCardItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CoursesSearch
import com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore.DormitoryScoreXuanCheng
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.Electric
import com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom.EmptyRoom
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.Exam
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRate
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade.Grade
import com.hfut.schedule.ui.screen.home.search.function.huiXin.hotWater.HotWater
import com.hfut.schedule.ui.screen.home.search.function.school.ietp.IETP
import com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao.LePaoYun
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryItem
import com.hfut.schedule.ui.screen.home.search.function.other.life.Life
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWeb
import com.hfut.schedule.ui.screen.home.search.function.one.mail.Mail
import com.hfut.schedule.ui.screen.home.search.function.school.news.News
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse.NextCourse
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsCenter
import com.hfut.schedule.ui.screen.home.search.function.one.pay.Pay
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.Program
import com.hfut.schedule.ui.screen.home.search.function.school.repair.Repair
import com.hfut.schedule.ui.screen.home.search.function.my.schoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.WeChatGo
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourse
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.Shower
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.Survey
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.TeacherSearch
import com.hfut.schedule.ui.screen.home.search.function.school.student.ToadyCampus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotal
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Transfer
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.practice.Practice
import com.hfut.schedule.ui.screen.home.search.function.school.work.Work
import com.hfut.schedule.ui.screen.home.search.function.other.xueXin.XueXin
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.component.mixedCardNormalColor
import com.hfut.schedule.ui.screen.home.search.function.community.termInfo.TermInfo
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.Holiday
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.Washing
import com.hfut.schedule.ui.screen.home.search.function.my.supabase.Supabase
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.AppAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.TransitionState
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay

private data class SearchAppBean(val searchKeyWord : String,val ui : @Composable () -> Unit,val route : String? = null)
@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun SearchScreen(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
    innerPaddings : PaddingValues,
    vmUI : UIViewModel,
    input : String,
    navController : NavHostController,
    hazeState: HazeState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    //建立索引 <搜索关键词,功能>
    val funcMaps : List<SearchAppBean>  = listOf(
        SearchAppBean("一卡通 校园卡 账单 充值 缴费 慧新易校" , { SchoolCardItem(vmUI, true) }),
        SearchAppBean("考试" , { Exam(vm, ifSaved,hazeState) }),
        SearchAppBean("寝室电费 缴费 慧新易校" , { Electric(vm, false, vmUI,hazeState) }),
        SearchAppBean("校园网 慧新易校 缴费" , { LoginWeb(vmUI, false, vm,hazeState) }),
        SearchAppBean("教育邮箱" , { Mail(ifSaved, vm,vmUI,hazeState) }),
        SearchAppBean("一卡通 校园卡 账单 充值 缴费 慧新易校 合肥" , { Huixin() }),
        SearchAppBean("成绩", { Grade(ifSaved,navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Grade.receiveRoute()),
        SearchAppBean("挂科率", { FailRate(vm,hazeState) }),
        SearchAppBean("课程汇总 教材 课本", { CourseTotal(vm,hazeState,ifSaved) }),
        SearchAppBean("个人信息", { PersonUI(vm,hazeState) }),
        SearchAppBean("网址导航 实验室 收纳", { WebUI(hazeState) }),
        SearchAppBean("洗浴 洗澡 呱呱物联 慧新易校 缴费", { Shower(vm,hazeState) }),
        SearchAppBean("选课", { SelectCourse(ifSaved, vm,hazeState,vmUI) }),
        SearchAppBean("寝室卫生评分 寝室卫生分数", { DormitoryScoreXuanCheng(vm,hazeState) }),
        SearchAppBean("消息中心 通知中心 收纳", { NotificationsCenter(hazeState) }),
        SearchAppBean("教师评教 教师教评", { Survey(ifSaved, vm,hazeState) }),
        SearchAppBean("通知公告 新闻 教务处", { News() }),
        SearchAppBean("培养方案", { Program(vm, ifSaved,hazeState) }),
        SearchAppBean("图书馆 座位预约 借阅", { LibraryItem(vm,hazeState) }),
        SearchAppBean("校车", { SchoolBus() }),
        SearchAppBean("报修 维修 后勤", { Repair(hazeState) }),
        SearchAppBean("下学期课程表 下学期课表", { NextCourse(ifSaved, vmUI,vm, hazeState) }),
        SearchAppBean("热水机 趣智校园", { HotWater() }),
        SearchAppBean("空教室", { EmptyRoom(vm, ifSaved,hazeState) }),
        SearchAppBean("乐跑云运动 校园跑 体测 体育测试 体育平台 体检", { LePaoYun(hazeState) }),
        SearchAppBean("校历", { SchoolCalendar() }),
        SearchAppBean("学信网", { XueXin() }),
        SearchAppBean("生活服务 校园 校园 天气 教学楼 建筑 学堂", { Life(vm,hazeState) }),
        SearchAppBean("转专业", { Transfer(ifSaved, vm,hazeState) }),
        SearchAppBean("开课查询 全校开课", { CoursesSearch(ifSaved, vm,hazeState) }),
        SearchAppBean("教师 老师", { TeacherSearch(vm,hazeState) }),
        SearchAppBean("学费 费用 欠缴学费", { Pay(vm,hazeState) }),
        SearchAppBean("实习", { Practice(ifSaved) }),
        SearchAppBean("微信专区 校友 毕业 第二课堂 今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常 空教室 教务 同班同学 快递 取件码 团员 团建", { WeChatGo(hazeState) }),
        SearchAppBean("今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常", { ToadyCampus(hazeState,vm) }),
        SearchAppBean("大创 大学生创新创业", { IETP() }),
        SearchAppBean("就业 实习 春招 双选 秋招", { Work(hazeState,vm) }),
        SearchAppBean("国家法定节假日 假期 节日", { Holiday(hazeState) }),
        SearchAppBean("云端共建平台 信息共建 日程 网课 网址导航", { Supabase(vm) }),
        SearchAppBean("洗衣机 洗鞋机 烘干机 慧新易校 海乐生活 缴费", { Washing(vm,hazeState) }),
        SearchAppBean("作息", { TermInfo(hazeState) }),
    )

    var filteredList = funcMaps
    LaunchedEffect(input) {
        if(input != "") {
            filteredList = funcMaps.filter {
                it.searchKeyWord.contains(input, ignoreCase = true)
            }
        }
//        else {
//            filteredList = funcMaps
//        }
    }

    
    val paddingModifier = remember { Modifier.padding(horizontal = 3.dp, vertical = 3.dp) }

    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 12.dp)) {
        items(2) {
            Column {
                Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        items(filteredList.size) { index->
            val item = filteredList[index]
            with(sharedTransitionScope) {
                SmallCard(
                    modifier = item.route?.let { containerShare(paddingModifier,animatedContentScope,it) } ?: paddingModifier,
                    color = mixedCardNormalColor()
                ) {
                    item.ui()
                }
            }
        }
        items( 2) { Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding())) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFuncs(ifSaved: Boolean,input: String,webVpn: Boolean = false, onInputChanged: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.Center
    ) {

        TextField(
            modifier = Modifier
                .weight(1f),
            value = input,
            onValueChange = onInputChanged,
            label = { Text("搜索功能" ) },
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            colors = textFiledTransplant(),
            trailingIcon = {
                if(ifSaved) {
                    TextButton(onClick = { refreshLogin() }) {
                        Icon(painter = painterResource(id =  R.drawable.login), contentDescription = "")
                    }
                } else {
                    TextButton(onClick = { }) {
                        Text(text = if(webVpn) "WEBVPN" else "已登录", color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TopBarTopIcon(
    navController : NavHostController,
    animatedContentScope: AnimatedContentScope,
    route : String,
    icon : Int
) {
    val speed = TransitionState.curveStyle.speedMs + TransitionState.curveStyle.speedMs/2
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        show = true
        delay(speed*1L)
        delay(1000L)
        show = false
        delay(3000L)
        show = true
    }

    IconButton(onClick = { navController.popBackStack() }) {
        Box() {
            AnimatedVisibility(
                visible = show,
                enter = AppAnimationManager.centerAnimation.enter,
                exit = AppAnimationManager.centerAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
            AnimatedVisibility(
                visible = !show,
                enter = AppAnimationManager.centerAnimation.enter,
                exit = AppAnimationManager.centerAnimation.exit
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopBarTopIconTip(
    icon : Int
) {
    val speed = TransitionState.curveStyle.speedMs + TransitionState.curveStyle.speedMs/2
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        show = true
        delay(speed*1L)
        delay(1000L)
        show = false
        delay(3000L)
        show = true
    }
    IconButton(onClick = { showToast("返回上一级界面") }) {
        Box() {
            AnimatedVisibility(
                visible = show,
                enter = AppAnimationManager.centerAnimation.enter,
                exit = AppAnimationManager.centerAnimation.exit
            ) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            AnimatedVisibility(
                visible = !show,
                enter = AppAnimationManager.centerAnimation.enter,
                exit = AppAnimationManager.centerAnimation.exit
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}