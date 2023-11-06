package com.hfut.schedule.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SettingsScreen() {


    //待开发
    //退出登录 //壁纸取色 //获取更新
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen() {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("嗨  亲爱的学子") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {
            Text(//text = "姓名 班级 学院 学号 今日课程",
                modifier = Modifier.padding(25.dp),
                fontSize = 25.sp,
                text = "")
        }
    }
    //待开发
}
@Composable
fun SearchScreen() {
    //待开发
    // 考试安排 //培养方案 //空教室 //一卡通
}