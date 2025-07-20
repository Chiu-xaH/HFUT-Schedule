package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, vmUI : UIViewModel, hazeState: HazeState) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }


    val EndNumber = prefs.getString("EndNumber", "0")
    var room by remember { mutableStateOf("寝室电费") }
    if(EndNumber == "12" || EndNumber == "22") room = "空调"
    else if(EndNumber == "11" || EndNumber == "21") room = "照明"


    if (showBottomSheet) {

        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
//            sheetState = sheetState
        ) {
            EleUI(vm = vm,hazeState)
            }
        }

    val memoryEle = prefs.getString("memoryEle","0")

    TransplantListItem(
        headlineContent = { if(!card)Text(text = "寝室电费" ) else ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") },
        overlineContent = { if(!card) ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") else ScrollText(text = room) },
        leadingContent = { Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}


