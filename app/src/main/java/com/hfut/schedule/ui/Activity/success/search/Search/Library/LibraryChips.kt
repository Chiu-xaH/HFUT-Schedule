package com.hfut.schedule.ui.Activity.success.search.Search.Library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
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
import com.hfut.schedule.R
import com.hfut.schedule.logic.Enums.LibraryItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryChips() {
    val sheetState_History = rememberModalBottomSheetState()
    var showBottomSheet_History by remember { mutableStateOf(false) }
    val sheetState_Borrow = rememberModalBottomSheetState()
    var showBottomSheet_Borrow by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 0.dp), horizontalArrangement = Arrangement.Start){

        AssistChip(
            onClick = { showBottomSheet_Borrow = true },
            label = { Text(text = "当前借阅  ${getBorrow(LibraryItems.BORROWED.name).size} 本") },
            // leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
        )

        Spacer(modifier = Modifier.width(10.dp))

        AssistChip(
            onClick = { showBottomSheet_History = true },
            label = { Text(text = "借阅历史  ${getBorrow(LibraryItems.HISTORY.name).size} 本") },
            // leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
        )
    }
    if(showBottomSheet_History) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet_History = false }, sheetState = sheetState_History) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("借阅历史") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) { BorrowItems(LibraryItems.HISTORY.name) }
            }
        }
    }

    if(showBottomSheet_Borrow) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet_Borrow = false }, sheetState = sheetState_Borrow) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("当前借阅") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        // .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) { BorrowItems(LibraryItems.BORROWED.name) }
            }
        }
    }
}

@Composable
fun BorrowItems(PerfsJson : String) {
    LazyColumn {
        items(getBorrow(PerfsJson).size){ item ->
            val Outtime = getBorrow(PerfsJson)[item].outTime
            val Returntime = getBorrow(PerfsJson)[item].returnTime
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center)
            {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    ListItem(
                        headlineContent = { Text(text = getBorrow(PerfsJson)[item].bookName,fontWeight = FontWeight.Bold) },
                        supportingContent = {  Text(text = getBorrow(PerfsJson)[item].author) },
                        overlineContent = {
                            if(Returntime == null)
                                Text(text = "借于 $Outtime")
                            else
                                Text(text = "借于 $Outtime\n还于 $Returntime")
                        },
                        leadingContent = {
                            Icon(painterResource(R.drawable.book), contentDescription = "Localized description",)
                        }
                    )
                }
            }
        }
    }
}
