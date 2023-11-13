package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.LoginActivity

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
                title = { Text("肥工教务通") }
            )
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            //.background()插入背景
        ) {

            ListItem(
                headlineContent = { Text(text = "壁纸取色") },
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
                          //  checked = it
                                          Toast.makeText(MyApplication.context,"Android 12及以上默认开启,无需修改",Toast.LENGTH_SHORT).show()
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
                headlineContent = { Text(text = "获取更新") },
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.net),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    val it = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Chiu-xaH/HFUT-Schedule/releases/tag/Android"))
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context.startActivity(it)
                }
            )
            ListItem(
                headlineContent = { Text(text = "向我反馈") },
                // supportingText =
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.feedback),
                        contentDescription = "Localized description",
                    )
                },
                modifier = Modifier.clickable{
                    val it = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:zsh0908@outlook.com"))
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    MyApplication.context.startActivity(it)
                }
            )
            ListItem(
                headlineContent = { Text(text = "清除数据") },
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
                    Toast.makeText(MyApplication.context,"已清除缓存", Toast.LENGTH_SHORT).show()
                }
            )
            ListItem(
                headlineContent = { Text(text = "切换账号") },
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