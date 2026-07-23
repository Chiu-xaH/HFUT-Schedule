package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.cas.CasGetFlavorSessionDto
import com.xah.common.logic.state.UiStateHolder

interface CasLoginRepositoryInf {
    suspend fun gotoCommunity(cookie : String): Int
    suspend fun gotoSecondClass(cookie : String): Int
    suspend fun gotoZhiJian(cookie : String): Int
    suspend fun gotoLibrary(cookie : String): Int
    suspend fun goToStu(cookie : String): Int
    suspend fun goToPe(cookie : String): Int
    suspend fun goToOne(cookie : String): Int
    suspend fun goToHuiXin(cookie : String): Int
    suspend fun getCasCookie(execution : UiStateHolder<Pair<String, String>>)
    suspend fun getEncryptKey(jSessionId : UiStateHolder<CasGetFlavorSessionDto>)
}