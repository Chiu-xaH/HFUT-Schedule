package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.button.LiquidButton
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
import com.xah.container.util.NoneRoundShape
import com.xah.floating.util.LocalFloatingController
import kotlinx.coroutines.launch

object FeedbackWindow: FloatingWindow() {

    override val key = "feedback"

    override val title = res(R.string.navigation_label_feedback)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val controller = LocalFloatingController.current
        val context = LocalContext.current

        val displayList = remember {
            if(getCampusRegion() == CampusRegion.XUANCHENG) {
                listOf(CampusRegion.XUANCHENG, CampusRegion.HEFEI)
            } else {
                listOf(CampusRegion.HEFEI,CampusRegion.XUANCHENG)
            }
        }

        val scope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.largeIncreased,
                key = key,
                contentStrategy = ContentStrategy.Layer(isFloating = true),
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

                            displayList.forEach { campus ->
                                when(campus) {
                                    CampusRegion.HEFEI -> {
                                        CardListItem(
                                            headlineContent = { Text("合肥校区") },
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    Starter.startWebUrlInner(context,Constant.FEEDBACK_URL)
                                                }
                                            },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.mail),null)
                                            }
                                        )
                                    }
                                    CampusRegion.XUANCHENG -> {
                                        CardListItem(
                                            headlineContent = { Text("宣城校区") },
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    Starter.startWebUrlInner(context,Constant.FEEDBACK_XC_URL)
                                                }
                                            },
                                            leadingContent = {
                                                Icon(painterResource(R.drawable.mail),null)
                                            }
                                        )
                                    }
                                }
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