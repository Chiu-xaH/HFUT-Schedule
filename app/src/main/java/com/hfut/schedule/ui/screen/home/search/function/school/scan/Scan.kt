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
import com.hfut.schedule.ui.nav.destination.ScanQrCodeDestination
import com.xah.common.ui.component.text.ScrollText


import com.xah.navigation.util.LocalNavController


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun Scan() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = ScanQrCodeDestination.title.asString()) },
        overlineContent = { ScrollText(stringResource(R.string.navigation_label_scan_qr_code_description))},
        leadingContent = {
            Icon(painterResource(ScanQrCodeDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(ScanQrCodeDestination)
        }
    )
}