package com.hfut.schedule.ui.screen.home.search.function.grade

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.TransplantListItem


@SuppressLint("SuspiciousIndentation")
@Composable
fun Grade(vm : NetWorkViewModel, ifSaved : Boolean)  {
    TransplantListItem(
        headlineContent = { Text(text = "成绩") },
        leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            Starter.startGrade(vm,ifSaved)
        }
    )
}


