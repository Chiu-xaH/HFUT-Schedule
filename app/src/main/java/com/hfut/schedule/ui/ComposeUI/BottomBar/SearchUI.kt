package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.ui.ComposeUI.Search.FWDT
import com.hfut.schedule.ui.ComposeUI.Search.Library
import com.hfut.schedule.ui.ComposeUI.Search.Person
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard
import com.hfut.schedule.ui.ComposeUI.Search.emptyRoomUI

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.jsoup.Jsoup

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : JxglstuViewModel) {

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)

    val card =prefs.getString("card","正在获取")
    val borrow =prefs.getString("borrow","正在获取")
    val sub =prefs.getString("sub","正在获取")

//解析图书数据//////////////////////////////////////////////////////////////////


  /////////////////////////////////////////////////////////////////@#$%^&*()

    if (card == "请登录刷新" && vm.token.value?.contains("AT") == true) {
        CoroutineScope(Job()).apply {
            async { vm.getCard("Bearer " + vm.token.value) }
            async { vm.getBorrowBooks("Bearer " + vm.token.value) }
            async { vm.getSubBooks("Bearer " + vm.token.value) }
            async {
                delay(500)

            }
        }
    }


    var view by rememberSaveable { mutableStateOf("") }

    fun ExamGet() : List<Map<String,String>>{
        //考试JSON解析

        val examjson = prefs.getString("exam",MyApplication.NullExam)

        val doc = Jsoup.parse(examjson).select("tbody tr")

        val data = doc.map { row ->
            val elements = row.select("td")
            val courseName = elements[0].text()
            val examRoom = elements[2].text()
            val  examtime = elements[1].text()
            mapOf("课程名称" to courseName,
                "日期时间" to examtime,
                "考场" to examRoom)
        }
        return data
    }

    val info = prefs.getString("info","")
    val doc = info?.let { Jsoup.parse(it) }
    val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("嗨  亲爱的 ${name} 同学") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {
/////////////////////////////////////////////////////////////////////////////////////
            val sheetState_Grade = rememberModalBottomSheetState()
            var showBottomSheet_Grade by remember { mutableStateOf(false) }

            ListItem(
                        headlineContent = { Text(text = "成绩单") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.article),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            val grade = prefs.getString("grade", "")
                            showBottomSheet_Grade = true
                            view = "空"
                        }
                    )


            if (showBottomSheet_Grade ) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_Grade = false
                    },
                    sheetState = sheetState_Grade
                ) {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "暂未开发")
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }


                }
            }
//////////////////////////////////////////////////////////////////////////////////
            val sheetState_Exam = rememberModalBottomSheetState()
            var showBottomSheet_Exam by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text(text = "考试查询") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.draw),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {

                            showBottomSheet_Exam = true

                         ExamGet()
                        }
                    )

            if (showBottomSheet_Exam) {

                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_Exam = false
                    },
                    sheetState = sheetState_Exam
                ) {
                    LazyColumn {
                        items(ExamGet()) {item ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column() {
                                    ListItem(
                                        headlineContent = {  Text(text = "${item["课程名称"]}") },
                                        overlineContent = {Text(text = "${item["日期时间"]}")},
                                        supportingContent = { Text(text = "${item["考场"]}")},
                                        leadingContent = {
                                            Icon(
                                                painterResource(R.drawable.schedule),
                                                contentDescription = "Localized description",
                                            )
                                        },
                                        modifier = Modifier.clickable {

                                        }
                                    )

                                }
                            }
                        }
                    }
                }
            }

////////////////////////////////////////////////////////////////////////////////
            val sheetState_Program = rememberModalBottomSheetState()
            var showBottomSheet_Program by remember { mutableStateOf(false) }


            ListItem(
                        headlineContent = { Text(text = "培养方案") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.conversion_path),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            val program = prefs.getString("program", " <h2 class=\"info-title\"><i style=\"color: #ffa200;\" class=\"fa fa-warning highlight\"></i>未获取到</h2>")
                            showBottomSheet_Program = true
                            val doc = Jsoup.parse(program)
                            view = doc.select("h2.info-title").text()
                        }
                    )



            if (showBottomSheet_Program ) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_Program = false
                    },
                    sheetState = sheetState_Program
                ) {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = view)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }


                }
            }
/////////////////////////////////////////////////////////////////////////////////////
            val sheetState_Person = rememberModalBottomSheetState()
            var showBottomSheet_Person by remember { mutableStateOf(false) }

            ListItem(
                        headlineContent = { Text(text = "个人信息") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.person),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable { showBottomSheet_Person = true }
                    )

            if (showBottomSheet_Person) {

                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_Person = false
                    },
                    sheetState = sheetState_Person
                ) {
                    Person()
                }
            }
///////////////////////////////////////////////////////////////空教室//////////////////
            val sheetState_EmptyRoom = rememberModalBottomSheetState()
            var showBottomSheet_EmptyRoom by remember { mutableStateOf(false) }

                    ListItem(
                        headlineContent = { Text(text = "空教室") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.meeting_room),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
                            val token = prefs.getString("bearer","")
                            showBottomSheet_EmptyRoom = true
                            token?.let { vm.searchEmptyRoom("XC001", it) }
                            token?.let { vm.searchEmptyRoom("XC002", it) }
                           // view = "待开发"


                        }
                    )

            if (showBottomSheet_EmptyRoom) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_EmptyRoom = false
                    },
                    sheetState = sheetState_EmptyRoom
                ) {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            emptyRoomUI(vm)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }


                }
            }

///////////////////////////////////////////////////////////////一卡通//////////////////

                    SchoolCard()

/////////////////////////////////////////////////////////////////////////////////////////
          Library(vm)
//////////////////////////////////////////////////////////////////////////////////////
            val sheetState_Total = rememberModalBottomSheetState()
            var showBottomSheet_Total by remember { mutableStateOf(false) }

                    ListItem(
                        headlineContent = { Text(text = "本学期课程") },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.calendar_view_month),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable { showBottomSheet_Total = true }
                    )

            if (showBottomSheet_Total) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet_Total = false
                    },
                    sheetState = sheetState_Total
                ) {
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "暂未开发")
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }


                }
            }

////////////////////////////////////////////////////////////////////////////////
           FWDT()
//////////////////////////////////////////////////////////////////////////////////
        }
    }

}