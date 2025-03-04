package com.hfut.schedule.ui.activity.home.search.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.activity.home.search.functions.Huixin
import com.hfut.schedule.ui.activity.home.search.functions.bus.SchoolBus
import com.hfut.schedule.ui.activity.home.search.functions.card.SchoolCardItem
import com.hfut.schedule.ui.activity.home.search.functions.courseSearch.CoursesSearch
import com.hfut.schedule.ui.activity.home.search.functions.dormitoryScore.DormitoryScoreXuanCheng
import com.hfut.schedule.ui.activity.home.search.functions.electric.Electric
import com.hfut.schedule.ui.activity.home.search.functions.emptyRoom.EmptyRoom
import com.hfut.schedule.ui.activity.home.search.functions.exam.Exam
import com.hfut.schedule.ui.activity.home.search.functions.failRate.FailRate
import com.hfut.schedule.ui.activity.home.search.functions.grade.Grade
import com.hfut.schedule.ui.activity.home.search.functions.hotWater.HotWater
import com.hfut.schedule.ui.activity.home.search.functions.lepaoYun.LePaoYun
import com.hfut.schedule.ui.activity.home.search.functions.library.LibraryItem
import com.hfut.schedule.ui.activity.home.search.functions.life.Life
import com.hfut.schedule.ui.activity.home.search.functions.loginWeb.LoginWeb
import com.hfut.schedule.ui.activity.home.search.functions.mail.Mail
import com.hfut.schedule.ui.activity.home.search.functions.news.News
import com.hfut.schedule.ui.activity.home.search.functions.nextCourse.NextCourse
import com.hfut.schedule.ui.activity.home.search.functions.notifications.NotificationsCenter
import com.hfut.schedule.ui.activity.home.search.functions.pay.Pay
import com.hfut.schedule.ui.activity.home.search.functions.person.PersonUI
import com.hfut.schedule.ui.activity.home.search.functions.program.Program
import com.hfut.schedule.ui.activity.home.search.functions.repair.Repair
import com.hfut.schedule.ui.activity.home.search.functions.schoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.activity.home.search.functions.second.Second
import com.hfut.schedule.ui.activity.home.search.functions.selectCourse.SelectCourse
import com.hfut.schedule.ui.activity.home.search.functions.shower.Shower
import com.hfut.schedule.ui.activity.home.search.functions.survey.Survey
import com.hfut.schedule.ui.activity.home.search.functions.teacherSearch.TeacherSearch
import com.hfut.schedule.ui.activity.home.search.functions.todayCampus.ToadyCampus
import com.hfut.schedule.ui.activity.home.search.functions.totalCourse.CourseTotal
import com.hfut.schedule.ui.activity.home.search.functions.transferMajor.Transfer
import com.hfut.schedule.ui.activity.home.search.functions.webLab.WebUI
import com.hfut.schedule.ui.activity.home.search.functions.work.Work
import com.hfut.schedule.ui.activity.home.search.functions.xueXin.XueXin
import com.hfut.schedule.ui.utils.components.CardNormalColor
import com.hfut.schedule.ui.utils.components.CardNormalDp
import com.hfut.schedule.ui.utils.components.SmallCard
import com.hfut.schedule.ui.utils.style.textFiledTransplant
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.jsoup.Jsoup


fun getName(vm : NetWorkViewModel) : String? {
    val card =prefs.getString("card","正在获取")
    val borrow =prefs.getString("borrow","正在获取")
    val sub =prefs.getString("sub","正在获取")

    if (card == "请登录刷新" && vm.token.value?.contains("AT") == true) {
        CoroutineScope(Job()).apply {
           // async { vm.getCard("Bearer " + vm.token.value) }
            async { vm.getBorrowBooks("Bearer " + vm.token.value) }
            async { vm.getSubBooks("Bearer " + vm.token.value) }
        }
    }

    val info = prefs.getString("info","")
    val doc = info?.let { Jsoup.parse(it) }
    val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
    return name
}


