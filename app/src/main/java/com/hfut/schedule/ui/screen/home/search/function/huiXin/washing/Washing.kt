package com.hfut.schedule.ui.screen.home.search.function.huiXin.washing

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.status.EmptyIcon
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.destination.HaiLeWashingDestination
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.xah.container.container.SharedContainer
import com.xah.navigation.utils.LocalNavController
import com.xah.uicommon.component.text.ScrollText
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun Washing(
    hazeState: HazeState,
) {
    val navController = LocalNavController.current
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
//            isFullScreen = false,
            showBottomSheet = showBottomSheet,
        ) {
            WashingUI()
        }
    }

    TransplantListItem(
        headlineContent = { ScrollText(text = stringResource(R.string.navigation_label_laundry)) },
        leadingContent = {
            Icon(painterResource(id = R.drawable.local_laundry_service), contentDescription = "")
        },
        trailingContent = {
            SharedContainer(
                key = HaiLeWashingDestination.key,
                shape = CircleShape,
                containerColor = IconButtonDefaults.filledTonalIconButtonColors().containerColor
            ) {
                NoPadding {
                    FilledTonalIconButton(
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.size(30.dp),
                        onClick = {
                            navController.push(HaiLeWashingDestination)
                        },
                    ) {
                        Icon(
                            painterResource(R.drawable.search),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier.clickable {
            showBottomSheet = true
        }
    )
}


private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WashingUI() {
    val titles = remember { listOf("合肥","宣城") }
    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampusRegion()) {
            CampusRegion.XUANCHENG -> XUANCHENG_TAB
            CampusRegion.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current


    //布局///////////////////////////////////////////////////////////////////////////
    Column() {
        HazeBottomSheetTopBar("洗衣机", isPaddingStatusBar = false) {
            FilledTonalButton(onClick = {
                navController.push(HaiLeWashingDestination)
            }) {
                Text(stringResource(AppNavRoute.HaiLeWashing.label))
            }
        }
        CustomTabRow(pagerState,titles)
        HorizontalPager(state = pagerState) { page ->
            Column() {
                when(page) {
                    HEFEI_TAB -> {
                        CardListItem(
                            headlineContent = { Text("官方充值查询入口") },
                            modifier = Modifier.clickable {
                               scope.launch {
                                   Starter.startWebView(context,url = MyApplication.HUI_XIN_URL + "charge-app/?name=pays&appsourse=ydfwpt&id=${FeeType.WASHING_HEFEI.code}&name=pays&paymentUrl=${MyApplication.HUI_XIN_URL}plat&token=" + auth, title = "慧新易校")
                               }
                            },
                            trailingContent = {
                                Icon(painterResource(R.drawable.arrow_forward),null)
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.search),null)
                            },
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                    XUANCHENG_TAB -> {
                        EmptyIcon("请使用${stringResource(AppNavRoute.HaiLeWashing.label)}")
                    }
                }
                Spacer(Modifier.height(APP_HORIZONTAL_DP*3))
            }
        }
    }
}



