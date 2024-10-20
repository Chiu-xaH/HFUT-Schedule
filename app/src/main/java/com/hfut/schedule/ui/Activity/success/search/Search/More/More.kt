package com.hfut.schedule.ui.Activity.success.search.Search.More

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.LoginActivity
import com.hfut.schedule.activity.ShowerActivity
import com.hfut.schedule.ui.UIUtils.ScrollText

@Composable
fun More() {
    ListItem(
        headlineContent = { Text(text = "登录教务") },
        overlineContent = { ScrollText(text = "当前未登录教务,部分功能不可用,可从此登录") },
        leadingContent = { Icon(painter = painterResource(R.drawable.login), contentDescription = "") },
        modifier = Modifier.clickable {
           Login()
        }
    )
}

fun Login() {
    val it = Intent(MyApplication.context, LoginActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("nologin",false)
    }
    MyApplication.context.startActivity(it)
}
fun LoginGuaGua() {
    val it = Intent(MyApplication.context, ShowerActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("nologin",false)
    }
    MyApplication.context.startActivity(it)
}