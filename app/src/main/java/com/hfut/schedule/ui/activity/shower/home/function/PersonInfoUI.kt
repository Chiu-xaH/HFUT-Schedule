package com.hfut.schedule.ui.activity.shower.home.function

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hfut.schedule.R
import com.hfut.schedule.logic.network.NetWork
import com.hfut.schedule.ui.activity.home.search.functions.shower.ShowerUI
import com.hfut.schedule.ui.activity.home.search.functions.shower.tranamt
import com.hfut.schedule.ui.activity.shower.login.getGuaGuaPersonInfo
import com.hfut.schedule.ui.utils.components.AppHorizontalDp
import com.hfut.schedule.ui.utils.components.LargeCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.style.Round
import com.hfut.schedule.viewmodel.NetWorkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuaGuaPersonInfoUI(vm: NetWorkViewModel) {

    val sheetState = rememberModalBottomSheetState(true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
//            , shape = Round(sheetState)
        ) {
            ShowerUI(vm,true)
        }
    }

    val personInfo = getGuaGuaPersonInfo()
    personInfo?.let {
        LargeCard("￥${tranamt(it.accountMoney)}", rightTop = {
            FilledTonalIconButton(
                onClick = {
                    showBottomSheet = true
                },
            ) {
                Icon(painterResource(R.drawable.add),null)
            }
        }) {
            Row {
                TransplantListItem(
                    headlineContent = { Text(text = personInfo.name) },
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.person), contentDescription = "")
                    },
                    overlineContent = { Text(text = "姓名") },
                    modifier = Modifier.weight(.4f)
                )
                TransplantListItem(
                    headlineContent = { ScrollText(text = personInfo.telPhone) },
                    leadingContent = {
                        Icon(painterResource(id = R.drawable.call), contentDescription = "")
                    },
                    overlineContent = { Text(text = "手机号") },
                    modifier = Modifier.weight(.6f)
                )
            }
        }
    }
}