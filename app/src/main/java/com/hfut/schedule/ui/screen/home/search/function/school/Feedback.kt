package com.hfut.schedule.ui.screen.home.search.function.school

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.helper.Constant
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.nav.window.FeedbackWindow
import com.hfut.schedule.ui.nav.window.RepairWindow

import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.common.ui.component.text.ScrollText
import com.xah.floating.util.LocalFloatingController
import dev.chrisbanes.haze.HazeState

@Composable
fun Feedback() {
    val floatController = LocalFloatingController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(R.string.navigation_label_feedback)) },
        leadingContent = { Icon(painter = painterResource(id = R.drawable.voice_selection), contentDescription = "") },
        modifier = Modifier.clickable {
            floatController.push(FeedbackWindow)
        }
    )
}