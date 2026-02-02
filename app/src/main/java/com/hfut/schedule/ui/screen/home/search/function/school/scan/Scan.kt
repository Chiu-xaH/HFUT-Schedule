package com.hfut.schedule.ui.screen.home.search.function.school.scan

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.xah.uicommon.component.text.ScrollText
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.xah.transition.component.iconElementShare

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun Scan(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.ScanQrCode.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.ScanQrCode.label)) },
        overlineContent = { ScrollText(stringResource(R.string.navigation_label_scan_qr_code_description))},
        leadingContent = {
            Icon(painterResource(AppNavRoute.ScanQrCode.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.ScanQrCode,route)
        }
    )
}