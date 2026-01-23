package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.lessons
import com.hfut.schedule.ui.component.container.AnimationCardListItem
import com.xah.uicommon.component.text.ScrollText

@Composable
fun TermFirstlyInfo(list: List<lessons>) {
    if(list.isEmpty()) return

    val info = list[0].semester
    AnimationCardListItem(
        overlineContent = { Text(text = info.startDate + " ~ " + info.endDate)},
        headlineContent = {  ScrollText(info.nameZh) },
        leadingContent = { Icon(
            painterResource(R.drawable.category),
            contentDescription = "Localized description",
        ) },
        color = MaterialTheme.colorScheme.secondaryContainer,
        trailingContent = {
            Text(text = "学分 ${periodsSum(list)}")
        },
        index = 0
    )
}



private fun periodsSum(list: List<lessons>) : Double {
    var num = 0.0
    for(i in list) {
        val credit = i.course.credits
        if (credit != null) {
            num += credit
        }
    }
    return num
}

