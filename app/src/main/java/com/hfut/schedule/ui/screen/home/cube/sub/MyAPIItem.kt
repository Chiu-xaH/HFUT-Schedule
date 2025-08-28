package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.getSettingInfo
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
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
            CustomCard(color = color) {
                TransplantListItem(
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
                if(isUserBirthday()) {
                    val color = Color(0xffba7f25)
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text(text = "${getUserAge()}周岁生日快乐")
                        },
                        supportingContent = { Text("Happy Birthday")},
                        leadingContent = {
                            Icon(painterResource(R.drawable.cake), contentDescription = "Localized description", tint = color)
                        },
                    )
                }
            }
        }
    }
}
