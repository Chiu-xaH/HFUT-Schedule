package com.hfut.schedule.ui.Activity.success.search.Search.Electric

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.ViewModel.UIViewModel
import com.hfut.schedule.logic.utils.ClipBoard
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.logic.utils.StartApp
import com.hfut.schedule.ui.UIUtils.MyToast
import com.hfut.schedule.ui.UIUtils.ScrollText

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Electric(vm : LoginSuccessViewModel,card : Boolean,vmUI : UIViewModel) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()


   // val BuildingsNumber = prefs.getString("BuildNumber", "0")
  //  val RoomNumber = prefs.getString("RoomNumber", "0")
    val EndNumber = prefs.getString("EndNumber", "0")
    var room by remember { mutableStateOf("寝室电费") }
    if(EndNumber == "12" || EndNumber == "22") room = "空调"
    else if(EndNumber == "11" || EndNumber == "21") room = "照明"

   // var room by remember { mutableStateOf(region) }


    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    if (showBottomSheet) {

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {

            EleUI(vm = vm)
            }
        }
    val showAdd = prefs.getBoolean("SWITCHELEADD",true)
    val memoryEle = prefs.getString("memoryEle","0")

    ListItem(
        headlineContent = { if(!card)Text(text = "寝室电费") else ScrollText(text = "￥${vmUI.electricValue.value ?: memoryEle}") },
        overlineContent = { if(!card) ScrollText(text = "校园网 宣城校区") else ScrollText(text = room)},
        leadingContent = { Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",) },
        trailingContent = {
            if(card && showAdd)
            FilledTonalIconButton(
                modifier = Modifier
                    .scale(scale.value)
                    .size(30.dp),
                interactionSource = interactionSource,
                onClick = {
                    StartApp.StartUri("http://172.31.248.26:8088")
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