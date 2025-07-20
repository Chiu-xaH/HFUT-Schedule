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
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.MainHost
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.component.titleElementShare
import com.xah.transition.util.navigateAndSaveForTransition


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun Grade(
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
)  {
    val route = remember { AppNavRoute.Grade.receiveRoute() }
    TransplantListItem(
        headlineContent = {
            Text(text = "成绩")
        },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(R.drawable.article), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(AppNavRoute.Grade.withArgs(ifSaved))
        }
    )
}


