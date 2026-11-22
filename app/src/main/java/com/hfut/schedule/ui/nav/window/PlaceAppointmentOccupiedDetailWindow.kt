package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.status.DevelopingIcon
import com.hfut.schedule.ui.component.text.AutoSizeText
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.hfut.schedule.ui.util.layout.measureDpSize
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController


data class PlaceAppointmentOccupiedDetailWindow(
    val id : String,
    val origin : Any? = null
) : FloatingWindow() {

    override val key = "place_appointment_occupied_${id}_$origin"
    override val title = text("占用详情")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val floatingController = LocalFloatingController.current

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
                        LazyColumn {
                            item { Spacer(Modifier.height(innerPadding+APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                            // TODO
                            item {
                                CustomCard(
                                    color = Color.Transparent
                                ) {
                                    RowHorizontal {
                                        DevelopingIcon()
                                    }
                                }
                            }
                            item { Spacer(Modifier.height(APP_HORIZONTAL_DP-CARD_NORMAL_DP)) }
                        }

                        AutoSizeText(
                            title.asString(),
                            innerPadding,
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    vertical = APP_HORIZONTAL_DP / 2,
                                    horizontal = APP_HORIZONTAL_DP
                                )
                        )
                        LiquidButton(
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(APP_HORIZONTAL_DP / 2)
                                    .measureDpSize { _, h -> innerPadding = h }
                            ,
                            onClick = {
                                floatingController.pop()
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
