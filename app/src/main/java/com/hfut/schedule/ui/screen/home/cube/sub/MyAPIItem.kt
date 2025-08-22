package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.getSettingInfo
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith


@Composable
fun APIIcons(celebration: Boolean) {
    when {
        celebration -> Icon(painterResource(R.drawable.celebration), contentDescription = "Localized description",)
        else -> Icon(painterResource(R.drawable.notifications), contentDescription = "Localized description",)
    }
}

@Composable
fun MyAPIItem(
    color : Color = cardNormalColor()
) {
    val data = getSettingInfo()
    val title = data.title
    val content = data.info

    val show by remember { mutableStateOf(data.show) }

    if(show) {
        DividerTextExpandedWith(text = "重要通知") {
            CardListItem(
                color = color,
                headlineContent = {
                    Text(text = title, fontWeight = FontWeight.Bold)
                },
                supportingContent = if(content.isNotBlank() && content.isNotEmpty()) {
                    { Text(text = content) }
                } else null,
                leadingContent = {
                    APIIcons(data.celebration)
                },
            )
        }
    }
}
