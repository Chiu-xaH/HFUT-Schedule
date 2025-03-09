package com.hfut.schedule.ui.activity.nologin

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.ui.activity.home.search.functions.bus.SchoolBus
import com.hfut.schedule.ui.activity.home.search.functions.dormitoryScore.DormitoryScoreXuanCheng
import com.hfut.schedule.ui.activity.home.search.functions.hotWater.HotWater
import com.hfut.schedule.ui.activity.home.search.functions.lepaoYun.LePaoYun
import com.hfut.schedule.ui.activity.home.search.functions.life.Life
import com.hfut.schedule.ui.activity.home.search.functions.news.News
import com.hfut.schedule.ui.activity.home.search.functions.notifications.NotificationsCenter
import com.hfut.schedule.ui.activity.home.search.functions.program.GuestProgram
import com.hfut.schedule.ui.activity.home.search.functions.repair.Repair
import com.hfut.schedule.ui.activity.home.search.functions.schoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.activity.home.search.functions.shower.Shower
import com.hfut.schedule.ui.activity.home.search.functions.teacherSearch.TeacherSearch
import com.hfut.schedule.ui.activity.home.search.functions.webLab.WebUI
import com.hfut.schedule.ui.activity.home.search.functions.xuexin.XueXin
import com.hfut.schedule.ui.utils.components.SmallCard
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun SearchScreenNoLogin(vm : NetWorkViewModel, ifSaved : Boolean, innerPaddings : PaddingValues, vmUI : UIViewModel, webVpn : Boolean, input : String) {


    @Composable
    fun CardItem(modifier: Modifier = Modifier.fillMaxSize(), content: @Composable () -> Unit) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
        ) {
            content()
        }
    }
//    if(SharePrefs.prefs.getString("TOKEN","")?.contains("ey") == false) MyToast("未登录,部分功能不可用")
//
//    //建立索引 <搜索关键词,功能>
    val funcMaps : Map<String,@Composable () -> Unit>  = mapOf(
//        "一卡通 校园卡 账单 充值 缴费 慧新易校" to { SchoolCardItem(vmUI, true) },
//        "考试" to { Exam(vm, ifSaved) },
//        "寝室电费 缴费" to { Electric(vm, false, vmUI) },
//        "校园网" to { LoginWeb(vmUI, false, vm) },
//        "教育邮箱" to { Mail(ifSaved, vm) },
//        "图书" to { LibraryItem(vm) },
//        "成绩" to { Grade(vm, ifSaved, webVpn) },
//        "挂科率" to { FailRate(vm) },
//        "课程汇总" to { CourseTotal(vm) },
//        "个人信息" to { PersonUI(ifSaved) },
        "网址导航 实验室 收纳" to { WebUI() },
        "报修 维修" to { Repair() },
//        "选课" to { SelectCourse(ifSaved, vm) },
        "寝室卫生评分 寝室卫生分数" to { DormitoryScoreXuanCheng(vm) },
        "消息中心 通知中心 收纳" to { NotificationsCenter() },
//        "教师评教 教师教评" to { Survey(ifSaved, vm) },
        "通知公告 新闻" to { News(vm) },
        "培养方案" to { GuestProgram(vm) },
        "校历" to { SchoolCalendar() },
        "校车" to { SchoolBus() },
        "洗浴 洗澡 呱呱物联 慧新易校 缴费" to { Shower(vm) },
//        "下学期课程表 下学期课表" to { NextCourse(ifSaved, vmUI) },
//        "今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常" to { ToadyCampus(ifSaved) },
//        "空教室" to { EmptyRoom(vm, ifSaved) },
        "乐跑云运动 校园跑" to { LePaoYun(vm) },
        "热水机 趣智校园" to { HotWater() },
        "学信网" to { XueXin() },
        "生活服务 校园 校园 天气 教学楼 建筑 学堂" to { Life(vm) },
//        "转专业" to { Transfer(ifSaved, vm) },
//        "开课查询 全校开课" to { CoursesSearch(ifSaved, vm) },
        "教师 老师" to { TeacherSearch(vm) },
//        "学费 费用 欠缴学费" to { Pay(ifSaved, vm) },
//        "实习" to { Work(ifSaved) },
//        "第二课堂" to { Second() }
    )

    val funcList = funcMaps.values.toList()

    var filteredList = funcList

    if(input != "") {
        filteredList = funcMaps.filterKeys { key ->
            key.contains(input, ignoreCase = true)
        }.values.toList()
    }


    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
            items(2) {
                Column {
                    Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            items(filteredList.size) { index->
                SmallCard(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                    filteredList[index]()
                }
            }
        }
        Login()
        Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))
    }



}

@Composable
fun Login() {
//    MyCard {
        androidx.compose.material3.ListItem(
            headlineContent = { Text(text = "登录解锁查询中心30+功能及课程表、聚焦完全版") },
            trailingContent = {
                FilledTonalIconButton(onClick = { refreshLogin() }) {
                    Icon(painterResource(id = R.drawable.login), contentDescription = null)
                }
            },
            modifier = Modifier.clickable { refreshLogin() }
        )
//    }
}