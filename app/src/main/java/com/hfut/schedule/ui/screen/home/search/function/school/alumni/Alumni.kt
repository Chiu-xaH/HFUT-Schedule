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
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog

@Composable
fun Alumni() {
    var showDialog by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { Text(text = "校友平台") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.local_library), contentDescription = "") },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    WebDialog(showDialog,{ showDialog = false },MyApplication.ALUMNI_URL,"校友平台")
}