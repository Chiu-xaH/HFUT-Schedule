package com.hfut.schedule.receiver.widget
//
//import android.content.Context
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.produceState
//import androidx.glance.GlanceId
//import androidx.glance.GlanceModifier
//import androidx.glance.GlanceTheme
//import androidx.glance.LocalContext
//import androidx.glance.appwidget.GlanceAppWidget
//import androidx.glance.appwidget.GlanceAppWidgetReceiver
//import androidx.glance.appwidget.lazy.LazyColumn
//import androidx.glance.appwidget.provideContent
//import androidx.glance.background
//import androidx.glance.layout.Alignment
//import androidx.glance.layout.Column
//import androidx.glance.layout.fillMaxSize
//import androidx.glance.text.Text
//import com.hfut.schedule.ui.screen.home.focus.funiction.getTomorrowJxglstuCourse
//
//class AppWidgetReceiver :  GlanceAppWidgetReceiver() {
//    override val glanceAppWidget: GlanceAppWidget = MyAppWidget()
//}
//
//@Composable
//fun WidgetTheme(
//    content: @Composable () -> Unit
//) {
//    GlanceTheme (
//        content = content
//    )
//}
//
//class MyAppWidget : GlanceAppWidget() {
//
//    override suspend fun provideGlance(context: Context, id: GlanceId) {
//        // Load data needed to render the AppWidget.
//        // Use `withContext` to switch to another thread for long running
//        // operations.
//
//        provideContent {
//            // create your AppWidget here
//            WidgetTheme {
//                MyContent()
//            }
//        }
//    }
//
//    @Composable
//    private fun MyContent() {
//        val context = LocalContext.current
//        val list by produceState(initialValue = emptyList()) {
//            value = getTomorrowJxglstuCourse(context)
//        }
//        Column(
//            modifier = GlanceModifier
//                .fillMaxSize()
//                .background(
//                  GlanceTheme.colors.surface,
//                ),
//            verticalAlignment = Alignment.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            LazyColumn {
//                items(list.size) { index ->
//                    val item = list[index]
//                    Text(item.courseName)
//                }
//            }
//        }
//    }
//}