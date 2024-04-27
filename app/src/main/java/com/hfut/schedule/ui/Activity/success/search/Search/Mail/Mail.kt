package com.hfut.schedule.ui.Activity.success.search.Search.Mail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.ScrollText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay() {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val Savedusername = prefs.getString("Username", "")
    ListItem(
        headlineContent = { Text(text = "教育邮箱") },
        overlineContent = { ScrollText(text = "${Savedusername}@mail.hfut.edu.cn")},
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mail), contentDescription = "") },
        modifier = Modifier.clickable {
            StartApp.StartUri("https://email.mail.hfut.edu.cn/")
        }
    )

    if (showBottomSheet ) {
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
                        title = { Text("教育邮箱") }
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    MailUI()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
@Composable
fun MailUI() {

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                ListItem(
                    headlineContent = { Text(text = "") },
                    supportingContent = {  Text(text ="" ) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.net), contentDescription = "Localized description",)
                    },
                    modifier = Modifier.clickable {
                        StartApp.StartUri("https://email.mail.hfut.edu.cn/")
                    }
                )
            }
}