package com.hfut.schedule.ui.screen.home.search

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.SEARCH_DEFAULT_STR
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.container.SEARCH_FUC_CARD_HEIGHT
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.nav.destination.AdmissionDestination
import com.hfut.schedule.ui.nav.destination.AiDestination
import com.hfut.schedule.ui.nav.destination.AlumniDestination
import com.hfut.schedule.ui.nav.destination.BusDestination
import com.hfut.schedule.ui.nav.destination.ClassroomDestination
import com.hfut.schedule.ui.nav.destination.CommunityAppointmentDestination
import com.hfut.schedule.ui.nav.destination.CourseSearchDestination
import com.hfut.schedule.ui.nav.destination.DormitoryDestination
import com.hfut.schedule.ui.nav.destination.ExamDestination
import com.hfut.schedule.ui.nav.destination.FailRateDestination
import com.hfut.schedule.ui.nav.destination.FeeDestination
import com.hfut.schedule.ui.nav.destination.GradeDestination
import com.hfut.schedule.ui.nav.destination.HolidayDestination
import com.hfut.schedule.ui.nav.destination.LibraryDestination
import com.hfut.schedule.ui.nav.destination.LifeDestination
import com.hfut.schedule.ui.nav.destination.NewsDestination
import com.hfut.schedule.ui.nav.destination.NotificationsDestination
import com.hfut.schedule.ui.nav.destination.OfficeHallDestination
import com.hfut.schedule.ui.nav.destination.PersonInfoDestination
import com.hfut.schedule.ui.nav.destination.ProgramDestination
import com.hfut.schedule.ui.nav.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.nav.destination.SecondClassDestination
import com.hfut.schedule.ui.nav.destination.SelectCoursesDestination
import com.hfut.schedule.ui.nav.destination.StuTodayCampusDestination
import com.hfut.schedule.ui.nav.destination.SurveyDestination
import com.hfut.schedule.ui.nav.destination.TeacherSearchDestination
import com.hfut.schedule.ui.nav.destination.TermCoursesDestination
import com.hfut.schedule.ui.nav.destination.TrackDestination
import com.hfut.schedule.ui.nav.destination.TransferMajorDestination
import com.hfut.schedule.ui.nav.destination.WebFolderDestination
import com.hfut.schedule.ui.nav.destination.WebViewDestination
import com.hfut.schedule.ui.nav.destination.WebVpnDestination
import com.hfut.schedule.ui.nav.destination.WorkAndRestDestination
import com.hfut.schedule.ui.nav.destination.WorkDestination
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.nav.window.ExpressWindow
import com.hfut.schedule.ui.nav.window.FeedbackWindow
import com.hfut.schedule.ui.nav.window.RepairWindow

