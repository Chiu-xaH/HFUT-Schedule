package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.MyApiParse.getSettingInfo
import com.hfut.schedule.logic.util.sys.datetime.getUserAge
import com.hfut.schedule.logic.util.sys.datetime.isUserBirthday
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.theme.warnColor
import com.xah.common.logic.util.isNotEmptyAndBlank

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
    val data = remember { getSettingInfo() }
    val show = remember(data) { data.show }
    val isBirthday = remember { isUserBirthday() }

    if(!isBirthday && !show) {
        return
    }

    DividerTextExpandedWith(text = stringResource(R.string.settings_important_notice_half_title)) {
        CustomCard(color = color) {
            if(isBirthday)  {
                val age = remember { getUserAge() }
                TransplantListItem(
                    headlineContent = {
                        Text(text = "${age}周岁生日快乐")
                    },
                    supportingContent = { Text("Happy Birthday")},
                    leadingContent = {
                        Icon(painterResource(R.drawable.cake), contentDescription = "Localized description", tint = warnColor())
                    },
                )
            }
            if(show) {
                if(isBirthday) {
                    PaddingHorizontalDivider()
                }
                TransplantListItem(
                    headlineContent = {
                        Text(text = data.title, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = if(data.info.isNotEmptyAndBlank()) {
                        { Text(text = data.info) }
                    } else null,
                    leadingContent = {
                        APIIcons(data.celebration)
                    },
                )
            }
        }
    }
}
