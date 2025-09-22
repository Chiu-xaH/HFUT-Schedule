package com.hfut.schedule.ui.component.button

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.xah.transition.component.TopBarNavigateIcon

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TopBarNavigationIcon(
    navController : NavHostController,
    route : String,
    icon : Int,
) {
    val enablePredictive by DataStoreManager.enablePredictive.collectAsState(initial = AppVersion.CAN_PREDICTIVE)
    TopBarNavigateIcon(navController,route, painterResource(icon),!enablePredictive)
}