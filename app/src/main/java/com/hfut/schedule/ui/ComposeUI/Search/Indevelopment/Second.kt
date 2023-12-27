package com.hfut.schedule.ui.ComposeUI.Search.Indevelopment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Second() {

    val sheetState_Second = rememberModalBottomSheetState()
    var showBottomSheet_Second by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "第二课堂") },
        supportingContent = { Text(text = "暂未开发") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.school),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            // showBottomSheet_Second = true
        }
    )

    if (showBottomSheet_Second) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Second = false
            },
            sheetState = sheetState_Second
        ) {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "暂未开发")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }


        }
    }

}