@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun SearchScreen(vm : NetWorkViewModel, ifSaved : Boolean, innerPaddings : PaddingValues, vmUI : UIViewModel,input : String) {

    @Composable
    fun CardItem(modifier: Modifier = Modifier.fillMaxSize(), content: @Composable () -> Unit) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = CardNormalColor())
        ) {
            content()
        }
    }
//    if(prefs.getString("TOKEN","")?.contains("ey") == false) MyToast("未登录,部分功能不可用")

    //建立索引 <搜索关键词,功能>
    val funcMaps : Map<String,@Composable () -> Unit>  = mapOf(
        "一卡通 校园卡 账单 充值 缴费 慧新易校" to { SchoolCardItem(vmUI, true) },
        "考试" to { Exam(vm, ifSaved) },
        "寝室电费 缴费" to { Electric(vm, false, vmUI) },
        "校园网" to { LoginWeb(vmUI, false, vm) },
        "教育邮箱" to { Mail(ifSaved, vm) },
        "图书" to { LibraryItem(vm) },
        "成绩" to { Grade(vm, ifSaved) },
        "挂科率" to { FailRate(vm) },
        "课程汇总" to { CourseTotal(vm) },
        "个人信息" to { PersonUI(ifSaved) },
        "网址导航 实验室 收纳" to { WebUI() },
        "洗浴 洗澡 呱呱物联 慧新易校 缴费" to { Shower(vm) },
        "选课" to { SelectCourse(ifSaved, vm) },
        "寝室卫生评分 寝室卫生分数" to { DormitoryScoreXuanCheng(vm) },
        "消息中心 通知中心 收纳" to { NotificationsCenter() },
        "教师评教 教师教评" to { Survey(ifSaved, vm) },
        "通知公告 新闻" to { News(vm) },
        "培养方案" to { Program(vm, ifSaved) },
        "校历" to { SchoolCalendar() },
        "校车" to { SchoolBus() },
        "报修 维修" to { Repair() },
        "下学期课程表 下学期课表" to { NextCourse(ifSaved, vmUI,vm) },
        "热水机 趣智校园" to { HotWater() },
        "空教室" to { EmptyRoom(vm, ifSaved) },
        "乐跑云运动 校园跑" to { LePaoYun(vm) },
        "一卡通 校园卡 账单 充值 缴费 慧新易校 合肥" to { Huixin() },
        "学信网" to { XueXin() },
        "生活服务 校园 校园 天气 教学楼 建筑 学堂" to { Life(vm) },
        "转专业" to { Transfer(ifSaved, vm) },
        "开课查询 全校开课" to { CoursesSearch(ifSaved, vm) },
        "教师 老师" to { TeacherSearch(vm) },
        "学费 费用 欠缴学费" to { Pay(ifSaved, vm) },
        "实习" to { Work(ifSaved) },
        "第二课堂" to { Second() },
        "今日校园 学工系统 请假 助学金 奖学金 贫困 寝室 心理 日常" to { ToadyCampus(ifSaved,vm) },
    )

    val funcList = funcMaps.values.toList()

    var filteredList = funcList

    if(input != "") {
        filteredList = funcMaps.filterKeys { key ->
            key.contains(input, ignoreCase = true)
        }.values.toList()
    }



    LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
        items(2) {
            Column {
                Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        items(filteredList.size) { index->
            SmallCard(modifier = Modifier.padding(horizontal = CardNormalDp(), vertical = CardNormalDp())) {
                filteredList[index]()
            }
        }
        items( 2) { Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding())) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFuncs(ifSaved: Boolean,blur : Boolean,input: String,webVpn: Boolean = false, onInputChanged: (String) -> Unit) {
   // var input by remember { mutableStateOf("") }
    //val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.Center
    ) {

        TextField(
            modifier = Modifier
                .weight(1f),
                //.padding(horizontal = AppHorizontalDp()),
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
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = if(webVpn) "WEBVPN" else "已登录", color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
        )
    }
}