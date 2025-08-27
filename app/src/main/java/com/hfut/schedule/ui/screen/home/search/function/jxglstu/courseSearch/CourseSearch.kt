package com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch

import android.annotation.SuppressLint
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
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.iconElementShare


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CoursesSearch(
    ifSaved :  Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.CourseSearch.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = AppNavRoute.CourseSearch.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.CourseSearch.icon), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = route))
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin()
            else navController.navigateForTransition(AppNavRoute.CourseSearch,route)
        }
    )
}


