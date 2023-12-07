package com.hfut.schedule.ui.ComposeUI.Search

import android.media.tv.TvContract.Programs
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.SharePrefs.prefs
import org.jsoup.Jsoup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Program() {
    val sheetState_Program = rememberModalBottomSheetState()
    var showBottomSheet_Program by remember { mutableStateOf(false) }
    var view by rememberSaveable { mutableStateOf("") }


    ListItem(
        headlineContent = { Text(text = "培养方案") },
        leadingContent = {
            Icon(
                painterResource(R.drawable.conversion_path),
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable {
            val program = prefs.getString("program", " <h2 class=\"info-title\"><i style=\"color: #ffa200;\" class=\"fa fa-warning highlight\"></i>未获取到</h2>")
            showBottomSheet_Program = true
            val doc = Jsoup.parse(program)
            view = doc.select("h2.info-title").text()
        }
    )


    if (showBottomSheet_Program ) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet_Program = false },
            sheetState = sheetState_Program
        ) {

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = { Text("培养方案") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding).fillMaxSize()
                ){
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center)
                    { Text(text = view) }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}