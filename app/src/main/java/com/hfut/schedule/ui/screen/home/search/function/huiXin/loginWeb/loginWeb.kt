package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWeb(vmUI : UIViewModel, card : Boolean, vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }

    when(getCampus()) {
        Campus.HEFEI -> {
            TransplantListItem(
                headlineContent = { if(!card)Text(text = "校园网") else ScrollText(text = "登录") },
                overlineContent = { if(!card) ScrollText(text = "登录") else Text(text = "校园网")},
                leadingContent = { Icon(
                    painterResource(R.drawable.net),
                    contentDescription = "Localized description",
                ) },
                modifier = Modifier.clickable { showBottomSheet = true }
            )
        }
        Campus.XUANCHENG -> {
            val memoryWeb = prefs.getString("memoryWeb","0")

            val flow = vmUI.webValue.value?.flow?: memoryWeb
            val gB = (flow?.toDouble() ?: 0.0) / 1024
            val str = formatDecimal(gB,2)

            val precent = formatDecimal(((flow?.toDouble() ?: 0.0) / (1024 * MyApplication.MAX_FREE_FLOW)) * 100,1)

            TransplantListItem(
                headlineContent = { if(!card)Text(text = "校园网") else ScrollText(text = "${str}GB") },
                overlineContent = { if(!card) ScrollText(text = "${vmUI.webValue.value?.flow?: memoryWeb}MB") else Text(text = "校园网 ${precent}%")},
                leadingContent = { Icon(
                    painterResource(R.drawable.net),
                    contentDescription = "Localized description",
                ) },
                modifier = Modifier.clickable { showBottomSheet = true }
            )
        }
    }


    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            LoginWebScaUI(vmUI, vm,hazeState)
        }
    }
}



