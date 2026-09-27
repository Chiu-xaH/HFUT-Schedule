package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.theme.greenColor
import com.hfut.schedule.ui.theme.warnColor
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.hjq.device.compat.DeviceOs
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController


object AppEnvWarningWindow: FloatingWindow() {

    override val key = "app_env_warning"

    override val title = text("运行环境告警")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val controller = LocalFloatingController.current
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.largeIncreased,
                key = key,
                contentStrategy = ContentStrategy.Shared(keepShowContainer = false),
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = APP_HORIZONTAL_DP, horizontal = APP_HORIZONTAL_DP)
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = NoneRoundShape
                ) {
                    var innerPadding by remember { mutableStateOf(0.dp) }
                    Box {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-CARD_NORMAL_DP))
                            if(AppVersion.isNormalEnv) {
                                CardListItem(
                                    headlineContent = {
                                        Text("运行环境正常")
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.check_circle),
                                            null,
                                            tint = greenColor()
                                        )
                                    }
                                )
                            }
                            if(AppVersion.isDebug) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_debug))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.construction),
                                            null,
                                            tint = warnColor()
                                        )
                                    }
                                )
                            } else if(!AppVersion.isSignatureValid) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_sign))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.signature),
                                            null,
                                            tint = warnColor()
                                        )
                                    }
                                )
                            }
                            if(AppVersion.isDev) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_preview))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.logo_dev),
                                            null,
                                            tint = warnColor()
                                        )
                                    }
                                )
                            }
                            if(AppVersion.isRunningOnAvd) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_avd))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.adb),
                                            null,
                                            tint = warnColor()
                                        )
                                    }
                                )
                            } else if(AppVersion.isRunningOnWsa) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_wsa))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.desktop_windows),
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                            if(DeviceOs.isHarmonyOsNextAndroidCompatible()) {
                                CardListItem(
                                    headlineContent = {
                                        Text(stringResource(R.string.settings_person_info_tag_harmony_next))
                                    },
                                    leadingContent = {
                                        Icon(
                                            painterResource(R.drawable.circle),
                                            null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }
                            Spacer(Modifier.height(APP_HORIZONTAL_DP-CARD_NORMAL_DP))
                        }

                        AutoSizeText(
                            title.asString(),
                            innerPadding,
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(vertical = APP_HORIZONTAL_DP/2, horizontal = APP_HORIZONTAL_DP)
                        )
                        LiquidButton(
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(APP_HORIZONTAL_DP/2)
                                    .measureDpSize { _,h -> innerPadding = h }
                            ,
                            onClick = {
                                controller.pop()
                            },
                            backdrop = rememberLayerBackdrop(),
                            isCircle = true
                        ) {
                            Icon(painterResource(R.drawable.close),null)
                        }
                    }
                }
            }
        }
    }
}