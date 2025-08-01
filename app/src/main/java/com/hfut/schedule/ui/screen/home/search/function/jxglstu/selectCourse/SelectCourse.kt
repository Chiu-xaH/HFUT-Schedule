package com.hfut.schedule.ui.screen.home.search.function.jxglstu.selectCourse

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SelectCourse(
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.SelectCourse.route }

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.SelectCourse.title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(AppNavRoute.SelectCourse.icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            if(!ifSaved) navController.navigateAndSaveForTransition(route)
            else refreshLogin()
        }
    )
}

