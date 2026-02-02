package com.hfut.schedule.ui.screen.home.search.function.school.dormitoryScore

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DormitoryScoreXuanCheng(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.Dormitory.route }

    TransplantListItem(
        headlineContent = { ScrollText(stringResource(AppNavRoute.Dormitory.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Dormitory.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Dormitory,route)
        }
    )
}