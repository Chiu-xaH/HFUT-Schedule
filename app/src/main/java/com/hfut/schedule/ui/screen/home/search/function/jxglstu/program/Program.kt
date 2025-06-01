package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.item
import com.hfut.schedule.logic.util.network.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoard
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.AnimationCardListItem
import com.hfut.schedule.ui.component.BottomTip
import com.hfut.schedule.ui.component.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.EmptyUI
import com.hfut.schedule.ui.component.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.LoadingLargeCard
import com.hfut.schedule.ui.component.LoadingUI
import com.hfut.schedule.ui.component.ScrollText
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.courseSearch.ApiForCourseSearch
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import com.hfut.schedule.ui.style.HazeBottomSheet
import com.hfut.schedule.ui.style.textFiledTransplant
import com.hfut.schedule.ui.util.MyAnimationManager
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program(vm : NetWorkViewModel, ifSaved : Boolean, hazeState: HazeState) {
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var showBottomSheet_search by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { ScrollText(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        trailingContent = {
            FilledTonalIconButton(
                onClick = {
                    showBottomSheet_search = true
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(painterResource(R.drawable.search),null, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier.clickable {
            if (prefs.getString("program","")?.contains("children") == true || !ifSaved) {
                showBottomSheet_Program = true
            }
            else refreshLogin()
        }
    )



    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            showBottomSheet = showBottomSheet_search,
            hazeState = hazeState,
        ) {
            ProgramSearch(vm,ifSaved,hazeState)
        }
    }


    if (showBottomSheet_Program ) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_Program = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_Program
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    HazeBottomSheetTopBar("培养方案") {
                        FilledTonalButton(
                            onClick = {
                                showBottomSheet_search = true
                            }
                        ) {
                            Text("全校培养方案")
                        }
                    }
                },
            ) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    ProgramScreen(vm,ifSaved,hazeState)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestProgram(vm: NetWorkViewModel, hazeState: HazeState) {
    var showBottomSheet_search by remember { mutableStateOf(false) }


    if (showBottomSheet_search) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet_search = false },
            hazeState = hazeState,
            showBottomSheet = showBottomSheet_search
        ) {
            ProgramSearch(vm, true, hazeState )
        }
    }
    TransplantListItem(
        headlineContent = { Text(text = "全校培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            showBottomSheet_search = true
        }
    )
}