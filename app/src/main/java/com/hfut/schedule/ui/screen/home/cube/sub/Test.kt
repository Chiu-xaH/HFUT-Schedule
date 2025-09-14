package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import kotlinx.coroutines.launch


@OptIn(ExperimentalSharedTransitionApi::class)
@RequiresApi(36)
@Composable
fun TEST(vm : NetWorkViewModel,innerPaddings : PaddingValues,navController : NavHostController) {
    val scope = rememberCoroutineScope()
    val uiState by vm.zhiJianCourseResp.state.collectAsState()
    Column(Modifier.padding(innerPaddings).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(APP_HORIZONTAL_DP*2))
        Button(
            onClick = {
                scope.launch {
                    vm.zhiJianCourseResp.clear()
                    vm.getZhiJianCourses(getPersonInfo().studentId!!,"2025-09-16",prefs.getString("ZhiJian","") ?: "")
                }
            }
        ) {
            Text("发送")
        }
        CommonNetworkScreen(uiState, onReload = null, isFullScreen = false) {
            val data = (uiState as UiState.Success<*>).data
            Text(data.toString())
        }
    }
}