import com.hfut.schedule.ui.screen.home.search.function.community.appointment.Appointment
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
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.PersonUI
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
import com.hfut.schedule.ui.screen.home.search.function.other.AI
import com.hfut.schedule.ui.screen.home.search.function.other.Track
import com.hfut.schedule.ui.screen.home.search.function.other.life.Life
import com.hfut.schedule.ui.screen.home.search.function.other.life.Express
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.Alumni
import com.hfut.schedule.ui.screen.home.search.function.other.xueXin.XueXin
import com.hfut.schedule.ui.screen.home.search.function.school.Feedback
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
import com.hfut.schedule.ui.util.state.GlobalStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.navigation.util.LocalNavController
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.container.component.base.sharedContainer
import com.sharednav.common.util.NoneRoundShape
import com.xah.shared.LogUtil
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
    val name : Int,
    val icon : Int,
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
        LogUtil.error(e)
        // 出错时恢复默认顺序
        reorderByIds(GlobalStateHolder.funcDefault.map { it.id })
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
//    navController : NavHostController,
    hazeState: HazeState,
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val searchSort by DataStoreManager.searchSort.collectAsState(initial = SEARCH_DEFAULT_STR)
    //建立索引 <搜索关键词,功能>
    var funcMaps by remember(vm, ifSaved, vmUI, navController, hazeState) {
        mutableStateOf(
            listOf(
                SearchAppBean(1,"${context.getString(R.string.navigation_label_hui_xin)} 校园卡 账单 充值 缴费 ${context.getString(R.string.navigation_label_school_card)}" , { SchoolCardItem(vmUI, true) }, isHigh = true),
                SearchAppBean(2,"${ScanQrCodeDestination.title.asString(context)} 扫码 指尖工大 CAS统一认证登录", { Scan() }, ScanQrCodeDestination.key,isHigh = true),
                SearchAppBean(3,"${context.getString(R.string.navigation_label_dormitory_electricity_bill)} 缴费 ${context.getString(R.string.navigation_label_hui_xin)}" , { Electric(vm, false, vmUI,hazeState) },isHigh = true),
                SearchAppBean(4,"${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_school_net)} 缴费" , { LoginWeb(vmUI, false, vm,hazeState) },isHigh = true),
                SearchAppBean(5,"${context.getString(R.string.navigation_label_school_email)} 校园邮箱" , { Mail(vm,hazeState) }),
                SearchAppBean(6,"${ExamDestination.TITLE.asString(context)}安排 教务处考试安排 ${context.getString(R.string.navigation_label_exam_news)}" , { Exam() }, ExamDestination(null).key),
                SearchAppBean(7,"${GradeDestination.TITLE.asString(context)}", { Grade(ifSaved) }, GradeDestination(ifSaved).key),
                SearchAppBean(8,"${FailRateDestination.title.asString(context)}", { FailRate() }, FailRateDestination.key),
                SearchAppBean(9,"${TermCoursesDestination.TITLE.asString(context)} 教材 课本", { CourseTotal(ifSaved) }, TermCoursesDestination(ifSaved,"SEARCH").key),
                SearchAppBean(10,"${PersonInfoDestination.title.asString(context)}", { PersonUI() }, PersonInfoDestination.key),
                SearchAppBean(11,"${WebFolderDestination.title.asString(context)} 实验室 ${context.getString(R.string.navigation_label_hui_xin)}", { WebUI() }, WebFolderDestination.key),
                SearchAppBean(12,"${context.getString(R.string.navigation_label_shower)} 洗澡 呱呱物联 ${context.getString(R.string.navigation_label_notification_box)} 缴费", { Shower(vm,hazeState) }),
                SearchAppBean(13,"${SelectCoursesDestination.title.asString(context)}", { SelectCourse(ifSaved) }, SelectCoursesDestination.key),
                SearchAppBean(14,"${DormitoryDestination.title.asString(context)} 寝室卫生评分 寝室卫生分数", { DormitoryScoreXuanCheng() }, DormitoryDestination.key),
                SearchAppBean(15,"${NotificationsDestination.title.asString(context)} ${context.getString(R.string.navigation_label_notifications)} 收纳", { NotificationsCenter() }, NotificationsDestination.key),
                SearchAppBean(16,"${SurveyDestination.TITLE.asString(context)} 教师评教 教师教评", { Survey(ifSaved) }, SurveyDestination(ifSaved).key),
                SearchAppBean(17,"${NewsDestination.title.asString(context)} 新闻 教务处", { News() }, NewsDestination.key),
                SearchAppBean(18,"${ProgramDestination.TITLE.asString(context)}完成情况", { Program(ifSaved) }, ProgramDestination(ifSaved).key),
                SearchAppBean(19,"${LibraryDestination.title.asString(context)} 座位预约 ${context.getString(R.string.navigation_label_library_borrowed)}", { LibraryItem() }, LibraryDestination.key),
                SearchAppBean(20,"${BusDestination.title.asString(context)}", { SchoolBus() }, BusDestination.key),
                SearchAppBean(21,"${context.getString(R.string.navigation_label_repair)} 维修 后勤", { Repair() }, RepairWindow.key),
//                SearchAppBean(22,"${AppNavRoute.NextCourse.label}", { NextCourse(ifSaved,navController ) },AppNavRoute.NextCourse.receiveRoute()),
                SearchAppBean(23,"饮水机 ${context.getString(R.string.navigation_label_hot_water)}机 趣智校园", { HotWater() }),
                SearchAppBean(24,"${ClassroomDestination.TITLE.asString(context)} 空教室", { Classroom() }, ClassroomDestination().key),
                SearchAppBean(25,"体育 云运动 乐跑 校园跑 ${context.getString(R.string.navigation_label_physical_fitness_test)} 体育测试 体检", { LePaoYun() }, WebViewDestination.getKey(Constant.PE_HOME_URL)),
                SearchAppBean(26,"${WorkAndRestDestination.TITLE.asString(context)} 校历", { WorkAndRest() }, WorkAndRestDestination(null).key),
                SearchAppBean(27,"${context.getString(R.string.navigation_label_chsi)}", { XueXin() }, WebViewDestination.getKey(Constant.XUE_XIN_URL)),
                SearchAppBean(28,"${LifeDestination.title.asString(context)} 校园 天气 新生 楼层导向 地图 ${TermReportDestination.title.asString(context)} 学期总结 成绩 消费 图书馆 借阅 统计 报表", { Life() }, LifeDestination.key),
                SearchAppBean(29,"${TransferMajorDestination.title.asString(context)}", { Transfer(ifSaved) }, TransferMajorDestination.key),
                SearchAppBean(30,"${CourseSearchDestination.title.asString(context)} 全校开课 课程", { CoursesSearch(ifSaved) }, CourseSearchDestination.key),
                SearchAppBean(31,"${TeacherSearchDestination.title.asString(context)} 老师检索", { TeacherSearch() }, TeacherSearchDestination.key),
                SearchAppBean(32,"${FeeDestination.title.asString(context)} 费用 欠缴学费", { Pay() }, FeeDestination.key),
//                SearchAppBean(33,"实习", { Practice(ifSaved) }),
                SearchAppBean(34,"${AlumniDestination.title.asString(context)} 校友 毕业", { Alumni() }, AlumniDestination.key),
                SearchAppBean(35,"${StuTodayCampusDestination.title.asString(context)} 学工系统 学工平台 请假 助学金 奖学金 贫困 寝室 心理 日常", { ToadyCampus() }, StuTodayCampusDestination.key),
                SearchAppBean(36,"${context.getString(R.string.navigation_label_ietp)} 大学生创新创业系统", { IETP() }, WebViewDestination.getKey(Constant.IETP_URL)),
                SearchAppBean(37,"${WorkDestination.title.asString(context)} 实习 春招 双选 秋招", { Work() }, WorkDestination.key),
                SearchAppBean(38,"${HolidayDestination.title.asString(context)} 国家法定节假日 假期 节日 调休", { Holiday() }, HolidayDestination.key),
                SearchAppBean(39,"${context.getString(R.string.navigation_label_supabase)} 信息共建 日程 网课 网址导航", { Supabase() }),
                SearchAppBean(40,"${context.getString(R.string.navigation_label_laundry)} 洗鞋机 烘干机 ${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_washing)} 缴费", { Washing(hazeState) }),
                SearchAppBean(41,"${AdmissionDestination.title.asString(context)} 历年分数线 招生计划", { Admission() }, AdmissionDestination.key),
                SearchAppBean(42,"${WebVpnDestination.title.asString(context)} 外地访问 内网", { WebVpn() }, WebVpnDestination.key),
                SearchAppBean(43,"${OfficeHallDestination.title.asString(context)}", { OfficeHall() }, OfficeHallDestination.key),
                SearchAppBean(44,"${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_hui_xin)} 校园卡 账单 充值 缴费 合肥" , { HuiXin() }, WebViewDestination.getKey(getHuiXinURL())),
                SearchAppBean(45,"${SecondClassDestination.title.asString(context)}", { SecondClass() }, SecondClassDestination.key),
                SearchAppBean(46,"${CommunityAppointmentDestination.title.asString(context)} 场地预约 座位预约 宿舍自习室预约 智慧社区平台", { Appointment() }, CommunityAppointmentDestination.key),
                SearchAppBean(47,"AI 人工智能 ${context.getString(R.string.navigation_label_ai)}", { AI() }, AiDestination.key),
                SearchAppBean(48,"事务跟踪 事务追踪 issue 反馈 开发 ${context.getString(R.string.navigation_label_track)}", { Track() }, TrackDestination.key),
                SearchAppBean(49,"拼多多 淘宝 快递 取件码 包裹 ${context.getString(R.string.navigation_label_express)}", { Express() },ExpressWindow.key),
                SearchAppBean(50,"${context.getString(R.string.navigation_label_feedback)} 反馈 建议", { Feedback() }, FeedbackWindow.key),
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
            SmallCard(
                shape = NoneRoundShape,
                modifier = (item.route?.let { paddingModifier.sharedContainer(it, MaterialTheme.shapes.small,cardNormalColor()) } ?: paddingModifier.clip(MaterialTheme.shapes.small)),
                color = cardNormalColor()
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