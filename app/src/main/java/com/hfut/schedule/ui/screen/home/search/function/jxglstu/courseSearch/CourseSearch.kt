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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CoursesSearch(
    ifSaved :  Boolean,
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.CourseSearch.route }
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.CourseSearch.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.CourseSearch.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin(context)
            else navController.navigateForTransition(AppNavRoute.CourseSearch,route)
        }
    )
}


