package com.hfut.schedule.ui.screen.home.search.function.loginWeb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.WebDialog
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.bottomSheetRound
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState
import org.jsoup.Jsoup
import java.math.BigDecimal
import java.math.RoundingMode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginWeb(vmUI : UIViewModel, card : Boolean, vm : NetWorkViewModel, hazeState: HazeState) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val memoryWeb = prefs.getString("memoryWeb","0")

    val flow = vmUI.webValue.value?.flow?: memoryWeb
    val gB = (flow?.toDouble() ?: 0.0) / 1024
//    val bd = BigDecimal(gB)
    val str = formatDecimal(gB,2)

//    val bd2 = BigDecimal()
    val precent = formatDecimal(((flow?.toDouble() ?: 0.0) / (1024 * MyApplication.MAX_FREE_FLOW)) * 100,1)

    TransplantListItem(
        headlineContent = { if(!card)Text(text = "校园网"  + if(getCampus() != Campus.XUANCHENG) "(宣)" else "") else ScrollText(text = "${str}GB") },
        overlineContent = { if(!card) ScrollText(text = "${vmUI.webValue.value?.flow?: memoryWeb}MB") else Text(text = "校园网 ${precent}%")},
        leadingContent = { Icon(
            painterResource(R.drawable.net),
            contentDescription = "Localized description",
        ) },
        modifier = Modifier.clickable { showBottomSheet = true }
    )

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
//            sheetState = sheetState,
//            shape = Round(sheetState)
        ) {
            LoginWebScaUI(vmUI, vm,hazeState)
        }
    }
}



