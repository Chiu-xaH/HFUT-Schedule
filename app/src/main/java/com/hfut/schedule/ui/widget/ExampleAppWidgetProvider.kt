package com.hfut.schedule.ui.widget
//
//import android.content.Context
//import androidx.compose.material3.ListItem
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.glance.GlanceId
//import androidx.glance.GlanceModifier
//import androidx.glance.GlanceTheme
//import androidx.glance.Image
//import androidx.glance.ImageProvider
//import androidx.glance.appwidget.GlanceAppWidget
//import androidx.glance.appwidget.GlanceAppWidgetReceiver
//import androidx.glance.appwidget.provideContent
//import androidx.glance.background
//import androidx.glance.layout.Alignment
//import androidx.glance.layout.Box
//import androidx.glance.layout.Column
//import androidx.glance.layout.Row
//import androidx.glance.layout.Spacer
//import androidx.glance.layout.fillMaxSize
//import androidx.glance.layout.padding
//import androidx.glance.layout.width
//import androidx.glance.text.Text
//import androidx.glance.text.TextStyle
//import com.hfut.schedule.R
//
//
//class MyAppWidgetReceiver : GlanceAppWidgetReceiver() {
//    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
//}
//
//class MyAppWidget : GlanceAppWidget() {
//
//    override suspend fun provideGlance(context: Context, id: GlanceId) {
//
//        // In this method, load data needed to render the AppWidget.
//        // Use `withContext` to switch to another thread for long running
//        // operations.
//
//        provideContent {
//            // create your AppWidget here
//            GlanceTheme {
//                MyContent()
//            }
//        }
//    }
//
//    @Composable
//    private fun MyContent() {
//        Column (
//            modifier = GlanceModifier.fillMaxSize()
//                .background(GlanceTheme.colors.secondaryContainer),
//        ) {
//            Box(contentAlignment = Alignment.TopStart, modifier = GlanceModifier.padding(AppHorizontalDp())) {
//                //分布在左上角
//                Row() {
//                    Image(
//                        provider = ImageProvider(R.drawable.credit_card_black),
//                        contentDescription = "Image",
//                        modifier = GlanceModifier
//                    )
//                    Spacer(modifier = GlanceModifier.width(5.dp))
//                    Text(
//                        text = "￥100.0",
//                        maxLines = 1,
//                    )
//                }
//            }
//
//            Box(contentAlignment = Alignment.TopEnd, modifier = GlanceModifier.padding(AppHorizontalDp())) {
//                //分布在左上角
//                Row() {
//                    Image(
//                        provider = ImageProvider(R.drawable.water_voc_shortcut),
//                        contentDescription = "Image",
//                        modifier = GlanceModifier
//                    )
//                    Spacer(modifier = GlanceModifier.width(5.dp))
//                    Text(
//                        text = "￥100.0",
//                        maxLines = 1,
//                    )
//                }
//            }
//
//        }
//    }
//}