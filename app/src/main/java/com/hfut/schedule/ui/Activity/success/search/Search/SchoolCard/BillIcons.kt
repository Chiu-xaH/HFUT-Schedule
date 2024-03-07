package com.hfut.schedule.ui.Activity.success.search.Search.SchoolCard

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R

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