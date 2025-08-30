package com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWeb(vmUI : UIViewModel, card : Boolean, vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val maxFlow by DataStoreManager.maxFlow.collectAsState(initial = MyApplication.DEFAULT_MAX_FREE_FLOW)

    when(getCampusRegion()) {
        CampusRegion.HEFEI -> {
            TransplantListItem(
                headlineContent = { if(!card)ScrollText(text = "校园网") else ScrollText(text = "登录") },
                overlineContent = { if(!card) ScrollText(text = "-- MB") else ScrollText(text = "校园网")},
                leadingContent = { Icon(
                    painterResource(R.drawable.net),
                    contentDescription = "Localized description",
                ) },
                modifier = Modifier.clickable { showBottomSheet = true }
            )
        }
        CampusRegion.XUANCHENG -> {
            var showPercent by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                showPercent = true
                delay(3000L)
                showPercent = false
            }
            val memoryWeb = prefs.getString("memoryWeb","0")

            val flow = vmUI.webValue.value?.flow?: memoryWeb
            val gB = (flow?.toDouble() ?: 0.0) / 1024
            val str = formatDecimal(gB,2)

            val precent = formatDecimal(((flow?.toDouble() ?: 0.0) / (1024 * maxFlow)) * 100,2)

            TransplantListItem(
                headlineContent = { if(!card)ScrollText(text = "校园网") else ScrollText(text =
                    if(showPercent) "${precent}%" else "$str GiB"
                ) },
                overlineContent = { if(!card) ScrollText(text = "${vmUI.webValue.value?.flow?: memoryWeb} MB") else ScrollText(text = "校园网")},
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



