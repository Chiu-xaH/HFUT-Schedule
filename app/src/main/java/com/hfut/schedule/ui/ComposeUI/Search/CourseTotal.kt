package com.hfut.schedule.ui.ComposeUI.Search

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
fun CourseTotal() {
    val sheetState_Total = rememberModalBottomSheetState()
    var showBottomSheet_Total by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(text = "课程汇总") },
        supportingContent = { Text(text = "暂未开发") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.category),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            // showBottomSheet_Total = true
        }
    )

    if (showBottomSheet_Total) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Total = false
            },
            sheetState = sheetState_Total
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