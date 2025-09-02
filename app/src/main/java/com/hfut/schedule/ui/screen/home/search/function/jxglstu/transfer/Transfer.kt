package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.iconElementShare


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Transfer(
    ifSaved : Boolean,
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
){
    val route = remember { AppNavRoute.Transfer.route }
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { Text(text = AppNavRoute.Transfer.label) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Transfer.icon), contentDescription = null,modifier = Modifier.iconElementShare(sharedTransitionScope,animatedContentScope = animatedContentScope, route = route))
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin(context) else {
                navController.navigateForTransition(AppNavRoute.Transfer,route)
            }
        }
    )
}