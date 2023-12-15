package com.hfut.schedule.ui.ComposeUI.Search

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.activity.FWDTActivity
import com.hfut.schedule.ui.ComposeUI.Dialog

fun startFWDT(num : String) {
    val it = Intent(MyApplication.context, FWDTActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("boolean",num)
    }
    MyApplication.context.startActivity(it)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FWDT() {

    var openAlertDialog by remember { mutableStateOf(false) }

    if (openAlertDialog) {
        Dialog(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = { startFWDT("0") },
            dialogTitle = "服务大厅登录",
            dialogText = "即将跳转到服务大厅,在此之前需要确定您的服务大厅密码是否修改过,未修改可快速登录",
           // icon = Icons.Filled.Info,
            conformtext = "未修改(默认)",
            dismisstext = "已修改"
        )
    }

    ListItem(
        headlineContent = { Text(text = "服务大厅") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.redeem),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { openAlertDialog = true }
    )
}