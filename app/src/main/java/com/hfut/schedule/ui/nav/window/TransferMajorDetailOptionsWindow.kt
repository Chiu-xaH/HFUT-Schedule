package com.hfut.schedule.ui.nav.window

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.JumpTransitionEffectWallpaper
import com.hfut.schedule.logic.util.sys.showDevelopingToast
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.nav.destination.NewsApiDestination
import com.hfut.schedule.ui.nav.window.base.FloatingWindow
import com.sharednav.common.helper.NoneRoundShape
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.util.text
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.windowToDestination

data class TransferMajorDetailOptionsWindow(
    val sortByHot : Boolean,
    val sortByHotOnChanged : (Boolean) -> Unit
) : FloatingWindow() {
    override val key = "transfer_major_detail_options"
    override val title = text("选项")

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun BoxScope.Content() {
        val floatingController = LocalFloatingController.current
        val navController = LocalNavController.current

        Box(modifier = Modifier.fillMaxSize()) {
            SharedContent(
                shape = MaterialTheme.shapes.large,
                key = key,
                contentStrategy = ContentStrategy.Shared(keepShowContainer = false),
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(APP_HORIZONTAL_DP)
                    .align(Alignment.TopEnd)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = NoneRoundShape
                ) {
                    Column(modifier = Modifier.fillMaxWidth(2/3f)) {
                        TransplantListItem(
                            headlineContent = {
                                Text("按热度排序")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.mode_heat), null
                                )
                            },
                            trailingContent = {
                                if(sortByHot) {
                                    Icon(
                                        painterResource(R.drawable.check), null
                                    )
                                }
                            },
                            modifier = Modifier.clickable {
                                sortByHotOnChanged(!sortByHot)
                                floatingController.pop()
                            }
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = {
                                Text("通知公告")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.stream), null
                                )
                            },
                            modifier = Modifier.clickable {
                                windowToDestination(
                                    floatingController,
                                    navController,
                                    NewsApiDestination(NewsApiDestination.Keyword.TRANSFER_MAJOR.keyword),
                                    effect = JumpTransitionEffectWallpaper()
                                )
                            }
                        )
                        PaddingHorizontalDivider()
                        TransplantListItem(
                            headlineContent = {
                                Text("分享")
                            },
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.ios_share), null
                                )
                            },
                            modifier = Modifier.clickable {
                                // TODO 图片形式保存
                                showDevelopingToast()
                                floatingController.pop()
                            }
                        )
                    }
                }
            }
        }
    }
}