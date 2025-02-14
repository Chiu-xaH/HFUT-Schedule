package com.hfut.schedule.ui.activity.home.cube.items.subitems


import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.RemeasureToBounds
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R
import com.hfut.schedule.ui.activity.card.counts.RadarChart
import com.hfut.schedule.ui.activity.card.counts.RadarData
import com.hfut.schedule.ui.utils.style.CardForListColor
import com.hfut.schedule.ui.utils.style.RowHorizontal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TEST(innerPaddings : PaddingValues) {
//    Column (modifier = Modifier.padding(innerPaddings)){
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//
//
//        var showBottomSheet by remember { mutableStateOf(false) }
//        val sheetState = rememberModalBottomSheetState(
//            skipPartiallyExpanded = false,
//        )
//
//        LaunchedEffect(sheetState.currentValue) {
//            when (sheetState.currentValue) {
//                SheetValue.Expanded -> {
//                    // ModalBottomSheet 全屏展开时的处理逻辑
//                    println("Expanded")
//                }
//                SheetValue.PartiallyExpanded -> {
//                    // ModalBottomSheet 半屏展开时的处理逻辑
//                    println("Partially Expanded")
//                }
//                SheetValue.Hidden -> {
//                    // ModalBottomSheet 隐藏时的处理逻辑
//                    println("Hidden")
//                }
//            }
//        }
//        val dpAnimation by animateDpAsState(
//            targetValue = if (sheetState.currentValue != SheetValue.Expanded) 28.dp else 0.dp, label = ""
//            ,animationSpec = tween(MyApplication.Animation / 2, easing = LinearOutSlowInEasing),
//        )
//        //Log.d("ss",BottomSheetDefaults.ExpandedShape.toString())
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(
//                onClick = { showBottomSheet = true }
//            ) {
//                Text("打开底栏")
//            }
//
//            if (showBottomSheet) {
//                ModalBottomSheet(
//                    modifier = Modifier.fillMaxHeight(),
//                    sheetState = sheetState,
//                    onDismissRequest = { showBottomSheet = false },
//                    shape =  RoundedCornerShape(dpAnimation)
//                ) {
//                    Text(
//                        "Swipe up to open sheet. Swipe down to dismiss.",
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//            }
//        }
//
//
//
//        Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
//
//        var showDetails by remember { mutableStateOf(false) }
//        SharedTransitionLayout {
//            AnimatedContent(targetState = showDetails, label = "") { inDetails->
//              //  if(showDetails) {
//                    if(!inDetails) {
//                        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
//                            Card(
//                                elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
//                                modifier = Modifier
//                                    .width(200.dp)
//                                    .padding(horizontal = 15.dp, vertical = 5.dp)
//                                    .sharedBounds(
//                                        sharedContentState = rememberSharedContentState(key = "bounds"),
//                                        animatedVisibilityScope = this@AnimatedContent,
//                                        resizeMode = RemeasureToBounds
//                                    )
//                                ,
//                                shape = MaterialTheme.shapes.small,
//                                colors = CardForListColor()
//                            ) {
//                                ListItem(
//                                    headlineContent = { Text(text = "展开卡片") },
//                                    modifier = Modifier.clickable { showDetails = true }
//                                )
//                            }
//                        }
//                    } else  {
//                        //  Dialog(onDismissRequest = { showDetails = false }) {
//                        Card(
//                            elevation = CardDefaults.cardElevation(defaultElevation = 15.dp),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 15.dp, vertical = 5.dp)
//                                .sharedBounds(
//                                    sharedContentState = rememberSharedContentState(key = "bounds"),
//                                    animatedVisibilityScope = this@AnimatedContent,
//                                    resizeMode = RemeasureToBounds
//                                )
//                            ,
//                            shape = MaterialTheme.shapes.medium,
//                            colors = CardForListColor()
//                        ) {
//                            ListItem(
//                                headlineContent = { Text(text = "标题1") },
//                                modifier = Modifier.clickable {showDetails = false}
//                            )
//                            ListItem(
//                                headlineContent = { Text(text = "标题2") },
//                                modifier = Modifier.clickable {}
//                            )
//                            ListItem(
//                                headlineContent = { Text(text = "标题3") },
//                                modifier = Modifier.clickable {}
//                            )
//                        }
//                        //   }
//                    }
//               // }
//            }
//        }
//
//     // //  AnimatedVectorDrawable()
//      //  AnimatedVectorDrawable()
//       // AnimatedVectorDrawable()
//
//      //  Gesture()
//    }


    val party = Party(
        emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30)
    )

    Box() {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(party),
        )
        val listSnacks = listOf<Snack>(
            Snack("桌面","des",R.drawable.home),
            Snack("搜索","des",R.drawable.search),
            Snack("动画","des",R.drawable.animation),
            Snack("堆栈","des",R.drawable.stacks)
        )

        SharedTransitionLayout {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        navController,
                        this@SharedTransitionLayout,
                        this@composable,
                        listSnacks,
                        innerPaddings
                    )
                }
                composable(
                    "details/{item}",
                    arguments = listOf(navArgument("item") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("item")
                    val snack = listSnacks[id!!]
                    DetailsScreen(
                        navController,
                        id,
                        snack,
                        this@SharedTransitionLayout,
                        this@composable
                    )
                }
            }
        }
    }

}

