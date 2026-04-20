package com.hfut.schedule.ui.screen.home.search.function.other

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfut.schedule.R
import com.hfut.schedule.logic.model.GithubIssueBean
import com.hfut.schedule.logic.model.GithubIssueLabel
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.component.button.LargeButton
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.network.DEFAULT_IMAGE_SIZE
import com.hfut.schedule.ui.component.network.UrlImage
import com.hfut.schedule.ui.component.screen.pager.PaddingForPageControllerButton
import com.hfut.schedule.ui.component.screen.pager.PageController
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.TrackDestination

import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.component.text.ScrollText
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.navigation.util.LocalNavController
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@Composable
fun Track() {
    val navController = LocalNavController.current

    TransplantListItem(
        headlineContent = { ScrollText(text = TrackDestination.title.asString()) },
        leadingContent = {
            Icon(painterResource(TrackDestination.icon), contentDescription = null)
        },
        modifier = Modifier.clickable {
            navController.push(TrackDestination)
        }
    )
}


@Composable
private fun Buttons() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
        LargeButton(
            modifier = Modifier.fillMaxWidth().weight(.5f),
            text = "新建事务",
            icon = R.drawable.github,
            containerColor = Color.Black,
            contentColor = Color.White,
            onClick = {
                scope.launch {
                    Starter.startWebUrl(
                        context,
                        Constant.GITHUB_REPO_URL + "/issues",
                    )
                }
            }
        )
        Spacer(Modifier.width(APP_HORIZONTAL_DP/2))
        LargeButton(
            modifier = Modifier.fillMaxWidth().weight(.5f),
            text = "提出事务",
            icon = R.drawable.mail,
            onClick = {
                Starter.emailMe(context)
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    vm : NetWorkViewModel
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val backdrop = rememberLayerBackdrop()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var page by remember { mutableIntStateOf(1) }
    val refreshNetwork : suspend () -> Unit =  {
        vm.githubIssuesResp.clear()
        vm.getIssues(page)
    }
    val uiState by vm.githubIssuesResp.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(page) {
        refreshNetwork()
    }

    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column(
                modifier = Modifier.topBarBlur(hazeState),
            ) {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text(TrackDestination.title.asString()) },
                    navigationIcon = {
                        TopBarNavigationIcon()
                    },
                )
                if(uiState is UiState.Error) {
                    Buttons()
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .backDropSource(backdrop)
                .hazeSource(hazeState)
                .fillMaxSize()
        ) {
            CommonNetworkScreen(uiState, onReload = refreshNetwork) {
                val list = (uiState as UiState.Success).data
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(state = listState) {
                        item { InnerPaddingHeight(innerPadding,true) }
                        items(list.size, key = { list[it].number }) { index ->
                            val item = list[index]
                            val tag = "#${item.number}"
                            val isClosed = !item.getStateOpen()
                            val textDecoration = if(isClosed) TextDecoration.LineThrough else TextDecoration.None
                            CustomCard(
                                color = cardNormalColor(),
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        Starter.startWebView(context,item.url,tag)
                                    }
                                },
                            ) {
                                TransplantListItem(
                                    headlineContent = {
                                        Text(item.title,textDecoration = textDecoration)
                                    },
                                    leadingContent = {
                                        UrlImage(
                                            url = item.user.photoUrl,
                                            modifier = Modifier.size(36.dp),
                                            shape = CircleShape
                                        )
                                    },
                                    overlineContent = {
                                        Text("创建于 ${item.createTime}",textDecoration = textDecoration)
                                    },
                                )
                                IssueFlowChart(item)
                            }
                        }
                        item {
                            Box(modifier = Modifier.padding(vertical = APP_HORIZONTAL_DP/2)) {
                                Buttons()
                            }
                        }
                        item {
                            BottomTip("事务推进有快有慢为正常现象，以开发者的闲暇情况以及影响用户体验的严重性为准")
                        }
                        item { PaddingForPageControllerButton() }
                        item { InnerPaddingHeight(innerPadding,false) }
                    }
                    PageController(listState,page, onNextPage = { page = it }, onPreviousPage = { page = it })
                }
            }
        }
    }
}


