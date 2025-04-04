package com.hfut.schedule.ui.activity.home.cube.items.subitems

//import androidx.compose.ui.tooling.preview.Preview
import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hfut.schedule.R
import com.hfut.schedule.logic.utils.VersionUtils
import com.hfut.schedule.logic.utils.data.SharePrefs.prefs
import com.hfut.schedule.ui.utils.components.BottomSheetTopBar
import com.hfut.schedule.ui.utils.components.SharedTopBar
import com.hfut.schedule.ui.utils.components.SmallCard
import com.hfut.schedule.ui.utils.components.TransplantListItem
import com.hfut.schedule.ui.utils.style.dialogBlur
import dev.chrisbanes.haze.HazeDialog
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import kotlin.math.abs

@Composable
fun TEST(innerPaddings : PaddingValues) {
    Column (modifier = Modifier.padding(innerPaddings)) {
        Spacer(Modifier.height(innerPaddings.calculateTopPadding()))
        ShareUI()
    }
}
enum class ShareBarRoutes {
    FIRST,SECOND
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
//@Preview
@Composable
fun ShareUI() {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = ShareBarRoutes.FIRST.name
        ) {
            composable(ShareBarRoutes.FIRST.name) {
                FirstUI(navController,this@SharedTransitionLayout,this@composable)
            }
            composable(ShareBarRoutes.SECOND.name) {
                SecondUI(navController,this@SharedTransitionLayout,this@composable)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FirstUI(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            BottomSheetTopBar("一级界面")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 11.dp)) {
                item {
                    SmallCard(modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "container"),
                        animatedVisibilityScope = animatedContentScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )) {
                        TransplantListItem(
                            headlineContent = {
                                Text(
                                    "二级界面",
                                    modifier = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "title"),
                                        animatedVisibilityScope = animatedContentScope
                                    )
                                )
                            },
                            leadingContent = {
                                Icon(painterResource(R.drawable.home), null)
                            },
                            modifier = Modifier.clickable { navController.navigate(ShareBarRoutes.SECOND.name) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun SharedTransitionScope.SecondUI(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope) {
    var showDialog by remember { mutableStateOf(false) }

    val hazeState = remember { HazeState() }
    if(showDialog) {
        HazeDialog(
            hazeState = hazeState,
            onDismissRequest = { showDialog = false },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = .5f),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Box(Modifier.dialogBlur(hazeState))
                {
                    TransplantListItem(
                        headlineContent = {
                            Text("示例")
                        }
                    )
                    // empty
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().sharedBounds(
            sharedContentState = rememberSharedContentState(key = "container"),
            animatedVisibilityScope = animatedContentScope,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        ).hazeSource(state = hazeState),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            SharedTopBar(
                "二级界面",
                key = "title",
                sharedTransitionScope,
                animatedContentScope,
            )
        },
    ) { innerPadding ->
        val context = LocalContext.current
//        val activity = context as Activity
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("打开Dialog")
            }
        }
    }
}


@Composable
fun AnimatedColorBackground(content : @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val color1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val color2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(1f, abs(color1 - 0.5f), abs(color2 - 0.5f)),
            Color(abs(color2 - 0.5f), 1f, abs(color1 - 0.5f))
        )
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        with(density) {
            val contentHeight = constraints.maxHeight.toDp()

            Box(modifier = Modifier.height(contentHeight).fillMaxWidth()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRect(brush = gradient)
                }
                content()
            }
        }
    }
}

//@Preview
@Composable
fun PreviewAnimatedColorBackground() {
    AnimatedColorBackground {
        TransplantListItem(
            headlineContent = {
                Text("Hello")
            }
        )
    }
}
