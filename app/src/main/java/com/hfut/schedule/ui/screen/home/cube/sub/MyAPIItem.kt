package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.R
import com.hfut.schedule.logic.network.util.MyApiParse.getSettingInfo
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith

val birthdayColor = Color(0xffba7f25)

@Composable
fun APIIcons(celebration: Boolean) {
    when {
        celebration -> Icon(painterResource(R.drawable.celebration), contentDescription = "Localized description",)
        else -> Icon(painterResource(R.drawable.info), contentDescription = "Localized description",)
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
        DividerTextExpandedWith(text = stringResource(R.string.settings_important_notice_half_title)) {
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
                    PaddingHorizontalDivider()
                    TransplantListItem(
                        headlineContent = {
                            Text(text = "${getUserAge()}周岁生日快乐")
                        },
                        supportingContent = { Text("Happy Birthday")},
                        leadingContent = {
                            Icon(painterResource(R.drawable.cake), contentDescription = "Localized description", tint = birthdayColor)
                        },
                    )
                }
            }
        }
    }
}
