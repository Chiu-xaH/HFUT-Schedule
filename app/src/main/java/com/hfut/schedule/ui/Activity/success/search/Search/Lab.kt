package com.hfut.schedule.ui.Activity.success.search.Search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.datamodel.Community.BorrowRecords
import com.hfut.schedule.logic.datamodel.Community.BorrowResponse
import com.hfut.schedule.logic.datamodel.Lab
import com.hfut.schedule.logic.datamodel.MyAPIResponse
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.StartUri
import com.hfut.schedule.logic.utils.StartUri.StartUri
import com.hfut.schedule.ui.Activity.success.search.Search.Library.getBorrow
import com.hfut.schedule.ui.Activity.success.search.Search.Program.ProgramUI
import com.hfut.schedule.ui.UIUtils.MyToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Lab() {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { Text(text = "实验室") },
        overlineContent = { Text(text = "云控")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.science),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )

    if (showBottomSheet ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("实验室") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    LabUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun LabUI() {
    LazyColumn {
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                ListItem(
                    headlineContent = { Text(text = "这里是实验室,里面的选项会随云端发生变动,即使您不更新软件") },
                    leadingContent = {
                        Icon(painterResource(R.drawable.cloud_download), contentDescription = "Localized description",)
                    },
                    modifier = Modifier.clickable {
                    }
                )
            }
        }
        items(getLab().size) { item ->
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                ListItem(
                    headlineContent = { Text(text = getLab()[item].title) },
                    supportingContent = {  Text(text = getLab()[item].info) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.net), contentDescription = "Localized description",)
                    },
                    trailingContent = { Icon(Icons.Filled.ArrowForward, contentDescription = "" )},
                    modifier = Modifier.clickable {
                        StartUri(getLab()[item].info)
                    }
                )
            }
        }
    }
}


fun getLab() : MutableList<Lab>{
    var Items = mutableListOf<Lab>()
    val json = SharePrefs.prefs.getString("my","")
    return try {
        val item = Gson().fromJson(json, MyAPIResponse::class.java).Labs
        for (i in 0 until item.size) {
            val title = item[i].title
            val info = item[i].info
            val type = item[i].type
            Items.add(Lab(title, info, type))
        }
        Items
    } catch (e : Exception) {
        Items
    }
}