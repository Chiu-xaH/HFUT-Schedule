package com.hfut.schedule.ui.screen.home.search.function.community.library

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
import com.hfut.schedule.ui.component.container.TransplantListItem


import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun LibraryItem(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Library.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Library.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Library.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Library,route)
        }
    )
}