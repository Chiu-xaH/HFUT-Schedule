package com.hfut.schedule.ui.Activity.success.search.Search.Estimate

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel

@Composable
fun Estimate(vm : LoginSuccessViewModel){

    ListItem(
        headlineContent = { Text(text = "教师评教")},
        supportingContent = { Text(text = "暂未开发")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.verified), contentDescription = "")},
        modifier = Modifier.clickable {  }
    )
}