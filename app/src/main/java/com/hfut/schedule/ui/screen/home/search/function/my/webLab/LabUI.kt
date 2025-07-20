package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.webview.WebDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabUI() {
    var showDialog by remember { mutableStateOf(false) }
    var num by remember { mutableStateOf(-1) }

    if(num != -1) {
        WebDialog(showDialog,{ showDialog = false },getLab()[num].info,getLab()[num].title)
    }


    StyleCardListItem(
        headlineContent = { Text(text = "选项会随云端发生变动,即使不更新软件") },
        leadingContent = { Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",) },
        color = Color.Transparent
    )
    for(item in 0 until getLab().size) {
//        MyCustomCard {
            StyleCardListItem(
                headlineContent = { Text(text = getLab()[item].title) },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    num = item
                    showDialog = true
                }
            )
//        }
    }
}
