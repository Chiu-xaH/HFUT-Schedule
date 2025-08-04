package com.hfut.schedule.ui.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

@Composable
fun DepartmentIcons(name : String, modifier: Modifier = Modifier) = Icon(painterResource(departmentIcon(name)), null,modifier)

fun departmentIcon(name : String) : Int =
    if(name.contains("计算机")) R.drawable.data_object
    else if(name.contains("资源与环境工程")) R.drawable.eco
    else if(name.contains("数学")) R.drawable.function
    else if(name.contains("外国语")) R.drawable.translate
    else if(name.contains("马克思主义")) R.drawable.neurology
    else if(name.contains("机械工程")) R.drawable.settings
    else if(name.contains("材料科学")) R.drawable.texture
    else if(name.contains("电气与自动化工程")) R.drawable.flash_on
    else if(name.contains("土木与水利工程")) R.drawable.precision_manufacturing
    else if(name.contains("化学与化工")) R.drawable.science
    else if(name.contains("经济")) R.drawable.paid
    else if(name.contains("文法")) R.drawable.newsstand
    else if(name.contains("管理")) R.drawable.pie_chart
    else if(name.contains("仪器科学与光电工程")) R.drawable.body_fat
    else if(name.contains("建筑与艺术")) R.drawable.domain
    else if(name.contains("食品与生物工程")) R.drawable.genetics
    else if(name.contains("微电子")) R.drawable.empty_dashboard
    else if(name.contains("物理")) R.drawable.category
    else if(name.contains("汽车与交通工程")) R.drawable.directions_car
    else if(name.contains("软件")) R.drawable.code
    else if(name.contains("体育部")) R.drawable.directions_run
    else if(name.contains("国际教育")) R.drawable.publics
    else R.drawable.calendar_view_month

@Composable
fun ScheduleIcons(title : String) {
    if (title.contains("实验"))
        Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if (title.contains("上机"))
        Icon(painter = painterResource(id = R.drawable.data_object), contentDescription = "")
    else if (title.contains("实习"))
        Icon(painter = painterResource(id = R.drawable.nature_people), contentDescription = "")
    else
        Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
}

@Composable
fun APIIcons(celebration: Boolean) {
    when {
        celebration -> Icon(painterResource(R.drawable.celebration), contentDescription = "Localized description",)
        else -> Icon(painterResource(R.drawable.error), contentDescription = "Localized description",)
    }
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
        name.contains("补助") -> Icon(painterResource(R.drawable.payments), contentDescription = "",)
        name.contains("医院") -> Icon(painterResource(R.drawable.emergency), contentDescription = "",)
        name.contains("呱呱") -> Icon(painterResource(R.drawable.bathtub), contentDescription = "")
        else ->  Icon(painterResource(R.drawable.paid), contentDescription = "")
    }
}
