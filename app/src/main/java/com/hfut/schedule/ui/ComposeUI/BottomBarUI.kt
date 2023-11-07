package com.hfut.schedule.ui.ComposeUI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.activity.LoginSuccessAcitivity
import okhttp3.internal.addHeaderLenient


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var checked by remember { mutableStateOf(true) }
    val openAlertDialog = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("肥工课程表") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {

            ListItem(
                headlineText = { Text(text = "壁纸取色")},
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.color),
                        contentDescription = "Localized description",
                    )
                },
              //  modifier = Modifier.clickable{
                  //          Toast.makeText(MyApplication.context,"暂未开发,敬请期待",Toast.LENGTH_SHORT).show()
              //  },
            //    shadowElevation = 100.dp,
                trailingContent = {
                    Switch(
                        checked = checked, 
                        onCheckedChange = {
                            checked = it
                        },
                     //   thumbContent = if (checked) {
                      //      {
                        //        Icon(imageVector = Icons.Filled.Check,
                         //           contentDescription = null,
                          //          modifier = Modifier.size(SwitchDefaults.IconSize),
                           //     )
                          //  }
                       // }else null
                    )
                }
            )
            ListItem(
                headlineText = { Text(text = "前往主页")},
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.net),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    val it = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Chiu-xaH/HFUT-Schedule"))
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context.startActivity(it)
                }
            )
            ListItem(
                headlineText = { Text(text = "关于与反馈")},
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.feedback),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    if (openAlertDialog.value) {
                      //
                    }
                }
            )
            ListItem(
                headlineText = { Text(text = "清除缓存")},
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.delete),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
                    prefs.edit().clear().commit()
                   Toast.makeText(MyApplication.context,"已清除缓存",Toast.LENGTH_SHORT).show()
                }
            )
            ListItem(
                headlineText = { Text(text = "退出登录")},
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.logout),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    val it = Intent(MyApplication.context, LoginActivity::class.java)
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context.startActivity(it)
                }
            )

        }
    }
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
                title = { Text("嗨  工大人") }
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
              Spacer(modifier = Modifier.height(15.dp))
              Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                  Card(
                      elevation = CardDefaults.cardElevation(
                          defaultElevation = 8.dp
                      ),
                      modifier = Modifier
                          .size(width = 350.dp, height = 150.dp),
                      shape = MaterialTheme.shapes.medium,
                      onClick = {
                          //
                      }
                  ) {
                      Text(text = "姓名，班级，专业，学院，学号")
                  }
              }

              Spacer(modifier = Modifier.height(15.dp))

              Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                  Card(
                      elevation = CardDefaults.cardElevation(
                          defaultElevation = 10.dp
                      ),
                      modifier = Modifier
                          .size(width = 350.dp, height = 150.dp),
                      shape = MaterialTheme.shapes.medium,
                      onClick = {
                          //
                      }
                  ) {
                      Text(text = "临近课程")
                  }
              }
          }
        }
    }
    //待开发
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    //待开发
    // 考试安排 //培养方案 //空教室 //一卡通

    ListItem(
        headlineText = { Text(text = "暂未开发 敬请期待")},
        // supportingText =
        leadingContent = {
            Icon(
                painterResource(R.drawable.error),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable{

        }
    )
}
