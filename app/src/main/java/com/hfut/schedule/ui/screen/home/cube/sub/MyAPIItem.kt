package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.logic.util.storage.SharePrefs.prefs
import com.hfut.schedule.logic.util.sys.DateTimeUtils
import com.hfut.schedule.logic.util.network.parse.ParseJsons.getSettingInfo
//import com.hfut.schedule.logic.utils.parse.getSettingInfo
import com.hfut.schedule.ui.screen.home.search.function.person.getPersonInfo
import com.hfut.schedule.ui.component.APIIcons
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.StyleCardListItem

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
                    supportingContent = if(content.isNotBlank()) {
                        { Text(text = content) }
                    } else null,
                    leadingContent = { APIIcons(celebration = celebration) },
                )
            }
    }
}
