package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.StartAppIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.res
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController

object ExpressWindow: FloatingWindow() {

    override val key = "express"

    override val title = res(R.string.navigation_label_express)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val controller = LocalFloatingController.current
        val context = LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.largeIncreased,
                key = key,
                contentStrategy = ContentStrategy.FloatingWindow,
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(vertical = APP_HORIZONTAL_DP, horizontal = APP_HORIZONTAL_DP)
                    .align(Alignment.Center)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    var innerPadding by remember { mutableStateOf(0.dp) }
                    Box {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-CARD_NORMAL_DP))

                            CardListItem(
                                headlineContent = { Text(Starter.AppPackages.PDD.appName) },
                                supportingContent = {
                                    Text("拼多多身份码，校区快递站用")
                                },
                                modifier = Modifier.clickable {
                                    Starter.startPddExpress(context)
                                },
                                leadingContent = {
                                    StartAppIcon(Starter.AppPackages.PDD)
                                }
                            )

                            CardListItem(
                                headlineContent = { Text(Starter.AppPackages.TAO_BAO.appName) },
                                supportingContent = {
                                    Text("淘宝身份码，合肥校区快递站用")
                                },
                                modifier = Modifier.clickable {
                                    Starter.startTaoBaoExpress(context)
                                },
                                leadingContent = {
                                    StartAppIcon(Starter.AppPackages.TAO_BAO)
                                }
                            )

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