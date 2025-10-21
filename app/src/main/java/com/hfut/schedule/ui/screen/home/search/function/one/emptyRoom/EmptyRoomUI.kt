package com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Classroom(
    navController : NavHostController,
){
    val route = remember { AppNavRoute.Classroom.route }
    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.Classroom.label) },
        leadingContent = {
            Icon(
                painterResource(AppNavRoute.Classroom.icon),
                contentDescription = "Localized description",
                modifier = Modifier.iconElementShare(route)
            )
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Classroom,route)
        }
    )
}
