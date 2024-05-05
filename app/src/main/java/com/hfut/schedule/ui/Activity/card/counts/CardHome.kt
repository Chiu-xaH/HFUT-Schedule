package com.hfut.schedule.ui.Activity.card.counts

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.Activity.card.BillsIcons
import com.hfut.schedule.ui.Activity.card.bills.main.BillItem
import com.hfut.schedule.ui.Activity.card.bills.main.processTranamt
import com.hfut.schedule.ui.Activity.card.counts.main.CardCounts
import com.hfut.schedule.ui.Activity.card.counts.main.getbillmonth
import com.hfut.schedule.ui.Activity.card.counts.main.monthCount
import com.hfut.schedule.ui.Activity.card.function.main.todayCount
import com.hfut.schedule.ui.UIUtils.DividerText
import com.hfut.schedule.ui.UIUtils.MyToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun CardHome(innerPadding : PaddingValues,vm : LoginSuccessViewModel,blur : Boolean) {
    val TAB_DAY = 0
    val TAB_WEEK = 1
    val TAB_MONTH = 2
    val TAB_TERM = 3
    Column(modifier = Modifier.padding(innerPadding)) {
        //Spacer(modifier = Modifier.height(5.dp))
        var state by remember { mutableStateOf(TAB_DAY) }
        val titles = listOf("日","周","月","学期")
        Column {
            TabRow(selectedTabIndex = state) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = state == index,
                        onClick = { state = index },
                        text = { Text(text = title) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if(blur).5f else 1f))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        if(state == TAB_MONTH) {
            monthCount(vm)
        } else {
            LazyColumn() {
                when (state) {
                    TAB_DAY -> items(BillItem(vm).size) { item -> todayCount(vm, item) }

                    TAB_WEEK -> {
                        item {
                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                ListItem(headlineContent = { Text(text = "正在开发") })
                            }
                        }
                    }

                    TAB_TERM -> {
                        item {
                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                ListItem(headlineContent = { Text(text = "正在开发") })
                            }
                        }
                    }
                }
            }
        }
    }
}



