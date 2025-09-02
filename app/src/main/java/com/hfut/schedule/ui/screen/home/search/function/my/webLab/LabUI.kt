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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.screen.AppNavRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabUI() {
    val context = LocalContext.current
    for(item in getLab()) {
//        MyCustomCard {
            CardListItem(
                headlineContent = { Text(text = item.title) },
                leadingContent = { Icon(painterResource(R.drawable.net), contentDescription = "Localized description",) },
                modifier = Modifier.clickable {
                    Starter.startWebView(context ,item.info,item.title, icon=AppNavRoute.WebNavigation.icon)
                }
            )
//        }
    }
}
