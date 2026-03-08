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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.SEARCH_DEFAULT_STR
import com.hfut.schedule.ui.component.container.SEARCH_FUC_CARD_HEIGHT
import com.hfut.schedule.ui.component.container.SmallCard
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.destination.AdmissionDestination
import com.hfut.schedule.ui.destination.AiDestination
import com.hfut.schedule.ui.destination.AlumniDestination
import com.hfut.schedule.ui.destination.BusDestination
import com.hfut.schedule.ui.destination.ClassroomDestination
import com.hfut.schedule.ui.destination.CommunityAppointmentDestination
import com.hfut.schedule.ui.destination.CourseSearchDestination
import com.hfut.schedule.ui.destination.DormitoryDestination
import com.hfut.schedule.ui.destination.ExamDestination
import com.hfut.schedule.ui.destination.FailRateDestination
import com.hfut.schedule.ui.destination.FeeDestination
import com.hfut.schedule.ui.destination.GradeDestination
import com.hfut.schedule.ui.destination.HolidayDestination
import com.hfut.schedule.ui.destination.LibraryDestination
import com.hfut.schedule.ui.destination.LifeDestination
import com.hfut.schedule.ui.destination.NewsDestination
import com.hfut.schedule.ui.destination.NotificationsDestination
import com.hfut.schedule.ui.destination.OfficeHallDestination
import com.hfut.schedule.ui.destination.PersonInfoDestination
import com.hfut.schedule.ui.destination.ProgramDestination
import com.hfut.schedule.ui.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.destination.SecondClassDestination
import com.hfut.schedule.ui.destination.SelectCoursesDestination
import com.hfut.schedule.ui.destination.StuTodayCampusDestination
import com.hfut.schedule.ui.destination.SurveyDestination
import com.hfut.schedule.ui.destination.TeacherSearchDestination
import com.hfut.schedule.ui.destination.TermCoursesDestination
import com.hfut.schedule.ui.destination.TrackDestination
import com.hfut.schedule.ui.destination.TransferMajorDestination
import com.hfut.schedule.ui.destination.WebFolderDestination
import com.hfut.schedule.ui.destination.WebViewDestination
import com.hfut.schedule.ui.destination.WebVpnDestination
import com.hfut.schedule.ui.destination.WorkAndRestDestination
import com.hfut.schedule.ui.destination.WorkDestination
import com.hfut.schedule.ui.screen.AppNavRoute
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
import com.hfut.schedule.ui.screen.home.search.function.other.wechat.Alumni
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
import com.hfut.schedule.ui.util.state.GlobalUIStateHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import com.xah.container.container.sharedContainer
import com.xah.navigation.utils.LocalNavController
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
                SearchAppBean(2,"${context.getString(AppNavRoute.ScanQrCode.label)} 扫码 指尖工大 CAS统一认证登录", { Scan() }, ScanQrCodeDestination.key,isHigh = true),
                SearchAppBean(3,"${context.getString(R.string.navigation_label_dormitory_electricity_bill)} 缴费 ${context.getString(R.string.navigation_label_hui_xin)}" , { Electric(vm, false, vmUI,hazeState) },isHigh = true),
                SearchAppBean(4,"${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_school_net)} 缴费" , { LoginWeb(vmUI, false, vm,hazeState) },isHigh = true),
                SearchAppBean(5,"${context.getString(R.string.navigation_label_school_email)} 校园邮箱" , { Mail(vm,hazeState) }),
                SearchAppBean(6,"${context.getString(AppNavRoute.Exam.label)}安排 教务处考试安排 ${context.getString(R.string.navigation_label_exam_news)}" , { Exam() }, ExamDestination(null).key),
                SearchAppBean(7,"${context.getString(AppNavRoute.Grade.label)}", { Grade(ifSaved) }, GradeDestination(ifSaved).key),
                SearchAppBean(8,"${context.getString(AppNavRoute.FailRate.label)}", { FailRate() }, FailRateDestination.key),
                SearchAppBean(9,"${context.getString(AppNavRoute.TermCourses.label)} 教材 课本", { CourseTotal(ifSaved) }, TermCoursesDestination(ifSaved,"SEARCH").key),
                SearchAppBean(10,"${context.getString(AppNavRoute.PersonInfo.label)}", { PersonUI() }, PersonInfoDestination.key),
                SearchAppBean(11,"${context.getString(AppNavRoute.WebFolder.label)} 实验室 ${context.getString(R.string.navigation_label_hui_xin)}", { WebUI() }, WebFolderDestination.key),
                SearchAppBean(12,"${context.getString(R.string.navigation_label_shower)} 洗澡 呱呱物联 ${context.getString(R.string.navigation_label_notification_box)} 缴费", { Shower(vm,hazeState) }),
                SearchAppBean(13,"${context.getString(AppNavRoute.SelectCourses.label)}", { SelectCourse(ifSaved) }, SelectCoursesDestination.key),
                SearchAppBean(14,"${context.getString(AppNavRoute.Dormitory.label)} 寝室卫生评分 寝室卫生分数", { DormitoryScoreXuanCheng() }, DormitoryDestination.key),
                SearchAppBean(15,"${context.getString(AppNavRoute.Notifications.label)} ${context.getString(R.string.navigation_label_notifications)} 收纳", { NotificationsCenter() }, NotificationsDestination.key),
                SearchAppBean(16,"${context.getString(AppNavRoute.Survey.label)} 教师评教 教师教评", { Survey(ifSaved) }, SurveyDestination(ifSaved).key),
                SearchAppBean(17,"${context.getString(AppNavRoute.News.label)} 新闻 教务处", { News() }, NewsDestination.key),
                SearchAppBean(18,"${context.getString(AppNavRoute.Program.label)}完成情况", { Program(ifSaved) }, ProgramDestination(ifSaved).key),
                SearchAppBean(19,"${context.getString(AppNavRoute.Library.label)} 座位预约 ${context.getString(R.string.navigation_label_library_borrowed)}", { LibraryItem() }, LibraryDestination.key),
                SearchAppBean(20,"${context.getString(AppNavRoute.Bus.label)}", { SchoolBus() }, BusDestination.key),
                SearchAppBean(21,"${context.getString(R.string.navigation_label_repair)} 维修 后勤", { Repair(hazeState) }),
