package com.hfut.schedule.ui.ComposeUI.Settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hfut.schedule.R
import com.hfut.schedule.ui.ComposeUI.TestUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun debug() {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) { TestUI() }
    }

    ListItem(
        headlineContent = { Text(text = "调试模式") },
        modifier = Modifier.clickable { showBottomSheet },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.api), contentDescription ="" ) }
    )
}