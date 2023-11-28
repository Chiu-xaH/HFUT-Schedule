package com.hfut.schedule.ui.ComposeUI.Settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
@Composable
fun SettingsItems() {

    ListItem(
        headlineContent = { Text(text = "获取更新") },
        supportingContent = { Text(text = "当前版本  ${MyApplication.version}")},
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
            //崩溃操作
            val s = listOf("")
            println(s[2])
        }
    )

}