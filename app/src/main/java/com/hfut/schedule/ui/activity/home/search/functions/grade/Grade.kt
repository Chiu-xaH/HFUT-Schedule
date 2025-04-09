package com.hfut.schedule.ui.activity.home.search.functions.grade

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.activity.screen.GradeActivity
import com.hfut.schedule.logic.utils.DateTimeUtils
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.data.SharePrefs.saveString
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.logic.utils.data.WebVpn
import com.hfut.schedule.ui.activity.grade.getGrade
import com.hfut.schedule.ui.utils.components.TransplantListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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


