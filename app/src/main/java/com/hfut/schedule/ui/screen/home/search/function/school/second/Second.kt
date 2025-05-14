package com.hfut.schedule.ui.screen.home.search.function.school.second

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Second() {

    var showDialog by remember { mutableStateOf(false) }

    val url = "https://wxaurl.cn/EDy2PLrcrih"

    TransplantListItem(
        headlineContent = { Text(text = "第二课堂") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.hotel_class),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showToast("请前往 第二课堂 微信小程序")
        }
    )
}