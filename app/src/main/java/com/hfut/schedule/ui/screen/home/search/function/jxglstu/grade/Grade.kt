package com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.AppNavRoute
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.screen.MainHost
import com.xah.transition.component.containerShare
import com.xah.transition.util.navigateAndSaveForTransition


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun Grade(
    ifSaved : Boolean,
    navController : NavHostController,
)  {
    TransplantListItem(
        headlineContent = { Text(text = "成绩") },
        leadingContent = { Icon(painterResource(R.drawable.article), contentDescription = "Localized description",) },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(AppNavRoute.Grade.withArgs(ifSaved))
        }
    )
}


