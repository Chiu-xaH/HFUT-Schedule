package com.hfut.schedule.ui.activity.home.search.functions.ietp

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
import com.hfut.schedule.logic.utils.parse.ParseJsons.getMy
import com.hfut.schedule.ui.utils.components.MyToast
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog


@Composable
fun IETP() {
    var showDialog by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "大创") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.groups), contentDescription = "") },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )


    WebDialog(showDialog,{showDialog = false}, MyApplication.IETP_URL,"大创系统")
}
