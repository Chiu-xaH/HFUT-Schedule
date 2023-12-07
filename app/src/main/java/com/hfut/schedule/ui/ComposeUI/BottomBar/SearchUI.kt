package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.GetDate
import com.hfut.schedule.logic.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.Search.EmptyRoom
import com.hfut.schedule.ui.ComposeUI.Search.Exam
import com.hfut.schedule.ui.ComposeUI.Search.FWDT
import com.hfut.schedule.ui.ComposeUI.Search.Grade
import com.hfut.schedule.ui.ComposeUI.Search.LibraryItem
import com.hfut.schedule.ui.ComposeUI.Search.Person
import com.hfut.schedule.ui.ComposeUI.Search.PersonUI
import com.hfut.schedule.ui.ComposeUI.Search.Program
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.SchoolCardItem
import com.hfut.schedule.ui.ComposeUI.Search.UndevelipItem
import com.hfut.schedule.ui.ComposeUI.Search.XuanquItem
import com.hfut.schedule.ui.ComposeUI.Search.emptyRoomUI
import com.hfut.schedule.ui.ComposeUI.Search.WebUI

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
            async { vm.getCard("Bearer " + vm.token.value) }
            async { vm.getBorrowBooks("Bearer " + vm.token.value) }
            async { vm.getSubBooks("Bearer " + vm.token.value) }
        }
    }

    val info = prefs.getString("info","")
    val doc = info?.let { Jsoup.parse(it) }
    val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
    return name
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : LoginSuccessViewModel) {

    getName(vm)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("嗨  亲爱的 ${getName(vm)} 同学") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
             Grade()
             Exam()
             Program()
             PersonUI()
             EmptyRoom(vm)
             SchoolCardItem(vm)
             LibraryItem(vm)
             XuanquItem(vm)
             WebUI()
             FWDT()
             UndevelipItem()

             Spacer(modifier = Modifier.height(90.dp))
        }
    }

}