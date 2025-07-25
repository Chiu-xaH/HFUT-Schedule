package com.hfut.schedule.ui.screen.home.search.function.huiXin

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.webview.WebDialog

@Composable
fun Huixin() {
    val auth = SharedPrefs.prefs.getString("auth","")

    var showDialog_Huixin by remember { mutableStateOf(false) }

    val urlHuixin = MyApplication.HUIXIN_URL + "plat" + "?synjones-auth=" + auth

    WebDialog(showDialog_Huixin,{ showDialog_Huixin = false },urlHuixin,"慧新易校", showTop = false)

    TransplantListItem(
        headlineContent = { Text(text = "生活缴费") },
        overlineContent = { Text("慧新易校") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.corporate_fare),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showDialog_Huixin = true
        }
    )
}