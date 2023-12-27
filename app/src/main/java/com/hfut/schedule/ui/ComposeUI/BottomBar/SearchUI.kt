package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.Indevelopment.CourseTotal
import com.hfut.schedule.ui.ComposeUI.Search.EmptyRoom.EmptyRoom
import com.hfut.schedule.ui.ComposeUI.Search.Indevelopment.Estimate
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
import com.hfut.schedule.ui.ComposeUI.Search.Indevelopment.SchoolBus
import com.hfut.schedule.ui.ComposeUI.Search.Electric.Electric
import com.hfut.schedule.ui.ComposeUI.Search.Indevelopment.Second
import com.hfut.schedule.ui.ComposeUI.Search.Indevelopment.XueGong
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

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : LoginSuccessViewModel,ifSaved : Boolean,) {

    getName(vm)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    if (ifSaved) Text("免登录查询") else
                        Text("嗨  亲爱的 ${getName(vm)} 同学")
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
            if (ifSaved){
                SchoolCardItem(vm)
                LibraryItem(vm)
                XuanquItem(vm)
                WebUI()
                LePaoYun(vm)
                Electric(vm)
                NotificationsCenter()

                if (prefs.getBoolean("SWITCHBETA",false)){
                    HotWater()
                }
            } else {
                Grade(vm)
                Exam()
                Program()
                PersonUI()
                EmptyRoom(vm)
                SchoolCardItem(vm)
                LibraryItem(vm)
                XuanquItem(vm)
                WebUI()
                LePaoYun(vm)
                Electric(vm)
                NotificationsCenter()

                if (prefs.getBoolean("SWITCHBETA",false)){
                    HotWater()
                    Estimate()
                    SchoolBus()
                    Second()
                    CourseTotal()
                    XueGong()
                }
            }

             Spacer(modifier = Modifier.height(90.dp))
        }
    }

}