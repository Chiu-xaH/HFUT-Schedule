package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.logic.util.sys.queryCalendars
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.container.MyCustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import kotlinx.coroutines.launch

@Composable
fun CalendarSettingsScreen(innerPadding : PaddingValues) {
    val activity = LocalActivity.current
    LaunchedEffect(activity) {
        activity?.let {
            PermissionSet.checkAndRequestCalendarPermission(it)
        }
    }
    val defaultCalendarAccount by DataStoreManager.defaultCalendarAccount.collectAsState(initial = 1)

    val calendarAccounts by produceState<List<Pair<Long, String>>>(initialValue = emptyList()) {
        value = queryCalendars()
    }
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(innerPadding)) {
        DividerTextExpandedWith("日历账户") {
            MyCustomCard(containerColor = MaterialTheme.colorScheme.surface) {
                LazyColumn() {
//        item {
//            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
//        }
                    items(calendarAccounts.size, key = { calendarAccounts[it].first }) { index ->
                        val item = calendarAccounts[index]
                        val id = item.first
                        val isSelected = defaultCalendarAccount == id
                        TransplantListItem(
                            headlineContent = { Text(item.second) },
//                            leadingContent = {
//                                Text((index+1).toString())
//                            },
                            trailingContent = {
                                if(isSelected) {
                                    Icon(Icons.Filled.Check,null)
                                }
                            },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    DataStoreManager.saveDefaultCalendarAccount(id)
                                }
                            }
                        )
                        if(index + 1 != calendarAccounts.size)
                            PaddingHorizontalDivider()
                    }
//        item {
//            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
//        }
                }
            }
        }
    }
}