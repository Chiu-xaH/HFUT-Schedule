package com.hfut.schedule.ui.activity.home.cube.funictions.items

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.beans.MyAPIResponse
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.ui.utils.APIIcons
import com.hfut.schedule.ui.utils.DividerText
import com.hfut.schedule.ui.utils.MyCard

@Composable
fun MyAPIItem() {
    val my = prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, MyAPIResponse::class.java).SettingsInfo
    var title = data.title
    var content = data.info
    val version = data.version

    val celebration = data.celebration

    var show by remember { mutableStateOf(data.show) }


    val id = prefs.getString("ChineseId", "")
    if (id != null) {
        if (id.length == 18) {
            val birthday = id.substring(10, 14)
            val today = GetDate.Date_MM_dd.replace("-", "")
            if (today == birthday) {
               content = Birthday()
                title = "Happy Birthday !"
            }
        }
    }

    if(show) {
        DividerText(text = "重要通知")
        MyCard {
            ListItem(
                headlineContent = {
                    Text(text = title, fontWeight = FontWeight.Bold)
                },
                supportingContent = { Text(text = content) },
                leadingContent = { APIIcons(celebration = celebration) },
                modifier = Modifier.clickable{}
            )
        }
    }


}


@Composable
fun Birthday() : String {
    val id = prefs.getString("ChineseId", "")
    var age = ""
    var info = ""
    if (id != null) {
        if (id.length == 18) {
            val year = id.substring(6, 10)
            val birthday = id.substring(10, 14)
            val todayYear = GetDate.Date_yyyy.toInt()
            val today = GetDate.Date_MM_dd.replace("-", "")
            if (today == birthday) {
                age = " " + (todayYear - year.toInt()).toString() + " 岁"
                info = "祝你${age}生日快乐"
            }
        }
    }
    return info
}