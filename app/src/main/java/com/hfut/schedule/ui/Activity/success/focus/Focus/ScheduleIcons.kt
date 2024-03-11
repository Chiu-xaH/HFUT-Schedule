package com.hfut.schedule.ui.Activity.success.focus.Focus

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

@Composable
fun ScheduleIcons(title : String) {
    if (title.contains("实验"))
        Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if (title.contains("上机"))
        Icon(painter = painterResource(id = R.drawable.devices), contentDescription = "")
    else if (title.contains("实习"))
        Icon(painter = painterResource(id = R.drawable.nature_people), contentDescription = "")
    else
        Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
}
