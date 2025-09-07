package com.hfut.schedule.ui.screen.home.search

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.SEARCH_DEFAULT_STR
import com.hfut.schedule.ui.component.container.SEARCH_FUC_CARD_HEIGHT
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.community.bus.SchoolBus
import com.hfut.schedule.ui.screen.home.search.function.community.failRate.FailRate
import com.hfut.schedule.ui.screen.home.search.function.community.library.LibraryItem
import com.hfut.schedule.ui.screen.home.search.function.community.workRest.WorkAndRest
import com.hfut.schedule.ui.screen.home.search.function.huiXin.HuiXin
import com.hfut.schedule.ui.screen.home.search.function.huiXin.card.SchoolCardItem
import com.hfut.schedule.ui.screen.home.search.function.huiXin.electric.Electric
import com.hfut.schedule.ui.screen.home.search.function.huiXin.getHuiXinURL
import com.hfut.schedule.ui.screen.home.search.function.huiXin.hotWater.HotWater
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.LoginWeb
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.Shower
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.Washing
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.CoursesSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.exam.Exam
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade.Grade
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.nextCourse.NextCourse
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonUI
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.practice.Practice
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.program.Program
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse.SelectCourse
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.survey.Survey
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse.CourseTotal
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Transfer
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.Holiday
import com.hfut.schedule.ui.screen.home.search.function.my.notification.NotificationsCenter
import com.hfut.schedule.ui.screen.home.search.function.my.supabase.Supabase
import com.hfut.schedule.ui.screen.home.search.function.my.webLab.WebUI
import com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom.Classroom
import com.hfut.schedule.ui.screen.home.search.function.one.mail.Mail
import com.hfut.schedule.ui.screen.home.search.function.one.pay.Pay
import com.hfut.schedule.ui.screen.home.search.function.other.life.Life
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.WeChatGo
import com.hfut.schedule.ui.screen.home.search.function.other.xueXin.XueXin
import com.hfut.schedule.ui.screen.home.search.function.school.SecondClass
import com.hfut.schedule.ui.screen.home.search.function.school.admission.Admission
import com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore.DormitoryScoreXuanCheng
import com.hfut.schedule.ui.screen.home.search.function.school.hall.OfficeHall
import com.hfut.schedule.ui.screen.home.search.function.school.ietp.IETP
import com.hfut.schedule.ui.screen.home.search.function.school.news.News
import com.hfut.schedule.ui.screen.home.search.function.school.repair.Repair
import com.hfut.schedule.ui.screen.home.search.function.school.scan.Scan
import com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao.LePaoYun
import com.hfut.schedule.ui.screen.home.search.function.school.student.ToadyCampus
import com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch.TeacherSearch
import com.hfut.schedule.ui.screen.home.search.function.school.webvpn.WebVpn
import com.hfut.schedule.ui.screen.home.search.function.school.work.Work
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.util.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.transition.component.containerShare
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.HazeState

data class SearchAppBean(
    val id : Int,
    val searchKeyWord : String,
    val ui : @Composable () -> Unit,
    val route : String? = null,
    val isHigh : Boolean = false,
)
data class SearchAppBeanLite(
    val id : Int,
    val name : String,
    val icon : Int,
//    val route : String? = null,
//    val clickable: (() -> Unit)? = null
)



// 按 List<Int> 排序，并把未出现的新元素追加到末尾
private fun MutableList<SearchAppBean>.reorderByIds(idOrder: List<Int>): MutableList<SearchAppBean> {
    val map = this.associateBy { it.id }

    // 按顺序取出原有元素
    val sorted = idOrder.mapNotNull { map[it] }.toMutableList()

    // 追加未在 idOrder 中的新元素
    val remaining = this.filter { it.id !in idOrder }
    sorted.addAll(remaining)

    this.clear()
    this.addAll(sorted)
    return this
}

