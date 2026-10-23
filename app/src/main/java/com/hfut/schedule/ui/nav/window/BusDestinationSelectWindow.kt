package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.logic.model.Campus
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController

private val campus = listOf(Campus.FCH.description, Campus.TXL.description,"六安路", Campus.XC.description,NULL)

private const val NULL = "全部"

data class BusDestinationSelectWindow(
    val isStart : Boolean,
    val destination : String?,
    val otherDestination : String?,
    val onSelected : (String?) -> Unit
) : FloatingWindow() {
    override val key = "bus_destination_select_$isStart"
    override val title = text("选择${if(isStart) "起点" else "终点"}")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val floatingController = LocalFloatingController.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.large,
                key = key,
                contentStrategy = ContentStrategy.Shared(keepShowContainer = false),
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(APP_HORIZONTAL_DP)
                    .align(
                        if(isStart) {
                            Alignment.TopStart
                        } else {
                            Alignment.TopEnd
                        }
                    )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = NoneRoundShape
                ) {
                    Column(modifier = Modifier.fillMaxWidth(2/3f)) {
                        for(index in campus.indices) {
                            val item = campus[index]
                            if(item == otherDestination) {
                                continue
                            }
                            if(destination == null && item == NULL) {
                                continue
                            }
                            TransplantListItem(
                                headlineContent = {
                                    Text(item)
                                },
                                trailingContent = if(item == destination) {
                                    {
                                        Icon(
                                            painterResource(R.drawable.check),
                                            null
                                        )
                                    }
                                } else {
                                    null
                                },
                                modifier = Modifier.clickable {
                                    onSelected(
                                        if(item == NULL) {
                                            null
                                        } else {
                                            item
                                        }
                                    )
                                    floatingController.pop()
                                }
                            )
                            if(index != campus.size-1) {
                                PaddingHorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}