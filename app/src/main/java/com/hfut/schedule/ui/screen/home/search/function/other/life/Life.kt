package com.hfut.schedule.ui.screen.home.search.function.other.life

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.text.ScrollText
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Life(vm : NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet by remember { mutableStateOf(false) }


    TransplantListItem(
        headlineContent = { ScrollText(text = "生活服务") },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.near_me), contentDescription = null) },
        modifier = Modifier.clickable {
                showBottomSheet = true
        }
    )


    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
            hazeState = hazeState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("生活服务")
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding).verticalScroll(rememberScrollState())
                        .fillMaxSize()
                ) {
                    LifeScreen(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}