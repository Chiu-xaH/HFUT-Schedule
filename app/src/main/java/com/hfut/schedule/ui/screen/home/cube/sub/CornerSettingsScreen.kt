package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.destination.CornerSettingsDestination
import com.xah.common.ScreenCornerHelper
import com.xah.uicommon.component.slider.CustomSlider
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CornerSettingsScreen() {
    val corner by DataStoreManager.screenCorner.collectAsState(ScreenCornerHelper.corner.value)

    if(corner < 0f) {
        LaunchedEffect(Unit) {
            DataStoreManager.saveScreenCorner(ScreenCornerHelper.corner.value)
        }
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface))
    } else {
        val view = LocalView.current
        val scope = rememberCoroutineScope()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.inversePrimary,
            topBar = {
                MediumTopAppBar(
                    colors = topBarTransplantColor(),
                    title = { Text(CornerSettingsDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        scope.launch {
                            DataStoreManager.saveScreenCorner(ScreenCornerHelper(view).getCornerDp().value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(APP_HORIZONTAL_DP)
                        .navigationBarsPadding()
                ) {
                    Text("恢复默认")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface,RoundedCornerShape(corner.dp))
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column (modifier = Modifier.align(Alignment.Center),horizontalAlignment = Alignment.CenterHorizontally) {
                    CustomSlider(
                        value = corner,
                        onValueChange = {
                            scope.launch {
                                DataStoreManager.saveScreenCorner(it)
                            }
                        },
                        valueRange = 0f..100f
                    )
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)) {
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveScreenCorner(corner-0.5f)
                                }
                            },
                            enabled = corner > 0f,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Text("-0.5")
                        }
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveScreenCorner(ScreenCornerHelper(view).getCornerDp().value)
                                }
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text("${formatDecimal(corner.toDouble(),2)}")
                        }
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveScreenCorner(corner+0.5f)
                                }
                            },
                            enabled = corner < 100f,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Text("+0.5")
                        }
                    }
                }
            }
        }
    }
}
