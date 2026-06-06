package com.hfut.schedule.ui.screen.home.search.function.my.webLab

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.WebFolderDestination

import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabUI() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val list = remember { getLab() }
    DividerTextExpandedWith(text = "实验室") {
        if(list.isEmpty()) {
            EmptyIcon()
        } else {
            for(item in list) {
                CardListItem(
                    headlineContent = { Text(text = item.title) },
                    leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                    modifier = Modifier.clickable {
                        scope.launch {
                            Starter.startWebUrlInner(context ,item.info,item.title, icon= WebFolderDestination.icon)
                        }
                    }
                )
            }
        }
    }
}
