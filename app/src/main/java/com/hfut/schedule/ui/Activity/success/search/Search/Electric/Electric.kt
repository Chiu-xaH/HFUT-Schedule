package com.hfut.schedule.ui.Activity.success.search.Search.Electric

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.NetWorkViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.Round
import com.hfut.schedule.ui.UIUtils.ScrollText
import com.hfut.schedule.ui.UIUtils.WebViewScreen
import com.hfut.schedule.ui.theme.FWDTColr

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, vmUI : UIViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }



    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()



    val EndNumber = prefs.getString("EndNumber", "0")
    var room by remember { mutableStateOf("寝室电费") }
    if(EndNumber == "12" || EndNumber == "22") room = "空调"
    else if(EndNumber == "11" || EndNumber == "21") room = "照明"


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
            , shape = Round(sheetState)
        ) {

            EleUI(vm = vm)
            }
        }
    val showAdd = prefs.getBoolean("SWITCHELEADD",true)
    val memoryEle = prefs.getString("memoryEle","0")
    var showDialog by remember { mutableStateOf(false) }
    val auth = SharePrefs.prefs.getString("auth","")
    val url = MyApplication.ZJGDBillURL + "charge-app/?name=pays&appsourse=ydfwpt&id=261&name=pays&paymentUrl=http://121.251.19.62/plat&token=" + auth

    val switch_startUri = SharePrefs.prefs.getBoolean("SWITCHSTARTURI",true)
    if (showDialog) {
        if(switch_startUri) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = FWDTColr,
                                titleContentColor = Color.White,
                            ),
                            actions = {
                                Row{
                                    IconButton(onClick = { StartApp.startUri( url) }) { Icon(painterResource(id = R.drawable.net), contentDescription = "", tint = Color.White) }
                                    IconButton(onClick = { showDialog = false }) { Icon(painterResource(id = R.drawable.close), contentDescription = "", tint = Color.White) }
                                }

                            },
                            title = { Text("宣城校区 电费缴纳") }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        WebViewScreen(url)
                    }
                }
            }
        } else {
            StartApp.startUri(url)
        }
    }
    ListItem(
        headlineContent = { if(!card)Text(text = "寝室电费") else ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") },
        overlineContent = { if(!card) ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") else ScrollText(text = room)},
        leadingContent = { Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",) },
        trailingContent = {
            if(card && showAdd)
            FilledTonalIconButton(
                modifier = Modifier
                    .scale(scale.value)
                    .size(30.dp),
                interactionSource = interactionSource,
                onClick = {
                   showDialog = true
                   // ClipBoard.copy(input)
                  //  MyToast("已将房间号复制到剪切板")
                          },
              //  colors =  if(test.length <= 4) {
              //      IconButtonDefaults.filledTonalIconButtonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
              //  } else IconButtonDefaults.filledTonalIconButtonColors()
            ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
         },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}


