package com.hfut.schedule.ui.ComposeUI.BottomBar

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.JxglstuViewModel
import com.hfut.schedule.ui.ComposeUI.Search.FWDT
import com.hfut.schedule.ui.ComposeUI.Search.Library
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard
import com.hfut.schedule.ui.ComposeUI.Search.XuanquItem
import com.hfut.schedule.ui.ComposeUI.Settings.CardItem
import com.hfut.schedule.ui.ComposeUI.Settings.DyColorItem
import com.hfut.schedule.ui.DynamicColor.DynamicColorViewModel
import com.hfut.schedule.ui.ComposeUI.Settings.SettingsItems

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm : JxglstuViewModel,
                   showItem : Boolean,
                   showlable : Boolean,
                   showlablechanged: (Boolean) -> Unit,
                   dynamicColorViewModel: DynamicColorViewModel,
                   dynamicColorEnabled: Boolean,
                   onChangeDynamicColorEnabled: (Boolean) -> Unit,
                   ) {
    val sp =
        PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCH", true) != showlable) { sp.edit().putBoolean("SWITCH", showlable).apply() }
    if (sp.getBoolean("dyswitch", true) != dynamicColorEnabled) { sp.edit().putBoolean("dyswitch", dynamicColorEnabled).apply() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("肥工教务通") }
            )
        },) {innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()) {
            Spacer(modifier = Modifier.height(10.dp))

            CardItem()

            if(showItem) {
                Library(vm)
                XuanquItem(vm)
               // FWDT()
                Divider()
            }

            ListItem(
                headlineContent = { Text(text = "显示标签") },
                leadingContent = { Icon(painterResource(R.drawable.label), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showlable, onCheckedChange = showlablechanged) }
            )

            DyColorItem(dynamicColorViewModel,dynamicColorEnabled, onChangeDynamicColorEnabled )

            SettingsItems()

            Spacer(modifier = Modifier.height(90.dp))

        }
    }
}