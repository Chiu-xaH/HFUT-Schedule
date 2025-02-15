package com.hfut.schedule.ui.activity.home.search.functions.dormitoryScore

import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.XuanquResponse

fun getDormitoryScore(vm: NetWorkViewModel) : List<XuanquResponse>? {
    val html = vm.XuanquData.value

    // 定义一个正则表达式来匹配HTML标签
    val regex = """<td rowspan="(\d+)">(\d+)</td>\s*<td>(\d+)</td>\s*<td>(\d+)</td>\s*<td rowspan="\d+">(\d{4}-\d{2}-\d{2})</td>""".toRegex()

    val data = html?.let {
        regex.findAll(it).map {
            XuanquResponse(score = it.groupValues[2].toInt(), date = it.groupValues[5])
        }.toList()
    }
    return data
}