package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.xah.transition.util.TransitionBackHandler
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.padding.InnerPaddingHeight
import kotlinx.coroutines.launch

@Composable
fun ApiKeyScreen(
    innerPadding : PaddingValues,
    navController: NavHostController
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    var scale by remember { mutableFloatStateOf(1f) }

    TransitionBackHandler(navController,enablePredictive) {
        scale = it
    }

    val pwd by DataStoreManager.apiKey.collectAsState(initial = "")
    var inputApiKey by remember { mutableStateOf("") }
    LaunchedEffect(pwd) {
        inputApiKey = pwd
    }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.verticalScroll(rememberScrollState()).scale(scale)) {
        InnerPaddingHeight(innerPadding,true)
        DividerTextExpandedWith("配置ApiKey") {
            CustomCard(
                color = MaterialTheme.colorScheme.surface
            ) {
                TransplantListItem(
                    headlineContent = { Text("ApiKey") },
                    leadingContent = { Icon(painterResource(R.drawable.edit),null) },
                )

                CustomTextField(
                    input = inputApiKey,
                    singleLine = false,
                    label = { Text("输入APiKey") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    DataStoreManager.saveApiKey(inputApiKey)
                                    showToast("保存完成")
                                }
                            }
                        ) {
                            Icon(painterResource(R.drawable.save),null)
                        }
                    }
                ) {
                    inputApiKey = it
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP))
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}