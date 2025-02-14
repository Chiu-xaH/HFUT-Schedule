package com.hfut.schedule.ui.activity.home.search.functions.shower

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
import com.hfut.schedule.viewmodel.NetWorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Shower(vm: NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState(true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            ShowerUI(vm)
        }
    }


    ListItem(
        headlineContent = { Text(text = "洗浴") },
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



