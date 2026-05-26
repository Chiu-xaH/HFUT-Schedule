package com.hfut.schedule.ui.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

@Composable
fun DepartmentIcons(name : String, modifier: Modifier = Modifier) = Icon(painterResource(departmentIcon(name)), null,modifier)

/**
 * 学院图标重绘
 * @param name 学院名称
 */
fun departmentIcon(name : String) : Int =
    // 学院
    if(name.contains("计算机与信息学院")) R.drawable.data_object
    else if(name.contains("资源与环境工程学院")) R.drawable.eco
    else if(name.contains("数学学院")) R.drawable.function
    else if(name.contains("外国语")) R.drawable.translate
    else if(name.contains("马克思主义学院")) R.drawable.cognition
    else if(name.contains("机械工程学院")) R.drawable.settings
    else if(name.contains("材料科学与工程学院")) R.drawable.texture
    else if(name.contains("电气与自动化工程学院")) R.drawable.flash_on
    else if(name.contains("土木与水利工程学院")) R.drawable.precision_manufacturing
    else if(name.contains("化学与化工学院")) R.drawable.science
    else if(name.contains("经济学院")) R.drawable.currency_pound
    else if(name.contains("文法学院")) R.drawable.newsstand
    else if(name.contains("管理学院")) R.drawable.account_tree
    else if(name.contains("仪器科学与光电工程学院")) R.drawable.body_fat
    else if(name.contains("建筑与艺术学院")) R.drawable.domain
    else if(name.contains("食品与生物工程学院")) R.drawable.genetics
    else if(name.contains("微电子学院")) R.drawable.empty_dashboard
    else if(name.contains("物理学院")) R.drawable.category
    else if(name.contains("汽车与交通工程学院")) R.drawable.directions_car
    else if(name.contains("软件学院")) R.drawable.code
    else if(name.contains("聚变科学与工程学院")) R.drawable.orbit
    else if(name.contains("卓越工程师学院")) R.drawable.manufacturing
    else if(name.contains("创新学院")) R.drawable.stylus_laser_pointer // 集成电路创新学院 人工智能创新学院 未来技术创新学院
    else if(name.contains("体育部")) R.drawable.directions_run
    // 其余机构
    else if(name.contains("校医院")) R.drawable.emergency
    else if(name.contains("工程素质教育中心")) R.drawable.massage
    else if(name.contains("创新创业教育处")) R.drawable.stylus_laser_pointer
    // 为二课适配的图标
    else if(name.contains("教务处") || name.contains("研究生")) R.drawable.school
    else if(name.contains("党委学生工作部") || name.contains("团委")) R.drawable.person_play
    else if(name.contains("招生与就业处")) R.drawable.azm
    else if(name.contains("校领导")) R.drawable.person
    // 未知图标
    else R.drawable.crossword

@Composable
fun ScheduleIcons(title : String) {
    if (title.contains("实验"))
        Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if (title.contains("上机"))
        Icon(painter = painterResource(id = R.drawable.data_object), contentDescription = "")
    else if (title.contains("实习"))
        Icon(painter = painterResource(id = R.drawable.massage), contentDescription = "")
    else
        Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
}

@Composable
fun BillsIcons(name : String) {
    when {
        name.contains("淋浴") ->  Icon(painterResource(R.drawable.bathtub), contentDescription = "")
        name.contains("网") -> Icon(painterResource(R.drawable.net), contentDescription = "")
        name.contains("餐饮") -> Icon(painterResource(R.drawable.restaurant), contentDescription = "")
        name.contains("电") -> Icon(painterResource(R.drawable.flash_on), contentDescription = "")
        name.contains("超市") || name.contains("贸易") || name.contains("商店") -> Icon(painterResource(R.drawable.storefront), contentDescription = "",)
        name.contains("打印") -> Icon(painterResource(R.drawable.print), contentDescription = "",)
        name.contains("充值") -> Icon(painterResource(R.drawable.add_card), contentDescription = "",)
        name.contains("补助") -> Icon(painterResource(R.drawable.volunteer_activism), contentDescription = "",)
        name.contains("医院") -> Icon(painterResource(R.drawable.emergency), contentDescription = "",)
        name.contains("呱呱") -> Icon(painterResource(R.drawable.bathtub), contentDescription = "")
        else ->  Icon(painterResource(R.drawable.paid), contentDescription = "")
    }
}
