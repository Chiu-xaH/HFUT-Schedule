package com.hfut.schedule.ui.Activity.Fix

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginViewModel
import com.hfut.schedule.logic.utils.APPVersion
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.ShareAPK
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.success.cube.Settings.getUpdates
import com.hfut.schedule.ui.Activity.success.focus.Focus.getTimeStamp
import com.hfut.schedule.ui.UIUtils.MyToast

@Composable
fun AboutUI(innerPadding : PaddingValues, vm : LoginViewModel) {
    Column (modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(innerPadding)){
        Spacer(modifier = Modifier.height(5.dp))

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp),
            shape = MaterialTheme.shapes.medium

        ){

        }
        Spacer(modifier = Modifier.height(5.dp))


        ListItem(
            headlineContent = { Text(text = "开源主页") },
            supportingContent = { Text(text = "欢迎来开源主页参观一下")},
            leadingContent = {
                Icon(
                    painterResource(R.drawable.net),
                    contentDescription = "Localized description",
                )
            },
            modifier = Modifier.clickable{
                //when(select) {
                //   false -> StartUri("https://gitee.com/chiu-xah/HFUT-Schedule")
                //true ->
                StartApp.StartUri("https://github.com/Chiu-xaH/HFUT-Schedule")
                //  }
            }
        )
        ListItem(
            headlineContent = { Text(text = "联系开发者") },
            leadingContent = {
                Icon(
                    painterResource(R.drawable.mail),
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
            headlineContent = { Text(text = "推广本应用") },
            supportingContent = { Text(text = "如果你觉得好用的话,可以替开发者多多推广")},
            leadingContent = {
                Icon(
                    painterResource(R.drawable.ios_share),
                    contentDescription = "Localized description",
                )
            },
            modifier = Modifier.clickable{ ShareAPK.ShareAPK() }
        )

        var version by remember { mutableStateOf(getUpdates()) }
        var showBadge by remember { mutableStateOf(false) }
        if (version.version != APPVersion.getVersionName()) showBadge = true

        ListItem(
            headlineContent = { Text(text = "获取更新") },
            supportingContent = { Text(text = if(version.version == APPVersion.getVersionName()) "当前为最新版本 ${APPVersion.getVersionName()}" else "当前版本  ${APPVersion.getVersionName()}\n最新版本  ${version.version}") },
            leadingContent = {
                BadgedBox(badge = {
                    if(showBadge)
                        Badge(modifier = Modifier.size(7.dp)) }) {
                    Icon(painterResource(R.drawable.arrow_upward), contentDescription = "Localized description",)
                }
            },
            modifier = Modifier.clickable{
                if (version.version != APPVersion.getVersionName())
                    StartApp.StartUri("https://gitee.com/chiu-xah/HFUT-Schedule/releases/tag/Android")
                else Toast.makeText(MyApplication.context,"与云端版本一致", Toast.LENGTH_SHORT).show()
            }
        )


        //Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
    }
}
