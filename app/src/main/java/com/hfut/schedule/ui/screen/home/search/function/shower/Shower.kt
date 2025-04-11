package com.hfut.schedule.ui.screen.home.search.function.shower

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.screen.home.search.function.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.transfer.getCampus
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Shower(vm: NetWorkViewModel, hazeState: HazeState) {
    val sheetState = rememberModalBottomSheetState(true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
//            sheetState = sheetState
        ) {
            ShowerUI(vm,hazeState = hazeState)
        }
    }


    TransplantListItem(
        headlineContent = { Text(text = "洗浴" + if(getCampus() != Campus.XUANCHENG) "(宣)" else "") },
        leadingContent = {
            Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    getInGuaGua(vm)
                },
            ) { Icon( painterResource(R.drawable.shower), contentDescription = "Localized description",) }
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}



