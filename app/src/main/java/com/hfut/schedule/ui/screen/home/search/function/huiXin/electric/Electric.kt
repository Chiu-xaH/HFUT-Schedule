package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.storage.DataStoreManager.HefeiElectricStorage
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, vmUI : UIViewModel, hazeState: HazeState) {
    val useHefei by DataStoreManager.useHefeiElectric.collectAsState(initial = getCampusRegion() == CampusRegion.HEFEI)
    var room by remember { mutableStateOf("寝室电费") }

    var showBottomSheet by remember { mutableStateOf(false) }
    if(useHefei) {
        val name by produceState<HefeiElectricStorage?>(initialValue = null) {
            value = DataStoreManager.getHefeiElectric()
        }
        room = name?.name ?: "合肥"
    } else {
        val buildingNumber = prefs.getString("BuildNumber", "0") ?: "0"

        room = when( prefs.getString("EndNumber", "0")) {
            "11"-> if(buildingNumber.toInt() > 5 )"照明" else "南边"
            "12" -> "空调"
            "21" -> if(buildingNumber.toInt() > 5 )"照明" else "北边"
            "22" -> "空调"
            else -> "寝室电费"
        }
    }


    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            hazeState = hazeState,
            showBottomSheet = showBottomSheet
        ) {
            EleUI(vm = vm,hazeState)
        }
    }

    val f = vmUI.electricValue.value ?: prefs.getString("memoryEle","0")
    val fD = f?.toDoubleOrNull() ?: 0.0
    val showRed = if(fD < 0.0) {
        // 爆红
        true
    } else if(fD > 0.0 && fD < 1.0) {
        // 爆红
        true
    } else {
        false
    }

    TransplantListItem(
        headlineContent = {
            ScrollText(
                text = if(!card) "寝室电费" else "￥${f}",
                color = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        },
        overlineContent = {
            ScrollText(
                text = if(!card) "￥${f}" else room,
                color = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            )
        },
        leadingContent = { Icon(
            painterResource(R.drawable.flash_on),
            contentDescription = "Localized description",
            tint = if(showRed) MaterialTheme.colorScheme.error else LocalContentColor.current
            ) },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}


