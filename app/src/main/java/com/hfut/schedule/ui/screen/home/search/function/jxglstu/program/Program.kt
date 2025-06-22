package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.custom.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var showBottomSheet_search by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            FilledTonalIconButton(
                onClick = {
                    showBottomSheet_search = true
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                showBottomSheet_Program = true
            }
            else refreshLogin()
        }
    )



    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            showBottomSheet = showBottomSheet_search,
            hazeState = hazeState,
        ) {
            ProgramSearch(vm,ifSaved,hazeState)
        }
    }


    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Program
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("培养方案") {
                        FilledTonalButton(
                            onClick = {
                                showBottomSheet_search = true
                            }
                        ) {
                            Text("全校培养方案")
                        }
                    }
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramScreen(vm,ifSaved,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestProgram(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_search by remember { mutableStateOf(false) }


    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_search
        ) {
            ProgramSearch(vm, true, hazeState )
        }
    }
    TransplantListItem(
        headlineContent = { Text(text = "全校培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet_search = true
        }
    )
}