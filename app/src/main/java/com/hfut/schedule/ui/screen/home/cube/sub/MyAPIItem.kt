package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.logic.util.network.ParseJsons.getSettingInfo
import com.hfut.schedule.ui.component.container.StyleCardListItem
import com.hfut.schedule.ui.component.icon.APIIcons
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith

@Composable
fun MyAPIItem() {
    val data = getSettingInfo()
    val title = data.title
    val content = data.info

    val celebration = data.celebration

    val show by remember { mutableStateOf(data.show) }

    if(show) {
        DividerTextExpandedWith(text = "重要通知") {
            StyleCardListItem(
                headlineContent = {
                    Text(text = title, fontWeight = FontWeight.Bold)
                },
                supportingContent = if(content.isNotBlank() && content.isNotEmpty()) {
                    { Text(text = content) }
                } else null,
                leadingContent = { APIIcons(celebration = celebration) },
            )
        }
    }
}
