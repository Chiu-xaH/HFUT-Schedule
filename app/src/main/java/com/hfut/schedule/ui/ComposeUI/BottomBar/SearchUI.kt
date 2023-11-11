package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm : JxglstuViewModel) {
    //待开发
    // 考试安排 //培养方案 //空教室 //一卡通
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val cookie = prefs.getString("redirect", "")

    CoroutineScope(Job()).apply {
        launch { vm.getExam(cookie!!) }
        launch { vm.getProgram(cookie!!) }
        launch { vm.getGrade(cookie!!) }
    }


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
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "一卡通") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.credit_card),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "图书服务") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.book),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"待开发,敬请期待",Toast.LENGTH_SHORT).show()
                    }
                )

                ListItem(
                    headlineContent = { Text(text = "更多") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.more_horiz),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        Toast.makeText(MyApplication.context,"没有更多了,如果有更多点子可以告诉我哦",Toast.LENGTH_SHORT).show()
                    }
                )


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