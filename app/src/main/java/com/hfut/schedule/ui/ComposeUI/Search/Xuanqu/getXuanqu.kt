package com.hfut.schedule.ui.ComposeUI.Search.Xuanqu

import android.util.Log
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.XuanquResponse
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs

fun getXuanqu(vm: LoginSuccessViewModel) : List<XuanquResponse>? {
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