package com.hfut.schedule.ui.Activity.success.search.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.MutableState
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
import com.hfut.schedule.ui.Activity.success.focus.Focus.TodayUI
import com.hfut.schedule.ui.Activity.success.search.Search.Bus.SchoolBus
import com.hfut.schedule.ui.Activity.success.search.Search.Electric.Electric
import com.hfut.schedule.ui.Activity.success.search.Search.EmptyRoom.EmptyRoom
import com.hfut.schedule.ui.Activity.success.search.Search.Estimate.Estimate
import com.hfut.schedule.ui.Activity.success.search.Search.Exam.Exam
import com.hfut.schedule.ui.Activity.success.search.Search.FailRate.FailRate
import com.hfut.schedule.ui.Activity.success.search.Search.Grade.Grade
import com.hfut.schedule.ui.Activity.success.search.Search.HotWater.HotWater
import com.hfut.schedule.ui.Activity.success.search.Search.Lab.Lab
import com.hfut.schedule.ui.Activity.success.search.Search.LePaoYun.LePaoYun
import com.hfut.schedule.ui.Activity.success.search.Search.Library.LibraryItem
import com.hfut.schedule.ui.Activity.success.search.Search.More.More
import com.hfut.schedule.ui.Activity.success.search.Search.News.News
import com.hfut.schedule.ui.Activity.success.search.Search.NotificationsCenter.NotificationsCenter
import com.hfut.schedule.ui.Activity.success.search.Search.Mail.Pay
import com.hfut.schedule.ui.Activity.success.search.Search.Person.PersonUI
import com.hfut.schedule.ui.Activity.success.search.Search.Program.Program
import com.hfut.schedule.ui.Activity.success.search.Search.Repair.Repair
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard.TodayAndCard
import com.hfut.schedule.ui.Activity.success.search.Search.Second.Second
import com.hfut.schedule.ui.Activity.success.search.Search.TotalCourse.CourseTotal
import com.hfut.schedule.ui.Activity.success.search.Search.Web.WebUI
import com.hfut.schedule.ui.Activity.success.search.Search.Xuanqu.XuanquItem
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
fun SearchScreen(vm : LoginSuccessViewModel,ifSaved : Boolean,innerPaddings : PaddingValues,vmUI : UIViewModel) {

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
            if (ifSaved){
                TodayAndCard(vmUI)
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(.5f),
                            shape = MaterialTheme.shapes.medium,
                        ){
                            Grade(vm,true)
                        }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Exam(vm,ifSaved)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        FailRate(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        LibraryItem(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        CourseTotal(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        if (info != null)
                            if(info.isNotEmpty())
                                PersonUI(true)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        WebUI()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        LePaoYun(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Electric(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        XuanquItem(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        SchoolCalendar()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        SchoolBus()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        NotificationsCenter()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        HotWater()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        News(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Repair()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Lab()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Pay()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Second()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Estimate()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Program(vm,true)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        EmptyRoom(vm,true)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        More()
                    }
                }
            } else {

                TodayAndCard(vmUI)
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Grade(vm,false)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Exam(vm,false)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        FailRate(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        LibraryItem(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        CourseTotal(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        PersonUI(false)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        WebUI()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        LePaoYun(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Electric(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        XuanquItem(vm)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Program(vm,false)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        EmptyRoom(vm,false)
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        SchoolCalendar()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        SchoolBus()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        NotificationsCenter()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        HotWater()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        News(vm)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Repair()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Second()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Estimate()
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp), horizontalArrangement = Arrangement.Center) {

                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Lab()
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(.5f),
                        shape = MaterialTheme.shapes.medium,
                    ){
                        Pay()
                    }
                }
            }
             Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))
        }

}

fun Refresh(vm : LoginSuccessViewModel, rotating: MutableState<Boolean>) {
    Handler(Looper.getMainLooper()).post{vm.ExamData.value = "{}"}
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var term = ""
    val month = GetDate.Date_MM.toInt()
    if( month >= 9 || month <= 2) term = "1"
    else term = "2"
    var years = GetDate.Date_yyyy
    if (month <= 8) years = (years.toInt() - 1).toString()
    rotating.value = true
    CoroutineScope(Job()).launch {
        vm.apply {
            async {
           //     async { CommuityTOKEN?.let { getOverDueBook(it,"1") } }
             //   async { CommuityTOKEN?.let { GetHistory(it,"1") } }
               /// async { CommuityTOKEN?.let { GetCourse(it) } }
             //   async { CommuityTOKEN?.let { Exam(it) } }
              //  async { CommuityTOKEN?.let { getAvgGrade(it) } }
              //  async { CommuityTOKEN?.let { getAllAvgGrade(it) }  }
               // async { CommuityTOKEN?.let { GetBorrowed(it,"1") } }
               // async { CommuityTOKEN?.let { vm.getGrade(it,years+"-"+(years.toInt()+1),term) } }
            }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.ExamData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("操作成功")) {
                                MyToast("刷新成功")
                                rotating.value = false
                            } else if (vm.ExamData.value == "错误") {
                                MyToast("请求失败")
                                rotating.value = false
                            }
                        }
                    }
                }
            }
        }
    }
}