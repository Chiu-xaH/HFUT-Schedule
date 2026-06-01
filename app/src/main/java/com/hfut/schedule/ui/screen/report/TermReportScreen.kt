package com.hfut.schedule.ui.screen.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.hfut.schedule.logic.util.parse.SemesterParser
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.nav.destination.TermReportDestination
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.ui.component.status.LoadingScreen
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermReportScreen(vm: NetWorkViewModel) {
    val hazeState = rememberHazeState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    var semester by remember { mutableStateOf<Int?>(null) }
    var initialSemester by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        initialSemester = SemesterParser.getSemester()
        semester = initialSemester
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(TermReportDestination.title.asString()) },
                navigationIcon = { TopBarNavigationIcon() }
            )
        }
    ) { innerPadding ->
        if(semester == null) {
            LoadingScreen()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(hazeState)
            ) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    item { InnerPaddingHeight(innerPadding, true) }
                    item {
                        AcademicReportSection(vm, semester!!) { latestSemester ->
                            semester = latestSemester
                        }
                    }
                    item { AcademicAnalysisSection(vm, semester!!) }
                    item { ExpenseAnalysisSection(vm, semester!!) }
                    item { LibraryReportSection(vm) }
                    item { LifeReportSection(vm, semester!!) }
                    item { PaddingForPageControllerButton() }
                    item { InnerPaddingHeight(innerPadding, false) }
                }

                PageController(
                    modifier = Modifier.padding(innerPadding),
                    listState = listState,
                    currentPage = semester!!,
                    onNextPage = { semester = it },
                    onPreviousPage = { semester = it },
                    gap = 20,
                    text = SemesterParser.parseSemester(semester!!) ?: "未知学期",
                    paddingBottom = false,
                    resetPage = initialSemester
                )
            }
        }
    }
}
