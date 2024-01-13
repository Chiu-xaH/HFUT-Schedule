package com.hfut.schedule.ui.ComposeUI.Settings.Items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.GetDate

@Composable
fun MyAPIItem() {
    val my = prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, MyAPIResponse::class.java).SettingsInfo
    var title = data.title
    var content = data.info
    val version = data.version
    val celebration = data.celebration


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


    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 5.dp),
        shape = MaterialTheme.shapes.medium

    ){
        ListItem(
            headlineContent = {
                Text(text = title, fontWeight = FontWeight.Bold)
            },
            supportingContent = { Text(text = content) },
            leadingContent = { APIIcons(celebration = celebration)},
            modifier = Modifier.clickable{}
        )
    }
}

@Composable
fun APIIcons(celebration: Boolean) {
    when {
        celebration -> Icon(painterResource(R.drawable.celebration), contentDescription = "Localized description",)
        Birthday().contains("生日") ->  Icon(painterResource(R.drawable.cake), contentDescription = "Localized description",)
        else -> Icon(painterResource(R.drawable.error), contentDescription = "Localized description",)
    }

}
@Composable
fun Birthday() : String {
    val id = prefs.getString("ChineseId", "")
    var age = ""
    if (id != null) {
        if (id.length == 18) {
            val year = id.substring(6, 10)
            val birthday = id.substring(10, 14)
            val todayYear = GetDate.Date_yyyy.toInt()
            val today = GetDate.Date_MM_dd.replace("-", "")
            if (today == birthday) {
                age = " " + (todayYear - year.toInt()).toString() + " 岁"
            }
        }
    }
    return "祝你${age}生日快乐"
}