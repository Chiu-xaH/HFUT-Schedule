package com.hfut.schedule.ui.ComposeUI

import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.activity.FWDTLoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoNetWorkOne(vm : JxglstuViewModel) {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val token = prefs.getString("bearer","")



    if (token != null) {
        if (token.contains("AT")) {
            CoroutineScope(Job()).apply {
                async { vm.getCard("Bearer $token") }
                async { vm.getSubBooks("Bearer $token") }
                async { vm.getBorrowBooks("Bearer $token") }

            }

        }
    }

    val card =prefs.getString("card","正在获取")
    val borrow =prefs.getString("borrow","正在获取")
    val sub =prefs.getString("sub","正在获取")

    var showBottomSheet2 by remember { mutableStateOf(false) }
    val sheetState2 = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("信息门户") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {


            ListItem(
                headlineContent = { Text(text = "一卡通余额   ${card} 元") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.credit_card),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {

                }
            )



            ListItem(
                headlineContent = { Text(text = "图书服务   借阅 ${borrow} 本   预约 ${sub} 本") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.book),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {

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
                    showBottomSheet2 = true
                    vm.searchEmptyRoom2("XC001","Bearer $token")
                    vm.searchEmptyRoom2("XC002","Bearer $token")


                }
            )
            if (showBottomSheet2) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet2 = false
                    },
                    sheetState = sheetState2
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

            ListItem(
                headlineContent = { Text(text = "费用查询") },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.redeem),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable {
                    Toast.makeText(MyApplication.context, "暂未开发", Toast.LENGTH_SHORT)
                        .show()
                }
            )











        }
    }
}