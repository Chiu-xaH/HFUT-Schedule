package com.hfut.schedule.ui.screen.home.search.function.school.ietp

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.ParseJsons.getMy
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.TransplantListItem
   
import com.hfut.schedule.ui.screen.AppNavRoute
import com.xah.transition.component.iconElementShare
import com.xah.transition.util.navigateAndSaveForTransition


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun IETP(
    navController : NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val route = remember { AppNavRoute.WebView.shareRoute(MyApplication.IETP_URL) }
    val icon = remember { R.drawable.groups }
    val title = remember { "大创系统" }
    TransplantListItem(
        headlineContent = { Text(text = title) },
        leadingContent = {
            with(sharedTransitionScope) {
                Icon(painterResource(icon), contentDescription = null,modifier = iconElementShare(animatedContentScope = animatedContentScope, route = route))
            }
        },
        modifier = Modifier.clickable {
            navController.navigateAndSaveForTransition(AppNavRoute.WebView.withArgs(
                url = MyApplication.IETP_URL,
                title = title,
                icon = icon,
            ))
        }
    )
}
