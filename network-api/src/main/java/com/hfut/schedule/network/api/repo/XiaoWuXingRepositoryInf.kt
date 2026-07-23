package com.hfut.schedule.network.api.repo

import android.graphics.Bitmap
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingFunction
import com.hfut.schedule.network.api.model.response.json.xiaowuxing.XiaoWuXingSchool
import com.xah.common.logic.state.UiStateHolder

interface XiaoWuXingRepositoryInf {
    suspend fun getSchoolList(
        holder: UiStateHolder<List<XiaoWuXingSchool>>
    )
    suspend fun login(
        schoolCode : Long,
        username : String,
        password : String,
        holder : UiStateHolder<Boolean>
    )
    suspend fun getFunctions(
        schoolCode : Long,
        username : String,
        token : String,
        holder : UiStateHolder<List<XiaoWuXingFunction>>
    )
    suspend fun getDocPreview(
        schoolCode : Long,
        username : String,
        filePropertyType : Int,
        fileProperty : String,
        token : String,
        holder : UiStateHolder<Bitmap>
    )
}