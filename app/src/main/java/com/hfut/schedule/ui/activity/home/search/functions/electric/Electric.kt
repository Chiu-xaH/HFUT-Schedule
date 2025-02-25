package com.hfut.schedule.ui.activity.home.search.functions.electric

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
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.WebViewScreen
import com.hfut.schedule.ui.theme.FWDTColr
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.components.WebDialog

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : NetWorkViewModel, card : Boolean, vmUI : UIViewModel) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }


    val EndNumber = prefs.getString("EndNumber", "0")
    var room by remember { mutableStateOf("寝室电费") }
    if(EndNumber == "12" || EndNumber == "22") room = "空调"
    else if(EndNumber == "11" || EndNumber == "21") room = "照明"


    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            EleUI(vm = vm)
            }
        }

    val memoryEle = prefs.getString("memoryEle","0")

    TransplantListItem(
        headlineContent = { if(!card)Text(text = "寝室电费") else ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") },
        overlineContent = { if(!card) ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") else ScrollText(text = room) },
        leadingContent = { Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet  = true }
    )
}


