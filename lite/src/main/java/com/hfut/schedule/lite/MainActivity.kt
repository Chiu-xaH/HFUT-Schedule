package com.hfut.schedule.lite

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.hfut.schedule.lite.ui.theme.肥工课程表Theme
import com.xah.shared.IJxglstuAidlInterface
import com.xah.shared.JxglstuCourseGroup
import androidx.core.net.toUri
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private var remoteService: IJxglstuAidlInterface? = null
    var result: List<JxglstuCourseGroup>? by mutableStateOf(null)

    companion object {
        private const val SERVER_PACKAGE_NAME = "com.hfut.schedule"
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            remoteService = IJxglstuAidlInterface.Stub.asInterface(binder)
            result = remoteService?.jxglstuCourseGroups
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            remoteService = null
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(IJxglstuAidlInterface::class.java.name).apply {
            setPackage(SERVER_PACKAGE_NAME)
        }
        val bound = bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        Log.d("结果", "Bind result: $bound")

        enableEdgeToEdge()
        setContent {
            肥工课程表Theme {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn {
                        item { Spacer(Modifier.statusBarsPadding()) }
                        result?.let { list ->
                            items(list.size, key = { list[it].date }) { index ->
                                val item = list[index]
                                Column {
                                    for(course in item.courses) {
                                        ListItem(
                                            headlineContent = { Text(course.courseName) },
                                            supportingContent = { course.place?.let { Text(it) } },
                                            overlineContent = { Text(
                                                with(course.dateTime) {
                                                    "$first~$second"
                                                })
                                            },
                                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                        item { Spacer(Modifier.navigationBarsPadding()) }
                    }
                }
            }
            // 两个界面 课程表方格（上面课程汇总） 聚焦（两个横划界面）（今明课程 考试）
            // 设置 主页 课程详情
            // 设置（更新）
        }
    }
}
