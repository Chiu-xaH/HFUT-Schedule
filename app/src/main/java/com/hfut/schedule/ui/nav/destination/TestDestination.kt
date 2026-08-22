package com.hfut.schedule.ui.nav.destination

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.button.TopBarNavigationIconForControlCenter
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.nav.effect.ControlCenterTransitionEffect
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.ui.theme.pureMaskColor
import com.hfut.schedule.ui.util.loadBitmap
import com.hfut.schedule.ui.util.pickColorFromTop
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.color.TransparentSystemBars
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object TestDestination : NavDestination() {
    override val key = "test"
    override val title = text("开发者调试页面")
    override val icon = R.drawable.build

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
    @Composable
    override fun Content() {
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val backdrop = rememberLayerBackdrop()

        val customBackground by DataStoreManager.customBackground.collectAsState(initial = "")
        val useCustomBackground = customBackground != ""
        val pureMaskColor = pureMaskColor()
        var inFullScreen by remember { mutableStateOf(false) }
        var downDrag by remember { mutableFloatStateOf(0f) }
        val navController = LocalNavController.current
        val surfaceColor = MaterialTheme.colorScheme.surface

        LaunchedEffect(useCustomBackground) {
            inFullScreen = useCustomBackground
        }

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if(!inFullScreen) {
                    Column(
                        modifier = Modifier.topBarBlur(hazeState),
                    ) {
                        MediumTopAppBar(
                            scrollBehavior = scrollBehavior,
                            colors = topBarTransplantColor().copy(
                                titleContentColor = pureMaskColor
                            ),
                            title = { Text(title.asString()) },
                            navigationIcon = {
                                TopBarNavigationIcon(tint = pureMaskColor)
                            },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .backDropSource(backdrop)
                    .hazeSource(hazeState)
                    .fillMaxSize()
            ) {
                // 背景图层
                if (useCustomBackground) {
                    // 状态栏反色
                    val file = remember(customBackground) { File(customBackground) }
                    val color by produceState<Int?>(initialValue = null) {
                        value = withContext(Dispatchers.IO) {
                            val bitmap = loadBitmap(file) ?: return@withContext null
                            val result = pickColorFromTop(bitmap)
                            bitmap.recycle()
                            result
                        }
                    }
                    TransparentSystemBars(color?.let { it1 -> Color(it1) })
                    GlideImage(
                        model = file,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                inFullScreen = !inFullScreen
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { _, dragAmount ->
                                        val dx = dragAmount.x
                                        val dy = dragAmount.y

                                        // 下滑唤醒启动台 TODO 后期做跟手
                                        if (dy > 0 && kotlin.math.abs(dy) > kotlin.math.abs(dx)) {
                                            downDrag += dy

                                            if (downDrag >= 300f) {
                                                downDrag = 0f
                                                inFullScreen = true
                                                navController.push(
                                                    destination = ControlCenterDestination,
                                                    effect = ControlCenterTransitionEffect(compositeOverColor = surfaceColor),
                                                    launchMode = LaunchMode.Push(keepPreviousAlive = true)
                                                )
                                            }
                                        }
                                        // 向右
                                        else if (dx > 0 && kotlin.math.abs(dx) > kotlin.math.abs(dy)) {
                                            downDrag = 0f
                                        }
                                    },
                                    onDragEnd = {
                                        downDrag = 0f
                                    },
                                    onDragCancel = {
                                        downDrag = 0f
                                    }
                                )
                            }
                    )
                }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    InnerPaddingHeight(innerPadding,true)

                    InnerPaddingHeight(innerPadding,false)
                }
            }
        }
    }
}