// 按字符串排序
fun MutableList<SearchAppBean>.reorderByIdsStr(idOrder: String): MutableList<SearchAppBean> {
    return try {
        val order = idOrder.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
        reorderByIds(order)
    } catch (e: Exception) {
        // 出错时恢复默认顺序
        reorderByIds(GlobalUIStateHolder.funcDefault.map { it.id })
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
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
    val searchSort by DataStoreManager.searchSort.collectAsState(initial = SEARCH_DEFAULT_STR)
    //建立索引 <搜索关键词,功能>
    var funcMaps by remember(vm, ifSaved, vmUI, navController, hazeState, sharedTransitionScope, animatedContentScope) {
        mutableStateOf(
            listOf(
                SearchAppBean(1,"一卡通 校园卡 账单 充值 缴费 慧新易校" , { SchoolCardItem(vmUI, true) }, isHigh = true),
                SearchAppBean(2,"${AppNavRoute.Scan.label} 扫码 指尖工大 CAS统一认证登录", { Scan(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Scan.route,isHigh = true),
                SearchAppBean(3,"寝室电费 缴费 慧新易校" , { Electric(vm, false, vmUI,hazeState) },isHigh = true),
                SearchAppBean(4,"校园网 慧新易校 缴费" , { LoginWeb(vmUI, false, vm,hazeState) },isHigh = true),
                SearchAppBean(5,"教育邮箱 校园邮箱" , { Mail(vm,hazeState) }),
                SearchAppBean(6,"${AppNavRoute.Exam.label}" , { Exam(ifSaved,navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Exam.route),
                SearchAppBean(7,"${AppNavRoute.Grade.label}", { Grade(ifSaved,navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Grade.receiveRoute()),
                SearchAppBean(8,"${AppNavRoute.FailRate.label}", { FailRate(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.FailRate.route),
                SearchAppBean(9,"${AppNavRoute.TotalCourse.label} 教材 课本", { CourseTotal(ifSaved,navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.TotalCourse.receiveRoute()),
                SearchAppBean(10,"${AppNavRoute.Person.label}", { PersonUI(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Person.route),
                SearchAppBean(11,"${AppNavRoute.WebNavigation.label} 实验室 收纳", { WebUI(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.WebNavigation.route),
                SearchAppBean(12,"洗浴 洗澡 呱呱物联 慧新易校 缴费", { Shower(vm,hazeState) }),
                SearchAppBean(13,"${AppNavRoute.SelectCourse.label}", { SelectCourse(ifSaved, navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.SelectCourse.route),
                SearchAppBean(14,"${AppNavRoute.DormitoryScore.label} 寝室卫生评分 寝室卫生分数", { DormitoryScoreXuanCheng(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.DormitoryScore.route),
                SearchAppBean(15,"${AppNavRoute.Notifications.label} 通知中心 收纳", { NotificationsCenter(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Notifications.route),
                SearchAppBean(16,"${AppNavRoute.Survey.label} 教师评教 教师教评", { Survey(ifSaved,navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Survey.route),
                SearchAppBean(17,"${AppNavRoute.News.label} 新闻 教务处", { News(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.News.route),
                SearchAppBean(18,"${AppNavRoute.Scan.label}", { Program(ifSaved,navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Program.receiveRoute()),
                SearchAppBean(19,"${AppNavRoute.Library.label} 座位预约 借阅", { LibraryItem(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Library.route),
                SearchAppBean(20,"${AppNavRoute.Bus.label}", { SchoolBus(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Bus.route),
                SearchAppBean(21,"报修 维修 后勤", { Repair(hazeState) }),
                SearchAppBean(22,"${AppNavRoute.NextCourse.label}", { NextCourse(ifSaved,vm,navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.NextCourse.receiveRoute()),
                SearchAppBean(23,"热水机 趣智校园", { HotWater() }),
                SearchAppBean(24,"${AppNavRoute.Classroom.label} 空教室", { Classroom(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Classroom.route),
                SearchAppBean(25,"体育 云运动 乐跑 校园跑 体测 体育测试 体检", { LePaoYun(hazeState) }),
                SearchAppBean(26,"${AppNavRoute.TimeTable.label} 校历", { WorkAndRest(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.TimeTable.route),
                SearchAppBean(27,"学信网", { XueXin(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.WebView.shareRoute(MyApplication.XUE_XIN_URL)),
                SearchAppBean(28,"${AppNavRoute.Life.label} 校园 校园 天气 教学楼 建筑 学堂", { Life(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Life.route),
                SearchAppBean(29,"${AppNavRoute.Transfer.label}", { Transfer(ifSaved,navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Transfer.route),
                SearchAppBean(30,"${AppNavRoute.CourseSearch.label} 全校开课 课程", { CoursesSearch(ifSaved,navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.CourseSearch.route),
                SearchAppBean(31,"${AppNavRoute.TeacherSearch.label} 老师检索", { TeacherSearch(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.TeacherSearch.route),
                SearchAppBean(32,"${AppNavRoute.Fee.label} 费用 欠缴学费", { Pay(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Fee.route),
                SearchAppBean(33,"实习", { Practice(ifSaved) }),
                SearchAppBean(34,"${AppNavRoute.Wechat.label} 校友 毕业 第二课堂 今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常 空教室 教务 同班同学 快递 取件码 团员 团建", { WeChatGo(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Wechat.route),
                SearchAppBean(35,"${AppNavRoute.StuTodayCampus.label} 今日校园 请假 助学金 奖学金 贫困 寝室 心理 日常", { ToadyCampus(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.StuTodayCampus.route),
                SearchAppBean(36,"大创系统 大学生创新创业", { IETP(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.WebView.shareRoute(MyApplication.IETP_URL)),
                SearchAppBean(37,"${AppNavRoute.Work.label} 实习 春招 双选 秋招", { Work(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Work.route),
                SearchAppBean(38,"${AppNavRoute.Holiday.label} 国家法定节假日 假期 节日", { Holiday(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.Holiday.route),
                SearchAppBean(39,"共建平台 信息共建 日程 网课 网址导航", { Supabase() }),
                SearchAppBean(40,"洗衣机 洗鞋机 烘干机 慧新易校 海乐生活 缴费", { Washing(vm,hazeState,navController,sharedTransitionScope,animatedContentScope) }),
                SearchAppBean(41,"${AppNavRoute.Admission.label} 历年分数线 招生计划", { Admission(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.Admission.route),
                SearchAppBean(42,"${AppNavRoute.WebVpn.label} 外地访问 内网", { WebVpn(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.WebVpn.route),
                SearchAppBean(43,"${AppNavRoute.OfficeHall.label}", { OfficeHall(navController,sharedTransitionScope,animatedContentScope) },AppNavRoute.OfficeHall.route),
                SearchAppBean(44,"慧新易校 一卡通 校园卡 账单 充值 缴费 合肥" , { HuiXin(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.WebView.shareRoute(getHuiXinURL())),
                SearchAppBean(45,"${AppNavRoute.SecondClass.label}", { SecondClass(navController,sharedTransitionScope,animatedContentScope) }, AppNavRoute.SecondClass.route),
            )
        )
    }
    LaunchedEffect(searchSort) {
        if(searchSort.isNotEmpty() && searchSort.isNotBlank()) {
            val l = funcMaps.toMutableList()
            funcMaps = l.reorderByIdsStr(searchSort).toList()
        }
    }
    val state = rememberLazyGridState()

    val filteredList = funcMaps.filter { it.searchKeyWord.contains(input, ignoreCase = true) }


    val paddingModifier = remember { Modifier.padding(horizontal = 3.dp, vertical = 3.dp) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP-3.dp)
    ) {
        items(2) {
            Column {
                InnerPaddingHeight(innerPaddings,true)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        items(filteredList.size, key = { filteredList[it].id }) { index->
            val item = filteredList[index]
            val s = @Composable {
                Box(modifier = Modifier.height(SEARCH_FUC_CARD_HEIGHT).fillMaxSize()) {
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        item.ui()
                    }
                }
            }
            with(sharedTransitionScope) {
                SmallCard(
                    modifier = (item.route?.let { paddingModifier.containerShare(sharedTransitionScope,animatedContentScope,it) } ?: paddingModifier),
                    color = mixedCardNormalColor()
                ) {
                    if(index % 2 == 0) {
                        // 位于左侧 观察右侧高度
                        if(index+1 < funcMaps.size) {
                            if(funcMaps[index+1].isHigh) {
                                s()
                                return@SmallCard
                            }
                        }
                    } else {
                        // 位于右侧 观察左侧高度
                        if(index-1 >= 0) {
                            if(funcMaps[index-1].isHigh) {
                                s()
                                return@SmallCard
                            }
                        }
                    }
                    item.ui()
                }
            }
        }
        items(2) { InnerPaddingHeight(innerPaddings,false) }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFuncs(
    input: String,
    onShow : (Boolean) -> Unit,
    onInputChanged: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
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
            leadingIcon = {
                IconButton(onClick = {
                    onShow(false)
                }) {
                    Icon(painterResource(R.drawable.search),null)
                }
            },
        )
    }
}