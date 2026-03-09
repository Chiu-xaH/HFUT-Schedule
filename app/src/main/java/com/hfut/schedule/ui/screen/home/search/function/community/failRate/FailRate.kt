package com.hfut.schedule.ui.screen.home.search.function.community.failRate


import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.containerBackDrop
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.status.PrepareSearchIcon
import com.hfut.schedule.ui.destination.FailRateDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.color.textFiledAllTransplant
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.mirror.util.rememberShaderState
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.xah.uicommon.style.color.topBarTransplantColor
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun FailRate() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(AppNavRoute.FailRate.label)) },
        leadingContent = {
            Icon(painterResource(AppNavRoute.FailRate.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(FailRateDestination)
        }
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FailRateScreen(
    vm: NetWorkViewModel,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    var input by remember { mutableStateOf( "") }


    LaunchedEffect(Unit) {
        vm.failRateData.emitPrepare()
    }

    val uiState by vm.failRateData.state.collectAsState()
    var firstUse by remember { mutableStateOf(true) }
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
            firstUse = false
        }
    }
    LaunchedEffect(page) {
        if(!firstUse) {
            refreshNetwork()
        }
    }
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column (
                modifier = Modifier.topBarBlur(hazeState),
            ){
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(stringResource(AppNavRoute.FailRate.label)) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        colors = textFiledAllTransplant(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .containerBackDrop(backdrop, MaterialTheme.shapes.medium)
                        ,
                        value = input,
                        onValueChange = {
                            input = it
                        },
                        label = { Text("输入科目名称" ) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { refreshNetwork() }
                                }) {
                                Icon(painter = painterResource(R.drawable.search), contentDescription = "description")
                            }
                        },
                        shape = MaterialTheme.shapes.medium,
//                        colors = textFiledTransplant(),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .hazeSource(hazeState)
                .backDropSource(backdrop)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork, prepareContent = { PrepareSearchIcon() }) {
                FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it },innerPadding,hazeState)
            }
        }
    }
//    }
}

var permit = 1
@Composable
fun ApiToFailRate(
    input : String,
    vm: NetWorkViewModel,
    hazeState: HazeState,
    innerPadding : PaddingValues
) {
    val uiState by vm.failRateData.state.collectAsState()
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit = {
        SharedPrefs.prefs.getString("TOKEN","")?.let {
            vm.failRateData.clear()
            vm.searchFailRate(it,input,page)
        }
    }
    LaunchedEffect(page) {
        refreshNetwork()
    }
    CommonNetworkScreen(uiState, onReload = refreshNetwork) {
        FailRateUI(vm,page,nextPage = { page = it }, previousPage = { page = it },innerPadding,hazeState)
    }
}