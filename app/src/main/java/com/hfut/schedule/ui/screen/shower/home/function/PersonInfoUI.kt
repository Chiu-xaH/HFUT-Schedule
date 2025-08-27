package com.hfut.schedule.ui.screen.shower.home.function

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
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
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.ShowerUI
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.tranamt
import com.hfut.schedule.ui.screen.shower.login.getGuaGuaPersonInfo
import com.hfut.schedule.ui.component.container.LargeCard
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaPersonInfoUI(vm: NetWorkViewModel, hazeState: HazeState) {

    val sheetState = rememberModalBottomSheetState(true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
            autoShape = false
//            sheetState = sheetState
//            , shape = Round(sheetState)
        ) {
            ShowerUI(vm,true, hazeState = hazeState)
        }
    }

    val personInfo = getGuaGuaPersonInfo()
    personInfo?.let {
        LargeCard("￥${tranamt(it.accountMoney)}", rightTop = {
            FilledTonalIconButton(
                onClick = {
                    showBottomSheet = true
                },
            ) {
                Icon(painterResource(R.drawable.add),null)
            }
        }) {
            Row {
                TransplantListItem(
                    headlineContent = { Text(text = personInfo.name) },
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.person), contentDescription = "")
                    },
                    overlineContent = { Text(text = "姓名") },
                    modifier = Modifier.weight(.4f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = personInfo.telPhone) },
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.call), contentDescription = "")
                    },
                    overlineContent = { Text(text = "手机号") },
                    modifier = Modifier.weight(.6f)
                )
            }
        }
    }
}