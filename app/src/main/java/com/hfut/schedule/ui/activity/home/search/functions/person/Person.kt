package com.hfut.schedule.ui.activity.home.search.functions.person

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.HazeBottomSheet
import com.hfut.schedule.ui.utils.style.bottomSheetRound
import dev.chrisbanes.haze.HazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonUI(ifSaved : Boolean,hazeState: HazeState) {
    val sheetState_Person = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet_Person by remember { mutableStateOf(false) }

    TransplantListItem(
        headlineContent = { Text(text = "个人信息") },
        leadingContent = { Icon(painterResource(R.drawable.person), contentDescription = "Localized description",) },
        modifier = Modifier.clickable { showBottomSheet_Person = true }
    )

    if (showBottomSheet_Person) {

        HazeBottomSheet (
            onDismissRequest = {
                showBottomSheet_Person = false
            }, hazeState = hazeState,
            showBottomSheet = showBottomSheet_Person
//            sheetState = sheetState_Person,
//            shape = bottomSheetRound(sheetState_Person)
        ) { PersonItems(ifSaved) }
    }
}

