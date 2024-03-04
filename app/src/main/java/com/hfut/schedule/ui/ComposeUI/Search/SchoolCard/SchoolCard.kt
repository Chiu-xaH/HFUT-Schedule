package com.hfut.schedule.ui.ComposeUI.Search.SchoolCard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.activity.CardActivity
import com.hfut.schedule.activity.LoginSuccessActivity
import com.hfut.schedule.logic.Enums.BottomBarItems
import com.hfut.schedule.logic.Enums.CardBarItems
import com.hfut.schedule.logic.datamodel.NavigationBarItemData
import com.hfut.schedule.logic.utils.GetDate
import com.hfut.schedule.logic.utils.OpenAlipay
import com.hfut.schedule.logic.utils.SharePrefs
import com.hfut.schedule.logic.datamodel.zjgd.BillResponse
import com.hfut.schedule.logic.datamodel.zjgd.records
import com.hfut.schedule.logic.utils.AndroidVersion
import com.hfut.schedule.logic.utils.SharePrefs.prefs
import com.hfut.schedule.ui.ComposeUI.BottomBar.SearchScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.SettingsScreen
import com.hfut.schedule.ui.ComposeUI.BottomBar.TodayScreen
import com.hfut.schedule.ui.ComposeUI.SavedCourse.SaveCourse
import com.hfut.schedule.ui.ComposeUI.Search.Electric.WebViewScreen
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.BottomBars.CardBills
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.BottomBars.CardCounts
import com.hfut.schedule.ui.ComposeUI.Search.SchoolCard.BottomBars.CardFunctions
import com.hfut.schedule.ui.UIUtils.MyToast
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SchoolCardItem(vm : LoginSuccessViewModel) {

    val interactionSource2 = remember { MutableInteractionSource() }
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val scale2 = animateFloatAsState(
        targetValue = if (isPressed2) 0.9f else 1f, // 按下时为0.9，松开时为1
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "" // 使用弹簧动画
    )

    var card = prefs.getString("card","00")
    val bd = BigDecimal(card)
    val str = bd.setScale(2, RoundingMode.HALF_UP).toString()

    ListItem(
        headlineContent = { Text(text = "一卡通   ${str} 元") },
        leadingContent = { Icon(painterResource(R.drawable.credit_card), contentDescription = "Localized description",) },
        trailingContent={
                FilledTonalIconButton(
                    modifier = Modifier.scale(scale2.value),
                    interactionSource = interactionSource2,
                    onClick = { OpenAlipay.openAlipay(MyApplication.AlipayCardURL) }
                ) { Icon( painterResource(R.drawable.add), contentDescription = "Localized description",) }
        },
        modifier = Modifier.clickable {
            val it = Intent(MyApplication.context, CardActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            MyApplication.context.startActivity(it)
        }
    )
}