package com.hfut.schedule.ui.screen.home.search.function.one.emptyRoom

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
import com.hfut.schedule.ui.destination.ClassroomDestination
import com.hfut.schedule.ui.screen.AppNavRoute

import com.xah.navigation.utils.LocalNavController

import com.xah.uicommon.component.text.ScrollText


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Classroom() {
    val navController = LocalNavController.current
    val route = remember { AppNavRoute.Classroom.route }
    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.Classroom.label)) },
        leadingContent = {
            Icon(
                painterResource(AppNavRoute.Classroom.icon),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            navController.push(ClassroomDestination)
        }
    )
}
