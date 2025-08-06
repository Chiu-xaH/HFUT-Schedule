package com.hfut.schedule.ui.screen.home.search.function.school.news

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
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun News(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.News.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.News.label) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.News.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.News,route)
        }
    )
}