@Composable
@Preview
fun t() {
    val partyTopStart = Party(
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(0.0,0.0)
    )
    val partyTopEnd = Party(
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(1.0,0.0)
    )
    val partyBottomStart = Party(
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(0.0,1.0)
    )
    val partyBottomEnd = Party(
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(1.0,1.0)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(partyTopStart,partyTopEnd,partyBottomStart,partyBottomEnd),
        )
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun Gesture() {
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                coroutineScope {
                    while (true) {
                        // Detect a tap event and obtain its position.
                        awaitPointerEventScope {
                            val position = awaitFirstDown().position

                            launch {
                                // Animate to the tap position.
                                offset.animateTo(position)
                            }
                        }
                    }
                }
            }
    ) {
        val image = AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
        var atEnd by remember { mutableStateOf(false) }
        Image(
            painter = rememberAnimatedVectorPainter(image, atEnd),
            contentDescription = "Timer",
            modifier = Modifier
                .clickable {
                    atEnd = !atEnd
                }
                .offset { offset.value.toIntOffset() },
            contentScale = ContentScale.Crop
        )
       // Icon(modifier = Modifier },painter = painterResource(id = R.drawable.deployed_code), contentDescription = "")
    }
}

@Composable
private fun AnimationSpecTween(enabled: Boolean) {
    // [START android_compose_animations_spec_tween]
    val alpha: Float by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        // Configure the animation duration and easing.
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    )
    // [END android_compose_animations_spec_tween]
}

@Composable
fun AnimatedVectorDrawable() {

    var big by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (big) 300.dp else 50.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow, 0.1.dp) ,
        label = ""
    )
    val size2 by animateDpAsState(
        targetValue = if (big) 225.dp else 37.dp,
        animationSpec = spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow, 0.1.dp),
        label = ""
    )
    FilledTonalIconButton(
        modifier = Modifier
            .size(size)
           // .background(Color.Blue)
            , onClick = {big = !big}
    ) {
        Icon(painter = painterResource(id = R.drawable.animation), contentDescription = "", modifier = Modifier.size(size2))
    }

}
private fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    val rotation by remember { mutableStateOf(0f) }
    val offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale *= zoomChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(Color.Blue)
            .fillMaxSize()
            .size(50.dp)
    )
}



@Composable
@Preview
fun RadarChartPreview() {
    val data = listOf(
        RadarData("Speed", 0.8f),
        RadarData("Accuracy", 0.6f),
        RadarData("Power", 0.7f),
        RadarData("Endurance", 0.9f),
        RadarData("Agility", 0.5f)
    )
    RadarChart(data = data, modifier = Modifier.size(300.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet() {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = { showBottomSheet = true }
        ) {
            Text("Display partial bottom sheet")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false }
            ) {
                Text(
                    "Swipe up to open sheet. Swipe down to dismiss.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}



@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun SharedElement_PredictiveBack() {

    val listSnacks = listOf<Snack>(
        Snack("桌面","des",R.drawable.home),
        Snack("搜索","des",R.drawable.search),
        Snack("动画","des",R.drawable.animation),
        Snack("堆栈","des",R.drawable.stacks)
    )

    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home") {
                HomeScreen(
                    navController,
                    this@SharedTransitionLayout,
                    this@composable,
                    listSnacks,
                    null
                )
            }
            composable(
                "details/{item}",
                arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item")
                val snack = listSnacks[id!!]
                DetailsScreen(
                    navController,
                    id,
                    snack,
                    this@SharedTransitionLayout,
                    this@composable
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailsScreen(
    navController: NavHostController,
    id: Int,
    snack: Snack,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    innerPaddings: PaddingValues? = null
) {
    with(sharedTransitionScope) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable {
                    navController.navigate("home")
                }
        ) {
            innerPaddings?.let { Spacer(modifier = Modifier.height(it.calculateTopPadding())) }
            Image(
                painterResource(id = snack.image),
                contentDescription = snack.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-$id"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
            RowHorizontal {
                Text(
                    snack.name, fontSize = 18.sp,
                    modifier =
                    Modifier
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "text-$id"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun HomeScreen(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    listSnacks : List<Snack>,
    innerPaddings: PaddingValues?
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { innerPaddings?.let { Spacer(modifier = Modifier.height(it.calculateTopPadding())) } }
        itemsIndexed(listSnacks) { index, item ->
            Row(
                Modifier.clickable {
                    navController.navigate("details/$index")
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                with(sharedTransitionScope) {
                    Image(
                        painterResource(id = item.image),
                        contentDescription = item.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "image-$index"),
                                animatedVisibilityScope = animatedContentScope
                            )
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.name, fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "text-$index"),
                                animatedVisibilityScope = animatedContentScope,
                            )
                    )
                }
            }
        }
    }
}

data class Snack(
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)
