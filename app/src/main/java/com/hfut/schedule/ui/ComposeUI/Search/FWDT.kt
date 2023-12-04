package com.hfut.schedule.ui.ComposeUI.Search

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
fun FWDT() {
    ListItem(
        headlineContent = { Text(text = "服务大厅") },
        supportingContent = { Text(text = "暂未开发")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.redeem),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            Toast.makeText(MyApplication.context, "暂未开发", Toast.LENGTH_SHORT).show()
          // val it =
            //    Intent(MyApplication.context, DebugActivity::class.java).apply {
              //      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               // }
            //MyApplication.context.startActivity(it)
        }
    )
}