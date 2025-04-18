package com.hfut.schedule.ui.screen.home.search.function.dormitoryScore

import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.XuanquResponse

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