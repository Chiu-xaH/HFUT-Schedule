package com.hfut.schedule.ui.ComposeUI.Search

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.OpenAlipay
@Composable
fun SchoolCard() {

    val prefs = MyApplication.context.getSharedPreferences("com.hfut.schedule_preferences", Context.MODE_PRIVATE)
    var card =prefs.getString("card","正在获取")

    ListItem(
        headlineContent = { Text(text = "一卡通余额   ${card} 元") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.credit_card),
                contentDescription = "Localized description",
            )
        },
        trailingContent={
            FilledTonalIconButton(onClick = {
                OpenAlipay.openAlipay()
            }) {
                Icon( painterResource(R.drawable.add),
                    contentDescription = "Localized description",)
            }
        },
        modifier = Modifier.clickable {}
    )
}