// ── 颜色 ─────────────────────────────────────────────
private val ColorInactive   = Color(0xFFE5E7EB)
private val ColorInactiveFg = Color(0xFF9CA3AF)
private val ColorNew        = Color(0xFF6B7280)  // 灰
private val ColorEval       = Color(0xFF7C3AED)  // 紫
private val ColorPass       = Color(0xFF0EA5E9)  // 青
private val ColorDiscard    = Color(0xFF0EA5E9)  // 红
private val ColorDesign     = Color(0xFFEC4899)  // 粉
private val ColorDev        = Color(0xFF1D4ED8)  // 蓝
private val ColorVerity     = Color(0xFFF97316)  // 橙
private val ColorResolved   = Color(0xFF059669)  // 绿
private val ConnectorColor  = Color(0xFFD1D5DB)

// ── 数据结构 ──────────────────────────────────────────
private data class CircleNode(
    val label: String,
    val activeColor: Color,
    val active: Boolean,
)

// ── 主入口 ────────────────────────────────────────────
@Composable
private fun IssueFlowChart(issue: GithubIssueBean) {
    val ids = issue.labels.map { it.id }.toSet()
    fun has(l: GithubIssueLabel) = l.id in ids

    val isOpen     = issue.getStateOpen()
    val isJoin     = has(GithubIssueLabel.JOIN_PLAN)
    val isDiscard  = has(GithubIssueLabel.DISCARD)
    val isDesign   = has(GithubIssueLabel.IN_DESIGN)
    val isDev      = has(GithubIssueLabel.IN_DEV)
    val isVerity   = has(GithubIssueLabel.VERITY)
    val isResolved = has(GithubIssueLabel.RESOLVED)
    val isEval     = isJoin || isDiscard || isDesign || isDev || isVerity || isResolved

    val mainNodes = listOf(
        CircleNode("提出",    ColorNew,      isOpen || isEval),
        CircleNode("评估",    ColorEval,     isEval),
        CircleNode("纳入计划", ColorPass,    isJoin || isDesign || isDev || isVerity || isResolved),
        CircleNode("设计",    ColorDesign,   isDesign || isDev || isVerity || isResolved),
        CircleNode("开发",    ColorDev,      isDev || isVerity || isResolved),
        CircleNode("测试",    ColorVerity,   isVerity || isResolved),
        CircleNode("发布",    ColorResolved, isResolved),
    )
    val discardNode = CircleNode("暂不采纳", ColorDiscard, isDiscard)

    val nodeCount  = mainNodes.size   // 7
    val labelGap   = 4.dp
    val branchGap  = 12.dp
    val charH      = 13.dp

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // ── 动态计算尺寸 ──────────────────────────────
        // totalWidth = diameter * 7 + hSpacing * 6
        // 令 hSpacing = diameter * 0.4（间距是直径的40%），解方程：
        // availableWidth = d * 7 + d * 0.4 * 6 = d * 9.4
        val availableWidth = maxWidth - 32.dp   // 留左右各16dp边距
        val diameter  = availableWidth / (nodeCount + (nodeCount - 1) * 0.4f)
        val radius    = diameter / 2
        val hSpacing  = diameter * 0.4f
        val totalWidth = availableWidth

        val maxMainChars  = mainNodes.maxOf { it.label.length }
        val mainLabelH    = charH * maxMainChars
        val discardLabelH = charH   // 横排只占一行高度
        val totalHeight   = mainLabelH + labelGap + diameter +
                branchGap + discardLabelH + labelGap + diameter

        Canvas(
            modifier = Modifier.width(totalWidth).height(totalHeight)
        ) {
            val r    = radius.toPx()
            val step = diameter.toPx() + hSpacing.toPx()
            val lH   = mainLabelH.toPx()
            val lG   = labelGap.toPx()
            val bG   = branchGap.toPx()
            val dlH  = discardLabelH.toPx()

            val mainCY   = lH + lG + r
            val branchCY = mainCY + r + bG + dlH + lG + r

            fun cx(i: Int) = r + i * step

            for (i in 0 until mainNodes.size - 1) {
                if (i == 1) continue
                drawArrowLine(Offset(cx(i) + r, mainCY), Offset(cx(i + 1) - r, mainCY), ConnectorColor)
            }
            drawArrowLine(Offset(cx(1) + r, mainCY), Offset(cx(2) - r, mainCY), ConnectorColor)
            drawForkLine(
                forkX = cx(1) + r * 0.4f,
                fromY = mainCY + r,
                toY   = branchCY,
                toX   = cx(2) - r,
                color = ConnectorColor
            )
        }

        Column(
            modifier = Modifier.width(totalWidth),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(hSpacing),
                verticalAlignment = Alignment.Bottom
            ) {
                mainNodes.forEach { CircleNodeItem(it, radius, labelGap) }
            }

            Spacer(Modifier.height(branchGap))

            Row(
                horizontalArrangement = Arrangement.spacedBy(hSpacing),
                verticalAlignment = Alignment.Bottom
            ) {
                repeat(2) { Spacer(Modifier.width(diameter)) }
                CircleNodeItem(discardNode, radius, labelGap, vertical = false)
            }
        }
    }
}

