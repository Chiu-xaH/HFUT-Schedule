package com.hfut.schedule.ui.ComposeUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDemo() {

  //  ModalBottomSheet(onDismissRequest = {  }) {

  //  }
    // [END android_compose_layout_material_bottom_sheet]

    // [START android_compose_layout_material_bottom_sheet2]
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Show bottom sheet") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { contentPadding ->
        // Screen content
        // [START_EXCLUDE silent]
        Box(modifier = Modifier.padding(contentPadding)) {
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // Sheet content
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false

                            }
                        }
                    }) {
                        Text("Hide bottom sheet")
                    }
                }
            }
        }
        // [END_EXCLUDE]


    }
}


//    val sheetState_1_1 = rememberModalBottomSheetState()
//                val sheetState_1_2 = rememberModalBottomSheetState()
//                val sheetState_1_3 = rememberModalBottomSheetState()
//                val sheetState_1_4 = rememberModalBottomSheetState()
//                val sheetState_1_5 = rememberModalBottomSheetState()
//                val sheetState_2_1 = rememberModalBottomSheetState()
//                val sheetState_2_2 = rememberModalBottomSheetState()
//                val sheetState_2_3 = rememberModalBottomSheetState()
//                val sheetState_2_4 = rememberModalBottomSheetState()
//                val sheetState_2_5 = rememberModalBottomSheetState()
//                val sheetState_3_1 = rememberModalBottomSheetState()
//                val sheetState_3_2 = rememberModalBottomSheetState()
//                val sheetState_3_3 = rememberModalBottomSheetState()
//                val sheetState_3_4 = rememberModalBottomSheetState()
//                val sheetState_3_5 = rememberModalBottomSheetState()
//                val sheetState_4_1 = rememberModalBottomSheetState()
//                val sheetState_4_2 = rememberModalBottomSheetState()
//                val sheetState_4_3 = rememberModalBottomSheetState()
//                val sheetState_4_4 = rememberModalBottomSheetState()
//                val sheetState_4_5 = rememberModalBottomSheetState()
//
//                val scope = rememberCoroutineScope()
//                var showBottomSheet_1_1 by remember { mutableStateOf(false) }
//                var showBottomSheet_1_2 by remember { mutableStateOf(false) }
//                var showBottomSheet_1_3 by remember { mutableStateOf(false) }
//                var showBottomSheet_1_4 by remember { mutableStateOf(false) }
//                var showBottomSheet_1_5 by remember { mutableStateOf(false) }
//                var showBottomSheet_2_1 by remember { mutableStateOf(false) }
//                var showBottomSheet_2_2 by remember { mutableStateOf(false) }
//                var showBottomSheet_2_3 by remember { mutableStateOf(false) }
//                var showBottomSheet_2_4 by remember { mutableStateOf(false) }
//                var showBottomSheet_2_5 by remember { mutableStateOf(false) }
//                var showBottomSheet_3_1 by remember { mutableStateOf(false) }
//                var showBottomSheet_3_2 by remember { mutableStateOf(false) }
//                var showBottomSheet_3_3 by remember { mutableStateOf(false) }
//                var showBottomSheet_3_4 by remember { mutableStateOf(false) }
//                var showBottomSheet_3_5 by remember { mutableStateOf(false) }
//                var showBottomSheet_4_1 by remember { mutableStateOf(false) }
//                var showBottomSheet_4_2 by remember { mutableStateOf(false) }
//                var showBottomSheet_4_3 by remember { mutableStateOf(false) }
//                var showBottomSheet_4_4 by remember { mutableStateOf(false) }
//                var showBottomSheet_4_5 by remember { mutableStateOf(false) }


//  val sheetState = arrayOf(
//                    arrayOf(sheetState_1_1, sheetState_1_2, sheetState_1_3, sheetState_1_4, sheetState_1_5),
//                    arrayOf(sheetState_2_1, sheetState_2_2, sheetState_2_3, sheetState_2_4, sheetState_2_5),
//                    arrayOf(sheetState_3_1, sheetState_3_2, sheetState_3_3, sheetState_3_4, sheetState_3_5),
//                    arrayOf(sheetState_4_1, sheetState_4_2, sheetState_4_3, sheetState_4_4, sheetState_4_5)
//                )
//
//                val showBottomSheet = arrayOf(
//                    arrayOf(showBottomSheet_1_1, showBottomSheet_1_2, showBottomSheet_1_3, showBottomSheet_1_4, showBottomSheet_1_5),
//                    arrayOf(showBottomSheet_2_1, showBottomSheet_2_2, showBottomSheet_2_3, showBottomSheet_2_4, showBottomSheet_2_5),
//                    arrayOf(showBottomSheet_3_1, showBottomSheet_3_2, showBottomSheet_3_3, showBottomSheet_3_4, showBottomSheet_3_5),
//                    arrayOf(showBottomSheet_4_1, showBottomSheet_4_2, showBottomSheet_4_3, showBottomSheet_4_4, showBottomSheet_4_5)
//                )
//   if (showBottomSheet[1][1]) {
//                    ModalBottomSheet(
//                        onDismissRequest = {showBottomSheet[1][1] = false },
//                        sheetState = sheetState[1][1],
//
//                        ) {
//                        Text(text = sheet[1][1])
//                        Log.d("测试",sheet[1][1])
//                    }
//                }
