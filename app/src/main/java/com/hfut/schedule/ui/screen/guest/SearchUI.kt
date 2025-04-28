package com.hfut.schedule.ui.screen.guest

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.sys.Starter.refreshLogin
import com.hfut.schedule.ui.screen.home.search.function.bus.SchoolBus
import com.hfut.schedule.ui.screen.home.search.function.dormitoryScore.DormitoryScoreXuanCheng
import com.hfut.schedule.ui.screen.home.search.function.hotWater.HotWater
import com.hfut.schedule.ui.screen.home.search.function.ietp.IETP
import com.hfut.schedule.ui.screen.home.search.function.sport.lepao.LePaoYun
import com.hfut.schedule.ui.screen.home.search.function.life.Life
import com.hfut.schedule.ui.screen.home.search.function.news.News
import com.hfut.schedule.ui.screen.home.search.function.notification.NotificationsCenter
import com.hfut.schedule.ui.screen.home.search.function.program.GuestProgram
import com.hfut.schedule.ui.screen.home.search.function.repair.Repair
import com.hfut.schedule.ui.screen.home.search.function.schoolCalendar.SchoolCalendar
import com.hfut.schedule.ui.screen.home.search.function.shower.Shower
import com.hfut.schedule.ui.screen.home.search.function.teacherSearch.TeacherSearch
import com.hfut.schedule.ui.screen.home.search.function.webLab.WebUI
import com.hfut.schedule.ui.screen.home.search.function.work.Work
import com.hfut.schedule.ui.screen.home.search.function.xueXin.XueXin
import com.hfut.schedule.ui.component.SmallCard
import com.hfut.schedule.ui.screen.home.search.function.alumni.Alumni
import com.hfut.schedule.ui.screen.home.search.function.holiday.Holiday
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.UIViewModel
import dev.chrisbanes.haze.HazeState

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun SearchScreenNoLogin(vm : NetWorkViewModel, ifSaved : Boolean, innerPaddings : PaddingValues, vmUI : UIViewModel, webVpn : Boolean, input : String, hazeState: HazeState) {


    @Composable
    fun CardItem(modifier: Modifier = Modifier.fillMaxSize(), content: @Composable () -> Unit) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 1.75.dp),
            modifier = modifier,
            shape = MaterialTheme.shapes.small,
        ) {
            content()
        }
    }
//    if(SharePrefs.prefs.getString("TOKEN","")?.contains("ey") == false) MyToast("未登录,部分功能不可用")
//
//    //建立索引 <搜索关键词,功能>
    val funcMaps : Map<String,@Composable () -> Unit>  = mapOf(
        "网址导航 实验室 收纳" to { WebUI(hazeState) },
        "报修 维修 后勤" to { Repair(hazeState) },
        "寝室卫生评分 寝室卫生分数" to { DormitoryScoreXuanCheng(vm,hazeState) },
        "消息中心 通知中心 收纳" to { NotificationsCenter(hazeState) },
        "通知公告 新闻" to { News(vm) },
        "培养方案" to { GuestProgram(vm, hazeState) },
        "校历" to { SchoolCalendar() },
        "校车" to { SchoolBus() },
        "洗浴 洗澡 呱呱物联 慧新易校 缴费" to { Shower(vm,hazeState = hazeState) },
        "乐跑云运动 校园跑 体测 体育测试 体育平台 体检" to { LePaoYun(hazeState) },
        "热水机 趣智校园" to { HotWater() },
        "学信网" to { XueXin() },
        "生活服务 校园 校园 天气 教学楼 建筑 学堂" to { Life(vm,hazeState) },
        "教师 老师" to { TeacherSearch(vm,hazeState) },
        "大创 大学生创新创业" to { IETP() },
        "就业 实习 春招 双选 秋招" to { Work(hazeState) },
        "校友 毕业" to { Alumni() },
        "国家法定节假日 假期 节日" to { Holiday(hazeState) },
    )

    val funcList = funcMaps.values.toList()

    var filteredList = funcList

    if(input != "") {
        filteredList = funcMaps.filterKeys { key ->
            key.contains(input, ignoreCase = true)
        }.values.toList()
    }


    Column {
        LazyVerticalGrid(columns = GridCells.Fixed(2),modifier = Modifier.padding(horizontal = 12.dp)) {
            items(2) {
                Column {
                    Spacer(modifier = Modifier.height(innerPaddings.calculateTopPadding()))
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            items(filteredList.size) { index->
                SmallCard(modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp)) {
                    filteredList[index]()
                }
            }
        }
        Login()
        Spacer(modifier = Modifier.height(innerPaddings.calculateBottomPadding()))
    }



}

@Composable
fun Login() {
//    MyCard {
        androidx.compose.material3.ListItem(
            headlineContent = { Text(text = "登录解锁查询中心30+功能及课程表、聚焦完全版") },
            trailingContent = {
                FilledTonalIconButton(onClick = { refreshLogin() }) {
                    Icon(painterResource(id = R.drawable.login), contentDescription = null)
                }
            },
            modifier = Modifier.clickable { refreshLogin() }
        )
//    }
}