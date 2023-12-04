package com.hfut.schedule.ui.ComposeUI.Settings

import android.content.Context
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
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.data4
@Composable
fun MyAPIItem() {
    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    val my = prefs.getString("my", MyApplication.NullMy)
    val data = Gson().fromJson(my, data4::class.java).SettingsInfo
    val title = data.title
    val content = data.info

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
            leadingContent = {
                Icon(
                    painterResource(R.drawable.error),
                    contentDescription = "Localized description",
                )
            },
            modifier = Modifier.clickable{}
        )
    }
}