package com.hfut.schedule.ui.screen.home.search.function.school.admission

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
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.uicommon.component.text.ScrollText

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Admission(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Admission.route }
    TransplantListItem(
        headlineContent = { ScrollText(AppNavRoute.Admission.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Admission.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Admission,route)
        }
    )
}

