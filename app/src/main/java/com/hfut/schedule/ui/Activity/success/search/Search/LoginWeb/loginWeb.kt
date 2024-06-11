package com.hfut.schedule.ui.Activity.success.search.Search.LoginWeb

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.ui.UIUtils.ScrollText
import org.jsoup.Jsoup

fun getIdentifyID() : String? {
    return try {
        val info = SharePrefs.prefs.getString("info","")
        val doc = info?.let { Jsoup.parse(it) }
        val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
        val seven = chineseid?.takeLast(7)
        var id = ""
        if (seven != null) {
            if(seven.last() == 'X') id = seven.take(6)
            else id = seven.takeLast(6)
        }
        id
    } catch (e : Exception) {
        null
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginWeb(vmUI : UIViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    ListItem(
        headlineContent = { ScrollText(text = "校园网登录") },
       // overlineContent = { ScrollText(text = "快速通道")},
        leadingContent = {
            Icon(
                painterResource(R.drawable.net),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { showBottomSheet = true }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("校园网登录") }
                    )
                },) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    loginWebUI(vmUI)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}



