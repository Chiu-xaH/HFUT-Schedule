@file:Suppress("DEPRECATION")

package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Observer
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.HefeiElectricStorage
import com.xah.common.logic.util.LogUtil
import com.xah.common.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.UIViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CancellationException

private fun xuanchengElectricRegion(buildingNumber: String, endNumber: String): String? {
    val building = buildingNumber.toIntOrNull() ?: return null
    return when(endNumber) {
        "11" -> if(building > 5) "照明" else "南边"
        "12" -> "空调"
        "21" -> if(building > 5) "照明" else "北边"
        "22" -> "空调"
        else -> null
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, vmUI : UIViewModel, hazeState: HazeState) {
    val defaultRoomText = stringResource(R.string.navigation_label_dormitory_electricity_bill)
    val useHefei by DataStoreManager.useHefeiElectric.collectAsState(initial = getCampusRegion() == CampusRegion.HEFEI)

    LaunchedEffect(Unit) {
        try {
            DataStoreManager.migrateXuanchengElectricIfNeeded()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LogUtil.error(e, "迁移宣城电费配置失败")
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val room = if(useHefei) {
        val name by produceState<HefeiElectricStorage?>(initialValue = null) {
            value = DataStoreManager.getHefeiElectric()
        }
        name?.name ?: "合肥"
    } else {
        val xuanchengElectric by DataStoreManager.xuanchengElectric.collectAsState(initial = null)
        xuanchengElectric?.let { xuanchengElectricRegion(it.buildingNumber, it.endNumber) }
            ?: defaultRoomText
    }


    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = true,
            sheetGesturesEnabled = false
        ) {
            EleUI(vm = vm,hazeState)
        }
    }

    var electricValue by remember { mutableStateOf(vmUI.electricValue.value) }
    DisposableEffect(vmUI) {
        val observer = Observer<String?> { electricValue = it }
        vmUI.electricValue.observeForever(observer)
        onDispose { vmUI.electricValue.removeObserver(observer) }
    }
    val savedHefeiElectricFee by DataStoreManager.hefeiElectricFee.collectAsState(initial = "0")
    val f = electricValue ?: savedHefeiElectricFee
    val fD = f.toDoubleOrNull() ?: 0.0
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
                text = if(!card) stringResource(R.string.navigation_label_dormitory_electricity_bill) else "￥${f}",
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


