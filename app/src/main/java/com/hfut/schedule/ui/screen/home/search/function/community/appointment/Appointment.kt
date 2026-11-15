package com.hfut.schedule.ui.screen.home.search.function.community.appointment

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.destination.AppointmentDestination


import com.xah.navigation.util.LocalNavController

import com.xah.common.ui.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Appointment() {
    val navController = LocalNavController.current
    TransplantListItem(
        headlineContent = { ScrollText(text = AppointmentDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(AppointmentDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(AppointmentDestination)
        }
    )
}

