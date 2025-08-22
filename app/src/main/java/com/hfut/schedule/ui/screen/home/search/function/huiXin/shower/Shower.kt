package com.hfut.schedule.ui.screen.home.search.function.huiXin.shower

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.icon.RotatingIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Shower(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            autoShape = false,
            showBottomSheet = showBottomSheet,
            hazeState = hazeState
        ) {
            ShowerUI(vm,hazeState = hazeState)
        }
    }


    TransplantListItem(
        headlineContent = { Text(text = "洗浴") },
        leadingContent = {
            if(loading) {
                LoadingIcon()
            } else {
                Icon(painterResource(id = R.drawable.bathtub), contentDescription = "")
            }
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    getInGuaGua(vm) { loading = it }
                },
            ) { Icon( painterResource(R.drawable.shower), contentDescription = "Localized description",) }
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}



