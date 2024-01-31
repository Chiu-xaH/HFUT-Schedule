package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.TotalCourse.CourseTotal
import com.hfut.schedule.ui.ComposeUI.Search.EmptyRoom.EmptyRoom
import com.hfut.schedule.ui.ComposeUI.Search.Estimate.Estimate
import com.hfut.schedule.ui.ComposeUI.Search.Exam.Exam
import com.hfut.schedule.ui.ComposeUI.Search.Grade.Grade
import com.hfut.schedule.ui.ComposeUI.Search.HotWater.HotWater
import com.hfut.schedule.ui.ComposeUI.Search.Library.LibraryItem
import com.hfut.schedule.ui.ComposeUI.Search.Person.PersonUI
import com.hfut.schedule.ui.ComposeUI.Search.Program.Program
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.ComposeUI.Search.Xuanqu.XuanquItem
import com.hfut.schedule.ui.ComposeUI.Search.Web.WebUI
import com.hfut.schedule.ui.ComposeUI.Search.LePaoYun.LePaoYun
import com.hfut.schedule.ui.ComposeUI.Search.NotificationsCenter.NotificationsCenter
import com.hfut.schedule.ui.ComposeUI.Search.Bus.SchoolBus
import com.hfut.schedule.ui.ComposeUI.Search.Electric.Electric
import com.hfut.schedule.ui.ComposeUI.Search.FailRate.FailRate
import com.hfut.schedule.ui.ComposeUI.Search.Second.Second
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.UIUtils.MyToast
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

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : LoginSuccessViewModel,ifSaved : Boolean,) {

    getName(vm)
    if(prefs.getString("TOKEN","")?.contains("ey") == false) MyToast("未登录,部分功能不可用")
    // 创建一个无限循环的动画过渡
    val transition = rememberInfiniteTransition()
    // 从0到360度线性插值，每秒旋转一圈
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    val rotating = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                //    if (ifSaved) Text("免登录查询") else
                        Text("嗨  亲爱的 ${getName(vm)} 同学")
                },
                actions = {
                    IconButton(onClick = {
                        Refresh(vm,rotating)
                    }) {
                        Icon(painter = painterResource(id = R.drawable.rotate_right),
                            contentDescription = "",
                            modifier = Modifier.graphicsLayer(rotationZ = if (rotating.value) angle else 0f)
                        )
                    }
                }
            )
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            if (ifSaved){
                Grade(vm,ifSaved)
                Exam(vm)
               // Program(vm)
                SchoolCardItem(vm)
                LibraryItem(vm)
                FailRate(vm)
                CourseTotal(vm)
                WebUI()
                LePaoYun(vm)
                XuanquItem(vm)
                Electric(vm)
                NotificationsCenter()
                HotWater()
                SchoolCalendar()
                //NextSchedule()
            } else {
                Grade(vm,ifSaved)
                Exam(vm)
                Program(vm)
                PersonUI()
                EmptyRoom(vm)
                SchoolCardItem(vm)
                LibraryItem(vm)
                FailRate(vm)
                CourseTotal(vm)
                XuanquItem(vm)
                Electric(vm)
                WebUI()
                LePaoYun(vm)
                NotificationsCenter()
                HotWater()
                SchoolCalendar()
             ///   NextSchedule()
                Estimate(vm)
                SchoolBus()
                Second()
                //XueGong()
            }
             Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

fun Refresh(vm : LoginSuccessViewModel, rotating: MutableState<Boolean>) {
    Handler(Looper.getMainLooper()).post{vm.ExamData.value = "{}"}
    val CommuityTOKEN = prefs.getString("TOKEN","")
    var term = ""
    val month = GetDate.Date_MM.toInt()
    if( month >= 8 || month <= 2) term = "1"
    else term = "2"
    var years = GetDate.Date_yyyy
    if (month <= 2) years = (years.toInt() - 1).toString()
    rotating.value = true
    CoroutineScope(Job()).launch {
        vm.apply {
            async {
                async { CommuityTOKEN?.let { GetProgram(it) } }
                async { CommuityTOKEN?.let { GetHistory(it,"1") } }
                async { CommuityTOKEN?.let { GetCourse(it) } }
                async { CommuityTOKEN?.let { Exam(it) } }
                async { CommuityTOKEN?.let { GetBorrowed(it,"1") } }
                async { CommuityTOKEN?.let { vm.getGrade(it,years+"-"+(years.toInt()+1),term) } }
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