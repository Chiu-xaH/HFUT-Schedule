package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : JxglstuViewModel) {
    //待开发
    // 考试安排 //培养方案 //空教室 //一卡通
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val cookie = prefs.getString("redirect", "")
    val ONE = prefs.getString("ONE","")
    val TGC = prefs.getString("TGC","")
    CoroutineScope(Job()).apply {
        launch { vm.getExam(cookie!!) }
        launch { vm.getProgram(cookie!!) }
        launch { vm.getGrade(cookie!!) }
        launch { vm.Onelogin(ONE + ";" + TGC) }
    }





    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    var view by rememberSaveable { mutableStateOf("") }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("查询中心") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()) {
                // Spacer(modifier = Modifier.height(15.dp))

                ListItem(
                    headlineContent = { Text(text = "成绩单") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.article),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        val grade = prefs.getString("grade","")
                        showBottomSheet = true
                        view = "空"
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "考试查询") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.edit_note),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        val exam = prefs.getString("exam","")
                        showBottomSheet = true
                        view = "空"
                    }
                )




                ListItem(
                    headlineContent = { Text(text = "培养方案") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.conversion_path),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        val program = prefs.getString("program","")
                        showBottomSheet = true
                        val doc = Jsoup.parse(program)
                        view = doc.select("h2.info-title").text()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "空教室") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.meeting_room),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        view = "待开发"

                    }
                )

                ListItem(
                    headlineContent = { Text(text = "一卡通余额   XX元") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.credit_card),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"待开发",Toast.LENGTH_SHORT).show()
                    }
                )




                ListItem(
                    headlineContent = { Text(text = "图书服务   借阅X本   预约X本") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.book),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"待开发",Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "本学期课程") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.calendar_view_month),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = true
                        view = "暂未开发"
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "全校课表查询") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.travel_explore),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                       Toast.makeText(MyApplication.context,"暂未开发",Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "服务大厅") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.redeem),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"暂未开发",Toast.LENGTH_SHORT).show()
                    }
                )





                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        Column() {
                            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                                Text(text = view)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }


                    }
                }



            }
        }
    }

   // ListItem(
     //   headlineText = { Text(text = "暂未开发 敬请期待") },
        // supportingText =
       // leadingContent = {
         //   Icon(
           //     painterResource(R.drawable.error),
             //   contentDescription = "Localized description",
           // )
       // },
        //modifier = Modifier.clickable{

        //}
    //)
}