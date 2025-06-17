package com.hfut.schedule.ui.screen.home.cube.sub

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.logic.util.sys.PermissionManager
import com.hfut.schedule.logic.util.sys.queryCalendars
import com.hfut.schedule.ui.component.TransplantListItem
import kotlinx.coroutines.launch

@Composable
fun CalendarSettingsScreen(innerPadding : PaddingValues) {
    val activity = LocalActivity.current
    LaunchedEffect(activity) {
        activity?.let {
            PermissionManager.checkAndRequestCalendarPermission(it)
        }
    }
    val defaultCalendarAccount by DataStoreManager.defaultCalendarAccount.collectAsState(initial = 1)

    val calendarAccounts by produceState<List<Pair<Long, String>>>(initialValue = emptyList()) {
        value = queryCalendars()
    }
    val scope = rememberCoroutineScope()
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding()))
        }
        items(calendarAccounts.size, key = { calendarAccounts[it].first }) { index ->
            val item = calendarAccounts[index]
            val id = item.first
            val isSelected = defaultCalendarAccount == id
            TransplantListItem(
                headlineContent = { Text(item.second) },
                leadingContent = {
                    Text((index+1).toString())
                },
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
        }
        item {
            Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding()))
        }
    }
}