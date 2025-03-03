package com.hfut.schedule.ui.activity.home.search.functions

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
import com.hfut.schedule.logic.utils.data.SharePrefs
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog

@Composable
fun Huixin() {
    val auth = SharePrefs.prefs.getString("auth","")

    var showDialog_Huixin by remember { mutableStateOf(false) }

    val urlHuixin = MyApplication.ZJGDBillURL + "plat" + "?synjones-auth=" + auth

    WebDialog(showDialog_Huixin,{ showDialog_Huixin = false },urlHuixin,"慧新易校")

    TransplantListItem(
        headlineContent = { Text(text = "肥区缴费") },
        overlineContent = { Text("惠新易校平台") },
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