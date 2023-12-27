package com.hfut.schedule.ui.ComposeUI.Search.Exam

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.GetDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exam() {
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

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("考试查询") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    LazyColumn {
                        items(ExamGet()) { item ->

                            var date = GetDate.Date_MM_dd
                            val todaydate = (date?.substring(0, 2) ) + date?.substring(3, 5)
                            val get = item["日期时间"]
                            val examdate = (get?.substring(5, 7) ) + get?.substring(8, 10)
                            //判断考完试不显示信息
                          //  Log.d("exam",examdate)
                            //Log.d("today",todaydate)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Column() {
                                    Card(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 15.dp,
                                                vertical = 5.dp
                                            ),
                                        shape = MaterialTheme.shapes.medium,
                                    ){
                                        ListItem(
                                            headlineContent = {  Text(text = "${item["课程名称"]}") },
                                            overlineContent = { Text(text = "${item["日期时间"]}") },
                                            supportingContent = { Text(text = "${item["考场"]}") },
                                            leadingContent = {
                                                if(examdate.toInt() >= todaydate.toInt())
                                                    Icon(painterResource(R.drawable.schedule), contentDescription = "Localized description",)
                                                else Icon(Icons.Filled.Check, contentDescription = "Localized description",)
                                            },
                                            trailingContent = {
                                                if(examdate.toInt() < todaydate.toInt())
                                                    Text(text = "已结束")
                                            },
                                            modifier = Modifier.clickable {},
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}