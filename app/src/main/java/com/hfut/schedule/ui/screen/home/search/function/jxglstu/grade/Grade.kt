package com.hfut.schedule.ui.screen.home.search.function.jxglstu.grade

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.nav.destination.GradeDestination
import com.hfut.schedule.ui.nav.destination.XiaoWuXingDestination
import com.hfut.schedule.ui.nav.destination.XiaoWuXingLoginDestination
import com.hfut.schedule.ui.screen.xwx.checkXwxLogin
import com.hfut.schedule.ui.util.state.GlobalEventHolder
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.navigation.util.LocalNavController
import com.xah.common.ui.component.text.ScrollText
import com.xah.navigation.controller.NavigationController
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun Grade(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
)  {
    val navController = LocalNavController.current
    var loading by remember { mutableStateOf(false) }
    val gradeCount by GlobalEventHolder.gradeCountCheckCallback.flow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    TransplantListItem(
        headlineContent = {
            ScrollText(text = GradeDestination.TITLE.asString())
        },
        leadingContent = {
            if(loading) {
                LoadingIcon()
            } else {
                BadgedBox(badge = {
                    if(gradeCount != null && gradeCount != 0) {
                        Badge {
                            Text(text = gradeCount.toString())
                        }
                    }
                }) {
                    Icon(
                        painterResource(id = R.drawable.article),
                        contentDescription = "",
                    )
                }
            }
        },
        trailingContent = {
            FilledTonalIconButton(
                modifier = Modifier
                    .size(30.dp),
                onClick = {
                    scope.launch {
                        loading = true
                        goToXwx(vm,navController)
                        loading = false
                    }
                },
            ) {
                Icon(
                    painterResource(R.drawable.attach_file), contentDescription = "Localized description",
                    Modifier.size(21.dp)
                )
            }
        },
        modifier = Modifier.clickable {
            GlobalEventHolder.gradeCountCheckCallback.clear()
            navController.push(GradeDestination(ifSaved))
        }
    )
}

suspend fun goToXwx(viewModel: NetWorkViewModel, navController : NavigationController) {
    if(!checkXwxLogin(viewModel)) {
        navController.push(XiaoWuXingLoginDestination, effect = JumpTransitionEffectWallpaper())
    } else {
        navController.push(XiaoWuXingDestination, effect = JumpTransitionEffectWallpaper())
    }
}
