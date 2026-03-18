package com.hfut.schedule.ui.screen.home.search.function.school.hall

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.OfficeHallDestination
import com.hfut.schedule.ui.screen.AppNavRoute

import com.xah.navigation.util.LocalNavController

import com.xah.common.ui.component.text.ScrollText

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun OfficeHall() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.OfficeHall.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.OfficeHall.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(OfficeHallDestination)
        }
    )
}