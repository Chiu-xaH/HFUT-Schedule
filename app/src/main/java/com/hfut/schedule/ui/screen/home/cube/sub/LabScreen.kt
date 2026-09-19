package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.SettingsAppearanceDestination
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun LabScreen(innerPadding : PaddingValues) {
    val navController = LocalNavController.current

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        InnerPaddingHeight(innerPadding,true)
        CardListItem(
            color = MaterialTheme.colorScheme.surface,
            headlineContent = {
                Text("本页面功能不代表最终是否正式使用，可能会随后续版本重新调整")
            },
            leadingContent = {
                Icon(painterResource(R.drawable.info),null)
            },
        )
        DividerTextExpandedWith("开关") {
            CustomCard(color = MaterialTheme.colorScheme.surface) {
                TransplantListItem(
                    headlineContent = {
                        Text("底栏新样式")
                    },
                    supportingContent = {
                        Text("已转正至外观设置，本入口将被移除")
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.label),null)
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = {
                                navController.push(SettingsAppearanceDestination, effect = JumpTransitionEffectWallpaper())
                            }
                        ) {
                            Icon(painterResource(R.drawable.forward),null)
                        }
                    },
                    modifier = Modifier.clickable {
                        navController.push(SettingsAppearanceDestination, effect = JumpTransitionEffectWallpaper())
                    }
                )
            }
        }
        InnerPaddingHeight(innerPadding,false)
    }
}