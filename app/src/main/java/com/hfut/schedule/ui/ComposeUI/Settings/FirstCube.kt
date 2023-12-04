package com.hfut.schedule.ui.ComposeUI.Settings

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.DebugActivity

fun StartDebug() {
     val it =
        Intent(MyApplication.context, DebugActivity::class.java).apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    MyApplication.context.startActivity(it)
}
@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstCube( ) {
    MyAPIItem()
    Spacer(modifier = Modifier.height(5.dp))
    MonetColorItem()
    SettingsItems()
    ListItem(
        headlineContent = { Text(text = "调试模式") },
        modifier = Modifier.clickable { StartDebug() },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.api), contentDescription ="" )}
    )
}