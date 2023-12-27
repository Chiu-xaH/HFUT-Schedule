package com.hfut.schedule.ui.ComposeUI.Settings

import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSetting() {
    val switch_api = SharePrefs.prefs.getBoolean("SWITCHMYAPI",true)
    var showapi by remember { mutableStateOf(switch_api) }
    val switch_card = SharePrefs.prefs.getBoolean("SWITCHCARD",true)
    var showcard by remember { mutableStateOf(switch_card) }
    val sp = PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
    if (sp.getBoolean("SWITCHCARD", true) != showcard) { sp.edit().putBoolean("SWITCHCARD", showcard).apply() }
    if (sp.getBoolean("SWITCHMYAPI", true) != showapi) { sp.edit().putBoolean("SWITCHMYAPI", showapi).apply() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("聚焦编辑") },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()
        ){
            ListItem(
                headlineContent = { Text(text = "网络接口") },
                supportingContent = { Text(text = "本接口提供了除学校系统之外的聚焦信息") },
                leadingContent = { Icon(painterResource(R.drawable.api), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showapi, onCheckedChange = {showapich -> showapi = showapich }) },
                modifier = Modifier.clickable { showapi = !showapi }
            )
            ListItem(
                headlineContent = { Text(text = "聚焦显示一卡通") },
                leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
                trailingContent = { Switch(checked = showcard, onCheckedChange = { showcardch -> showcard = showcardch }) },
                modifier = Modifier.clickable { showcard = !showcard }
            )
            ListItem(
                headlineContent = { Text(text = "本地数据编辑") },
                supportingContent = { Text(text = "由用户自行添加的数据,不会上传到云端,只在本地存储可见") },
                leadingContent = { Icon(painterResource(R.drawable.add_circle), contentDescription = "Localized description",) },
                // trailingContent = { Switch(checked = showcard, onCheckedChange = { showcardch -> showcard = showcardch }) },
                modifier = Modifier.clickable {  }
            )
            //用BottomSheet罗列出所有添加的卡片
            //选择卡片-->  删除   编辑
        }
    }

}