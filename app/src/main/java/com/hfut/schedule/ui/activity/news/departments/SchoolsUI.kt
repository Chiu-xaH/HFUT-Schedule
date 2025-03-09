package com.hfut.schedule.ui.activity.news.departments


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hfut.schedule.logic.utils.Starter
import com.hfut.schedule.ui.utils.components.AnimationCardListItem
import com.hfut.schedule.ui.utils.components.MyCustomCard
import com.hfut.schedule.ui.utils.components.ScrollText
import com.hfut.schedule.ui.utils.components.DepartmentIcons
import com.hfut.schedule.ui.utils.components.StyleCardListItem

@Composable
fun SchoolsUI(innerPdding : PaddingValues? = null) {
    val maps = mapOf(
        "http://jxxy.hfut.edu.cn/" to "机械工程学院",
        "http://mse.hfut.edu.cn/" to "材料科学与工程学院",
        "http://ea.hfut.edu.cn/" to "电气与自动化工程学院",
        "http://ci.hfut.edu.cn/" to "计算机与信息学院",
        "http://civil.hfut.edu.cn/" to "土木与水利工程学院",
        "http://hgxy.hfut.edu.cn/" to "化学与化工学院",
        "http://mks.hfut.edu.cn/" to "马克思主义学院",
        "http://jjxy.hfut.edu.cn/" to "经济学院",
        "http://wgyxy.hfut.edu.cn/" to "外国语学院",
        "http://wfxy.hfut.edu.cn/" to "文法学院",
        "http://som.hfut.edu.cn/" to "管理学院",
        "http://yqkx.hfut.edu.cn/" to "仪器科学与光电工程学院",
        "http://jyxy.hfut.edu.cn/" to "建筑与艺术学院",
        "http://geoscience.hfut.edu.cn/" to "资源与环境工程学院",
        "http://spysw.hfut.edu.cn/" to "食品与生物工程学院",
        "http://maths.hfut.edu.cn/" to "数学学院",
        "http://dwxy.hfut.edu.cn/" to "电子科学与应用物理学院",
        "http://wdzxy.hfut.edu.cn/" to "微电子学院",
        "http://jtxy.hfut.edu.cn/" to "汽车与交通工程学院",
        "http://rjxy.hfut.edu.cn/" to "软件学院",
        "http://tiyu.hfut.edu.cn/" to "体育部",
        "http://gpzx.hfut.edu.cn/" to "工培中心",
        "http://jsxy.hfut.edu.cn/" to "技师学院",
        "http://jxjy.hfut.edu.cn/" to "继续教育学院"
    )
    LazyColumn {
        item { if(innerPdding != null) Spacer(modifier = Modifier.height(innerPdding.calculateTopPadding())) }
        items(maps.entries.toList().size) { index ->
            val m = maps.entries.toList()[index]
            val title = m.value
            val url = m.key
//            MyCustomCard {
            AnimationCardListItem(
                    headlineContent = { ScrollText(text = title) },
                    leadingContent = { DepartmentIcons(title) },
                    overlineContent = { ScrollText(text = url) },
                    modifier = Modifier.clickable {
                        Starter.startWebUrl(url)
                    },
                    trailingContent = {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "")
                    },
                index = index
                )
//            }
        }
        item { if(innerPdding != null) Spacer(modifier = Modifier.height(innerPdding.calculateBottomPadding())) }
    }
}