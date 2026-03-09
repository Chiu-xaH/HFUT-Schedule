package com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.destination.TransferMajorDestination
import com.hfut.schedule.ui.screen.AppNavRoute

import com.xah.navigation.utils.LocalNavController

import com.xah.uicommon.component.text.ScrollText


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Transfer(
    ifSaved : Boolean,
){
    val navController = LocalNavController.current
    val context = LocalContext.current

    TransplantListItem(
        headlineContent = { ScrollText(text =stringResource(AppNavRoute.TransferMajor.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.TransferMajor.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin(context) else {
                navController.push(TransferMajorDestination)
            }
        }
    )
}