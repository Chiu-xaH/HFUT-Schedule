package com.hfut.schedule.ui.screen.home.search

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
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
import com.hfut.schedule.ui.screen.home.search.function.community.termInfo.TermInfo
import com.hfut.schedule.ui.screen.home.search.function.school.alumni.Alumni
import com.hfut.schedule.ui.screen.home.search.function.my.holiday.Holiday
import com.hfut.schedule.ui.screen.home.search.function.huiXin.washing.Washing
import com.hfut.schedule.ui.screen.home.search.function.my.supabase.Supabase
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun SearchScreen(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
    innerPaddings : PaddingValues,
    vmUI : UIViewModel,
    input : String,
    navController : NavHostController,
    hazeState: HazeState
) {
    //建立索引 <搜索关键词,功能>
    val funcMaps : Map<String,@Composable () -> Unit>  = mapOf(
        "一卡通 校园卡 账单 充值 缴费 慧新易校" to { SchoolCardItem(vmUI, true) },  
        "考试" to { Exam(vm, ifSaved,hazeState) },
        "寝室电费 缴费 慧新易校" to { Electric(vm, false, vmUI,hazeState) },
        "校园网 慧新易校 缴费" to { LoginWeb(vmUI, false, vm,hazeState) },
        "教育邮箱" to { Mail(ifSaved, vm,vmUI,hazeState) },
        "一卡通 校园卡 账单 充值 缴费 慧新易校 合肥" to { Huixin() },
        "成绩" to { Grade(ifSaved,navController) },
        "挂科率" to { FailRate(vm,hazeState) }, 
        "课程汇总" to { CourseTotal(vm,hazeState) }, 
        "个人信息" to { PersonUI(ifSaved,hazeState) }, 
        "网址导航 实验室 收纳" to { WebUI(hazeState) }, 
        "洗浴 洗澡 呱呱物联 慧新易校 缴费" to { Shower(vm,hazeState) }, 
        "选课" to { SelectCourse(ifSaved, vm,hazeState,vmUI) },
        "寝室卫生评分 寝室卫生分数" to { DormitoryScoreXuanCheng(vm,hazeState) },  
        "消息中心 通知中心 收纳" to { NotificationsCenter(hazeState) }, 
        "教师评教 教师教评" to { Survey(ifSaved, vm,hazeState) }, 
        "通知公告 新闻" to { News() },
        "培养方案" to { Program(vm, ifSaved,hazeState) },
        "图书馆 座位预约 借阅" to { LibraryItem(vm,hazeState) },
        "校车" to { SchoolBus() },
        "报修 维修 后勤" to { Repair(hazeState) }, 
        "下学期课程表 下学期课表" to { NextCourse(ifSaved, vmUI,vm, hazeState) }, 
        "热水机 趣智校园" to { HotWater() }, 
        "空教室" to { EmptyRoom(vm, ifSaved,hazeState) }, 
        "乐跑云运动 校园跑 体测 体育测试 体育平台 体检" to { LePaoYun(hazeState) },
        "校历" to { SchoolCalendar() },
        "学信网" to { XueXin() },
        "生活服务 校园 校园 天气 教学楼 建筑 学堂" to { Life(vm,hazeState) }, 
        "转专业" to { Transfer(ifSaved, vm,hazeState) }, 
        "开课查询 全校开课" to { CoursesSearch(ifSaved, vm,hazeState) }, 
        "教师 老师" to { TeacherSearch(vm,hazeState) }, 
        "学费 费用 欠缴学费" to { Pay(vm,hazeState) },
        "实习" to { Practice(ifSaved) }, 
        "微信专区 校友 毕业 第二课堂 今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常 空教室 教务 同班同学 快递 取件码 团员 团建" to { WeChatGo(hazeState) },
        "今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常" to { ToadyCampus(ifSaved,vm) }, 
        "大创 大学生创新创业" to { IETP() }, 
        "就业 实习 春招 双选 秋招" to { Work(hazeState,vm) },
//        "校友 毕业" to { Alumni() },
        "国家法定节假日 假期 节日" to { Holiday(hazeState) },
        "云端共建平台 信息共建 日程 网课 网址导航" to { Supabase(vm) },
        "洗衣机 慧新易校 缴费" to { Washing(hazeState) },
        "作息" to { TermInfo(hazeState) },
        )

    val funcList = funcMaps.values.toList()

    var filteredList = funcList

    if(input != "") {
        filteredList = funcMaps.filterKeys { key ->
            key.contains(input, ignoreCase = true)
        }.values.toList()
    }



    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 12.dp)) {
        items(2) {
            Column {
                Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        items(filteredList.size) { index->
            SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
                filteredList[index]()
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