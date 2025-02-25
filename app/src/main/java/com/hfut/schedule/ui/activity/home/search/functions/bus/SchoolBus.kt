package com.hfut.schedule.ui.activity.home.search.functions.bus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.R
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebViewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolBus() {
    val sheetState_Bus = rememberModalBottomSheetState()
    var showBottomSheet_Bus by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    TransplantListItem(
        headlineContent = { Text(text = "校车") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.directions_bus),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showDialog = true
        }
    )

    if (showBottomSheet_Bus) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet_Bus = false
            },
            sheetState = sheetState_Bus
        ) {
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "不想开发")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }



    if (showDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        actions = { IconButton(onClick = { showDialog = false }) {
                            Icon(painterResource(id = R.drawable.close), contentDescription = "")
                        }
                        },
                        title = { Text("校车") }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    WebViewScreen("file:///android_asset/BusInfos.html")
                }
            }
        }
    }
}