//                SearchAppBean(22,"${AppNavRoute.NextCourse.label}", { NextCourse(ifSaved,navController ) },AppNavRoute.NextCourse.receiveRoute()),
                SearchAppBean(23,"饮水机 ${context.getString(R.string.navigation_label_hot_water)}机 趣智校园", { HotWater() }),
                SearchAppBean(24,"${context.getString(AppNavRoute.Classroom.label)} 空教室", { Classroom() }, ClassroomDestination.key),
                SearchAppBean(25,"体育 云运动 乐跑 校园跑 ${context.getString(R.string.navigation_label_physical_fitness_test)} 体育测试 体检", { LePaoYun() }, WebViewDestination.getKey(MyApplication.PE_HOME_URL)),
                SearchAppBean(26,"${context.getString(AppNavRoute.WorkAndRest.label)} 校历", { WorkAndRest() }, WorkAndRestDestination(null).key),
                SearchAppBean(27,"${context.getString(R.string.navigation_label_chsi)}", { XueXin() }, WebViewDestination.getKey(MyApplication.XUE_XIN_URL)),
                SearchAppBean(28,"${context.getString(AppNavRoute.Life.label)} 校园 天气 新生 地图", { Life() }, LifeDestination(false).key),
                SearchAppBean(29,"${context.getString(AppNavRoute.TransferMajor.label)}", { Transfer(ifSaved) }, TransferMajorDestination.key),
                SearchAppBean(30,"${context.getString(AppNavRoute.CourseSearch.label)} 全校开课 课程", { CoursesSearch(ifSaved) }, CourseSearchDestination.key),
                SearchAppBean(31,"${context.getString(AppNavRoute.TeacherSearch.label)} 老师检索", { TeacherSearch() }, TeacherSearchDestination.key),
                SearchAppBean(32,"${context.getString(AppNavRoute.Fee.label)} 费用 欠缴学费", { Pay() }, FeeDestination.key),
//                SearchAppBean(33,"实习", { Practice(ifSaved) }),
                SearchAppBean(34,"${context.getString(AppNavRoute.Alumni.label)} 校友 毕业", { Alumni() }, AlumniDestination.key),
                SearchAppBean(35,"${context.getString(AppNavRoute.StuTodayCampus.label)} 学工系统 学工平台 请假 助学金 奖学金 贫困 寝室 心理 日常", { ToadyCampus() }, StuTodayCampusDestination.key),
                SearchAppBean(36,"${context.getString(R.string.navigation_label_ietp)} 大学生创新创业系统", { IETP() }, WebViewDestination.getKey(MyApplication.IETP_URL)),
                SearchAppBean(37,"${context.getString(AppNavRoute.Work.label)} 实习 春招 双选 秋招", { Work() }, WorkDestination.key),
                SearchAppBean(38,"${context.getString(AppNavRoute.Holiday.label)} 国家法定节假日 假期 节日 调休", { Holiday() }, HolidayDestination.key),
                SearchAppBean(39,"${context.getString(R.string.navigation_label_supabase)} 信息共建 日程 网课 网址导航", { Supabase() }),
                SearchAppBean(40,"${context.getString(R.string.navigation_label_laundry)} 洗鞋机 烘干机 ${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_washing)} 缴费", { Washing(hazeState) }),
                SearchAppBean(41,"${context.getString(AppNavRoute.Admission.label)} 历年分数线 招生计划", { Admission() }, AdmissionDestination.key),
                SearchAppBean(42,"${context.getString(AppNavRoute.WebVpn.label)} 外地访问 内网", { WebVpn() }, WebVpnDestination.key),
                SearchAppBean(43,"${context.getString(AppNavRoute.OfficeHall.label)}", { OfficeHall() }, OfficeHallDestination.key),
                SearchAppBean(44,"${context.getString(R.string.navigation_label_hui_xin)} ${context.getString(R.string.navigation_label_hui_xin)} 校园卡 账单 充值 缴费 合肥" , { HuiXin() }, WebViewDestination.getKey(getHuiXinURL())),
                SearchAppBean(45,"${context.getString(AppNavRoute.SecondClass.label)}", { SecondClass() }, SecondClassDestination.key),
                SearchAppBean(46,"${context.getString(AppNavRoute.CommunityAppointment.label)} 场地预约 座位预约 宿舍自习室预约 智慧社区平台", { Appointment() }, CommunityAppointmentDestination.key),
                SearchAppBean(47,"AI 人工智能 ${context.getString(R.string.navigation_label_ai)}", { AI() }, AiDestination.key),
                SearchAppBean(48,"事务跟踪 事务追踪 issue 反馈 开发 ${context.getString(R.string.navigation_label_track)}", { Track() }, TrackDestination.key),
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
                shape = RectangleShape,
                modifier = (item.route?.let { paddingModifier.sharedContainer(it, MaterialTheme.shapes.small) } ?: paddingModifier.clip(MaterialTheme.shapes.small)),
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