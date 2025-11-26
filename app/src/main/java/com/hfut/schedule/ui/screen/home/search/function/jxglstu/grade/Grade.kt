package com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.MainHost
import com.hfut.schedule.ui.screen.home.search.function.huiXin.shower.getInGuaGua
import com.hfut.schedule.ui.screen.xwx.checkXwxLogin
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.XwxViewModel
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.component.titleElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.transition.util.navigateAndSaveForTransition
import com.xah.uicommon.component.text.ScrollText
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun Grade(
    ifSaved : Boolean,
    navController : NavHostController,
)  {
    val viewModel = viewModel { XwxViewModel() }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val route = remember { AppNavRoute.Grade.receiveRoute() }
    TransplantListItem(
        headlineContent = {
            ScrollText(text = AppNavRoute.Grade.label)
        },
        leadingContent = {
            Icon(painterResource(AppNavRoute.Grade.icon), contentDescription = null,modifier = Modifier.iconElementShare(route = route))
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    scope.launch {
                        loading = true
                        goToXwx(viewModel,context)
                        loading = false
                    }
                },
            ) { Icon( painterResource(R.drawable.attach_file), contentDescription = "Localized description",
                Modifier.size(21.dp)) }
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.Grade,AppNavRoute.Grade.withArgs(ifSaved))
        }
    )
}

suspend fun goToXwx(viewModel: XwxViewModel, context : Context) {
    if(!checkXwxLogin(viewModel,context)) {
        Starter.loginXwx(context)
    } else {
        Starter.startXwx(context)
    }
}
