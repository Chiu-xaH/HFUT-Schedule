package com.hfut.schedule.ui.activity.home.search.functions.mail

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import com.hfut.schedule.ui.utils.components.LoadingUI
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.logic.utils.Starter.refreshLogin
import com.hfut.schedule.logic.utils.reEmptyLiveDta
import com.hfut.schedule.ui.utils.components.BottomTip
import com.hfut.schedule.ui.utils.style.RowHorizontal
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.WebDialog
import com.hfut.schedule.ui.utils.components.WebViewScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mail(ifSaved : Boolean,vm : NetWorkViewModel) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val savedUsername = prefs.getString("Username", "")
    val mail = "@mail.hfut.edu.cn"
    ListItem(
        headlineContent = { Text(text = "邮箱") },
        overlineContent = { ScrollText(text = mail) },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.mail), contentDescription = "") },
        modifier = Modifier.clickable {
            if(ifSaved) refreshLogin()
            else
                showBottomSheet = true
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
                        title = { Text("学生邮箱") },
                    )
                },) {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ){
                    MailUI(vm)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MailUI(vm: NetWorkViewModel) {
    val token = prefs.getString("bearer","")
    val savedUsername = prefs.getString("Username", "")
    val mail = "$savedUsername@mail.hfut.edu.cn"

    var loading by remember { mutableStateOf(true) }
    var refresh by remember { mutableStateOf(true) }

    var url by remember { mutableStateOf("") }
    var used by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    WebDialog(showDialog,{ showDialog = false },url,mail)

    fun refresh() {
        loading = true
        CoroutineScope(Job()).launch {
            async { reEmptyLiveDta(vm.mailData) }.await()
            async { token?.let { vm.getMailURL(it) } }.await()
            async {
                Handler(Looper.getMainLooper()).post{
                    vm.mailData.observeForever { result ->
                        if (result != null) {
                            if(result.contains("success")) {
                                val data = getMail(vm)
                                url = data.data ?: ""
                                refresh = false
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }
    if(refresh) { refresh() }

    LaunchedEffect(used) {
        refresh()
    }
    if(loading) {
        LoadingUI(text = "正在登录 $mail")
    } else {
        RowHorizontal {
            Button(
                onClick = {
                    showDialog = true
                    used = !used
                }
            ) {
                Text("进入邮箱")
            }
        }
    }
}

data class MailResponse(
    val msg : String?,
    val data : String?
)

fun getMail(vm : NetWorkViewModel) : MailResponse {
    val json = vm.mailData.value
    return try {
        Gson().fromJson(json,MailResponse::class.java)
    } catch (e: Exception) {
        MailResponse("APP错误","")
    }
}
