package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsList
import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsType
import com.hfut.schedule.network.api.model.response.html.news.AcademicNewsXuanChengType
import com.hfut.schedule.network.api.model.response.html.news.News
import com.xah.common.logic.state.UiStateHolder

interface NewsRepositoryInf {
    fun searchXuanChengNews(title : String, page: Int = 1)
    suspend fun getXuanChengNews(page: Int,newsXuanChengResult : UiStateHolder<List<News>>)
    suspend fun getAcademicXC(type: AcademicNewsXuanChengType, page: Int = 1, holder : UiStateHolder<List<News>>)
    suspend fun getAcademic(type: AcademicNewsType, totalPage : Int? = null, page: Int = 1, holder : UiStateHolder<AcademicNewsList>)
    suspend fun searchNews(title : String,page: Int = 1,newsResult : UiStateHolder<List<News>>)
}