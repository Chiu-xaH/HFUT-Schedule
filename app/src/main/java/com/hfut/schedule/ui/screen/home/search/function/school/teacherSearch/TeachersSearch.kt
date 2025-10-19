package com.hfut.schedule.ui.screen.home.search.function.school.teacherSearch

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.status.PrepareSearchUI
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.color.textFiledTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.containerBackDrop
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.util.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.shader.GlassStyle
import com.xah.mirror.shader.glassLayer
import com.xah.mirror.util.rememberShaderState
import com.xah.mirror.util.shaderSource
import com.xah.transition.component.iconElementShare
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TeacherSearch(
    navController : NavHostController,
) {
    val route = remember { AppNavRoute.TeacherSearch.route }

    TransplantListItem(
        headlineContent = { ScrollText(text = "教师检索") },
        leadingContent = {
            Icon(painterResource(AppNavRoute.TeacherSearch.icon), contentDescription = null,modifier = Modifier.iconElementShare( route = route))
        },
        modifier = Modifier.clickable {
            navController.navigateForTransition(AppNavRoute.TeacherSearch,route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun TeacherSearchScreen(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.TeacherSearch.route }

    var name by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }

    val uiState by vm.teacherSearchData.state.collectAsState()

    val scope = rememberCoroutineScope()
    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(name,direction)
    }
    LaunchedEffect(Unit) {
        vm.teacherSearchData.emitPrepare()
    }
    val shaderState = rememberShaderState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backDrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = route,
        navHostController = navController,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState, )
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(AppNavRoute.TeacherSearch.label) },
                    navigationIcon = {
                        TopBarNavigationIcon(navController,route, AppNavRoute.TeacherSearch.icon)
                    },
                    actions = {
                        LiquidButton(
                            backdrop = backDrop,
                            isCircle = true,
                            onClick = { scope.launch { refreshNetwork() } },
                            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                        ) {
                            Icon(painterResource(R.drawable.search), contentDescription = "")
                        }
                    }
                )
                val s = MaterialTheme.shapes.medium
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("姓名" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        modifier = Modifier
                            .containerBackDrop(backDrop, MaterialTheme.shapes.medium)
                            .weight(.5f),
                        value = direction,
                        onValueChange = {
                            direction = it
                        },
                        label = { Text("研究方向" ) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = textFiledTransplant(),
                    )
                }
            }

        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .shaderSource(shaderState)
                .backDropSource(backDrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchUI() }) {
                TeacherListUI(vm,innerPadding)
            }
        }
    }
//    }
}

@Composable
fun ApiToTeacherSearch(input : String,vm: NetWorkViewModel,innerPadding : PaddingValues) {
    val uiState by vm.teacherSearchData.state.collectAsState()

    val refreshNetwork: suspend () -> Unit = {
        vm.teacherSearchData.clear()
        vm.searchTeacher(input)
    }
    LaunchedEffect(input) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        TeacherListUI(vm, innerPadding)
    }
}