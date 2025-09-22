package com.hfut.schedule.ui.screen.home.search.function.school.sport.lepao

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.navigation.NavHostController
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP

import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.screen.home.search.function.huiXin.getHuiXinURL
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.ui.util.navigateForTransition
import com.xah.transition.component.containerShare
import com.xah.transition.component.iconElementShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.text.ScrollText
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LePaoYun(
    navController : NavHostController,
) {
    val context = LocalContext.current
    val icon = remember { R.drawable.sports_volleyball }
    val title = remember { "体测平台" }
    val route = AppNavRoute.WebView.shareRoute(MyApplication.PE_HOME_URL)
    val scope = rememberCoroutineScope()
    TransplantListItem(
        headlineContent = { ScrollText(text = title) },
        leadingContent = {
            Icon(painterResource(icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier.size(30.dp),
                onClick = {
                    Starter.startAppLaunch(Starter.AppPackages.LEPAO, context)
                },
            ) { Icon( painterResource(R.drawable.directions_run), contentDescription = null, modifier = Modifier.size(20.dp)) }
        },
        modifier = Modifier.clickable {
            scope.launch {
                Starter.startWebView(
                    navController,
                    url = MyApplication.PE_HOME_URL,
                    title = "$title(校园网)",
                    icon = icon,
                    cookie = prefs.getString("PE","")
                )
            }
        }
    )
}
