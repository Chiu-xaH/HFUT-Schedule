package com.hfut.schedule.ui.screen.home.search.function.jxglstu.totalCourse

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.input.CustomTextField
import com.hfut.schedule.ui.nav.destination.TermCoursesDestination
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur

import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.util.rememberShaderState
import com.xah.navigation.util.LocalNavController
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CourseTotal(
    ifSaved : Boolean,
) {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = TermCoursesDestination.TITLE.asString()) },
        leadingContent = {
            Icon(painterResource(TermCoursesDestination.ICON), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(
                TermCoursesDestination(
                    ifSaved,
                    "SEARCH"
                )
            )
        }
    )
}


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TotalCourseScreen(
    vm : NetWorkViewModel,
    ifSaved : Boolean,
) {
    var sortType by rememberSaveable { mutableStateOf(true) }

    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var input by rememberSaveable() { mutableStateOf("") }
    val backdrop = rememberLayerBackdrop()

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(TermCoursesDestination.TITLE.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                    actions = {
                        LiquidButton(
                            onClick = {
                                sortType = !sortType
                            },
                            backdrop = backdrop,
                            isCircle = false,
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Text(text = if(sortType) "开课时间" else "学分高低")
                        }
                    }
                )
                CustomTextField(
                    colors = textFiledAllTransplant(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP)
                        .containerBackDrop(backdrop, MaterialTheme.shapes.medium),
                    input = input,
                    label = { Text("搜索 学院、课程、代码、类型")},
                    leadingIcon = {
                        Icon(painterResource(R.drawable.search),null)
                    }
                ) { input = it }
                Spacer(Modifier.height(CARD_NORMAL_DP))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backdrop)
                .fillMaxSize()
        ) {
            CourseTotalUI(
                TotalCourseDataSource.MINE,
                sortType,
                vm,
                ifSaved,
                innerPadding = innerPadding,
                input = input
            )
        }
    }
}