// ── 单圆节点（圆 + 上方文字） ─────────────────────────
@Composable
private fun CircleNodeItem(
    node: CircleNode,
    radius: Dp,
    labelGap: Dp,
    vertical: Boolean = true,   // true=竖排，false=横排
) {
    val circleColor by animateColorAsState(
        if (node.active) node.activeColor else ColorInactive,
        tween(400), label = "circle"
    )
    val textColor by animateColorAsState(
        if (node.active) node.activeColor else ColorInactiveFg,
        tween(400), label = "text"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(radius * 2)
    ) {
        if (vertical) {
            // 竖排：逐字输出
            node.label.forEach { char ->
                Text(
                    text = char.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                )
            }
        } else {
            // 横排：单行，允许文字超出圆宽度自然居中
            Text(
                text = node.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
            )
        }

        Spacer(Modifier.height(labelGap))

        Canvas(modifier = Modifier.size(radius * 2)) {
            drawCircle(color = circleColor, radius = radius.toPx())
        }
    }
}

// ── 工具函数：带箭头的水平线 ──────────────────────────
private fun DrawScope.drawArrowLine(
    from: Offset,
    to: Offset,
    color: Color,
) {
    if (to.x - from.x < 4f) return
    drawLine(color, from, to, strokeWidth = 1.5f)
    // 箭头
    val arrowSize = 6f
    val path = Path().apply {
        moveTo(to.x, to.y)
        lineTo(to.x - arrowSize, to.y - arrowSize * 0.5f)
        lineTo(to.x - arrowSize, to.y + arrowSize * 0.5f)
        close()
    }
    drawPath(path, color)
}

// ── 工具函数：L 形分叉线（垂直 → 水平 带箭头）────────
private fun DrawScope.drawForkLine(
    forkX: Float,
    fromY: Float,
    toY: Float,
    toX: Float,
    color: Color,
) {
    // 垂直段
    drawLine(color, Offset(forkX, fromY), Offset(forkX, toY), strokeWidth = 1.5f)
    // 水平段
    drawLine(color, Offset(forkX, toY), Offset(toX, toY), strokeWidth = 1.5f)
    // 箭头
    val arrowSize = 6f
    val path = Path().apply {
        moveTo(toX, toY)
        lineTo(toX - arrowSize, toY - arrowSize * 0.5f)
        lineTo(toX - arrowSize, toY + arrowSize * 0.5f)
        close()
    }
    drawPath(path, color)
}