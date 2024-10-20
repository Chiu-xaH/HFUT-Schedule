package com.hfut.schedule.ui.Activity.success.search.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.Activity.success.calendar.next.NextCourse
import com.hfut.schedule.ui.Activity.success.search.Search.Bus.SchoolBus
import com.hfut.schedule.ui.Activity.success.search.Search.CourseSearch.courseSearch
import com.hfut.schedule.ui.Activity.success.search.Search.DormitoryScore.DormitoryScoreItem
import com.hfut.schedule.ui.Activity.success.search.Search.Electric.Electric
import com.hfut.schedule.ui.Activity.success.search.Search.EmptyRoom.EmptyRoom
import com.hfut.schedule.ui.Activity.success.search.Search.Exam.Exam
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.FailRate
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.Grade
import com.hfut.schedule.ui.Activity.success.search.Search.HotWater.HotWater
import com.hfut.schedule.ui.Activity.success.search.Search.LePaoYun.LePaoYun
import com.hfut.schedule.ui.Activity.success.search.Search.Library.LibraryItem
import com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb.LoginWeb
import com.hfut.schedule.ui.Activity.success.search.Search.Mail.Mail
import com.hfut.schedule.ui.Activity.success.search.Search.Map.Map
import com.hfut.schedule.ui.Activity.success.search.Search.News.News
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationsCenter
import com.hfut.schedule.ui.Activity.success.search.Search.Pay.Pay
import com.hfut.schedule.ui.Activity.success.search.Search.Person.PersonUI
import com.hfut.schedule.ui.Activity.success.search.Search.Program.Program
import com.hfut.schedule.ui.Activity.success.search.Search.Repair.Repair
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.Activity.success.search.Search.Second.Second
import com.hfut.schedule.ui.Activity.success.search.Search.SelectCourse.selectCourse
import com.hfut.schedule.ui.Activity.success.search.Search.Shower.Shower
import com.hfut.schedule.ui.Activity.success.search.Search.Survey.Survey
import com.hfut.schedule.ui.Activity.success.search.Search.TodayCampus.ToadyCampus
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.CourseTotal
import com.hfut.schedule.ui.Activity.success.search.Search.Transfer.Transfer
import com.hfut.schedule.ui.Activity.success.search.Search.Web.WebUI
import com.hfut.schedule.ui.Activity.success.search.Search.Work.Work
import com.hfut.schedule.ui.Activity.success.search.Search.XueXin.XueXin
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.jsoup.Jsoup


fun getName(vm : LoginSuccessViewModel) : String? {
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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : LoginSuccessViewModel,ifSaved : Boolean,innerPaddings : PaddingValues,vmUI : UIViewModel,webVpn : Boolean) {

    getName(vm)
    val info = prefs.getString("info","")
    if(prefs.getString("TOKEN","")?.contains("ey") == false) MyToast("未登录,部分功能不可用")


    var text by remember { mutableStateOf("你好") }
    if(GetDate.formattedTime_Hour.toInt() == 12) text = "午饭时间到~"
    if(GetDate.formattedTime_Hour.toInt() in 13..17) text = "下午要忙什么呢"
    if(GetDate.formattedTime_Hour.toInt() in 7..11) text = "上午好呀"
    if(GetDate.formattedTime_Hour.toInt() in 5..6) text = "起的好早呀"
    if(GetDate.formattedTime_Hour.toInt() in 18..23) text = "晚上好"
    if(GetDate.formattedTime_Hour.toInt() in 0..4) text = "熬夜也要早睡哦"
    
    val hazeState = remember { HazeState() }


        Column(
            modifier = Modifier
                .haze(state = hazeState, backgroundColor = MaterialTheme.colorScheme.surface,)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPaddings)

        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    SchoolCardItem(vmUI,true)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Exam(vm,ifSaved)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Electric(vm,false,vmUI)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    LoginWeb(vmUI,false,vm)
                }
        }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Mail()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    LibraryItem(vm)
                }
        }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Grade(vm,ifSaved,webVpn)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    FailRate(vm)
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    CourseTotal(vm)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    PersonUI(ifSaved)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    WebUI()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Repair()
                }
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    selectCourse(ifSaved, vm)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    DormitoryScoreItem(vm)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    NotificationsCenter()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Survey(ifSaved,vm)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    News(vm)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Program(vm,ifSaved)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    SchoolCalendar()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    SchoolBus()
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Shower(vm)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    NextCourse(ifSaved,vmUI)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                   HotWater()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    EmptyRoom(vm,ifSaved)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    LePaoYun(vm)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    ToadyCampus(ifSaved)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    XueXin()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Map()
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                   Transfer(ifSaved, vm)
                }
                    Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    courseSearch(ifSaved,vm)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Work(ifSaved)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Pay(ifSaved,vm)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){
                    Second()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(.5f),
                    shape = MaterialTheme.shapes.small,
                ){

                }
            }
             Spacer(modifier = Modifier.height(5.dp))
        }
}