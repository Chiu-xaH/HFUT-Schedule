package com.hfut.schedule.ui.screen.home.search.function.report

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.xah.navigation.util.LocalNavController
import com.xah.common.ui.component.text.ScrollText

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun TermReport() {
    val navController = LocalNavController.current
    TransplantListItem(
        headlineContent = {
            ScrollText(text = TermReportDestination.title.asString())
        },
        leadingContent = {
            Icon(painterResource(TermReportDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(TermReportDestination)
        }
    )
}
