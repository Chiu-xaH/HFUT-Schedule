package com.hfut.schedule.ui.screen.home.search.function.school.alumni

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
   

@Composable
fun Alumni() {
    val context = LocalContext.current
    TransplantListItem(
        headlineContent = { Text(text = "校友平台") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.local_library), contentDescription = "") },
        modifier = Modifier.clickable {
            Starter.startWebView(context,MyApplication.ALUMNI_URL,"校友平台", icon = R.drawable.local_library)
        }
    )
}