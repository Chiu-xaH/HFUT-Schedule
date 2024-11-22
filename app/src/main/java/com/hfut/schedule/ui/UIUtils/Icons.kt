package com.hfut.schedule.ui.UIUtils

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.ui.Activity.success.cube.Settings.Items.Birthday

@Composable
fun schoolIcons(name : String) {
    if(name.contains("计算机")) Icon(painterResource(R.drawable.data_object), contentDescription = "Localized description",)
    else if(name.contains("资源与环境工程")) Icon(painterResource(R.drawable.eco), contentDescription = "Localized description",)
    else if(name.contains("数学")) Icon(painterResource(R.drawable.function), contentDescription = "Localized description",)
    else if(name.contains("外国语")) Icon(painterResource(R.drawable.translate), contentDescription = "Localized description",)
    else if(name.contains("马克思主义")) Icon(painterResource(R.drawable.neurology), contentDescription = "Localized description",)
    else if(name.contains("机械工程")) Icon(painterResource(R.drawable.settings), contentDescription = "Localized description",)
    else if(name.contains("材料科学")) Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if(name.contains("电气与自动化工程")) Icon(painterResource(R.drawable.flash_on), contentDescription = "Localized description",)
    else if(name.contains("土木与水利工程")) Icon(painterResource(R.drawable.precision_manufacturing), contentDescription = "Localized description",)
    else if(name.contains("化学与化工")) Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if(name.contains("经济")) Icon(painterResource(R.drawable.paid), contentDescription = "Localized description",)
    else if(name.contains("文法")) Icon(painterResource(R.drawable.newsstand), contentDescription = "Localized description",)
    else if(name.contains("管理")) Icon(painterResource(R.drawable.pie_chart), contentDescription = "Localized description",)
    else if(name.contains("仪器科学与光电工程")) Icon(painterResource(R.drawable.body_fat), contentDescription = "Localized description",)
    else if(name.contains("建筑与艺术")) Icon(painterResource(R.drawable.domain), contentDescription = "Localized description",)
    else if(name.contains("食品与生物工程")) Icon(painterResource(R.drawable.genetics), contentDescription = "Localized description",)
    else if(name.contains("微电子")) Icon(painterResource(R.drawable.empty_dashboard), contentDescription = "Localized description",)
    else if(name.contains("物理")) Icon(painterResource(R.drawable.category), contentDescription = "Localized description",)
    else if(name.contains("汽车与交通工程")) Icon(painterResource(R.drawable.directions_car), contentDescription = "Localized description",)
    else if(name.contains("软件")) Icon(painterResource(R.drawable.code), contentDescription = "Localized description",)
    else if(name.contains("体育部")) Icon(painterResource(R.drawable.directions_run), contentDescription = "Localized description",)
    else if(name.contains("国际教育")) Icon(painterResource(R.drawable.publics), contentDescription = "Localized description",)
    else Icon(painterResource(R.drawable.calendar_view_month), contentDescription = "Localized description",)
}

@Composable
fun ScheduleIcons(title : String) {
    if (title.contains("实验"))
        Icon(painterResource(R.drawable.science), contentDescription = "Localized description",)
    else if (title.contains("上机"))
        Icon(painter = painterResource(id = R.drawable.devices), contentDescription = "")
    else if (title.contains("实习"))
        Icon(painter = painterResource(id = R.drawable.nature_people), contentDescription = "")
    else
        Icon(painterResource(R.drawable.calendar), contentDescription = "Localized description",)
}

@Composable
fun APIIcons(celebration: Boolean) {
    when {
        celebration -> Icon(painterResource(R.drawable.celebration), contentDescription = "Localized description",)
        Birthday().contains("生日") ->  Icon(painterResource(R.drawable.cake), contentDescription = "Localized description",)
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
        else ->  Icon(painterResource(R.drawable.paid), contentDescription = "")
    }
}