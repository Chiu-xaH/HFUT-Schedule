package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppBuilding
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassmate
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomCourse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppClassroomSearchResult
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppCourse
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppEmptyClassroom
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppGrade
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramData
import com.hfut.schedule.network.api.model.response.json.uniapp.UniAppProgramSearchData
import com.xah.common.logic.model.Campus
import com.xah.common.logic.state.UiStateHolder

interface UniAppRepositoryInf {
    suspend fun login() : Boolean
    suspend fun getClassmates(
        lessonId : String,
        token : String ,
        holder : UiStateHolder<List<UniAppClassmate>>
    )
    suspend fun updateCourses(token : String)
    suspend fun parseUniAppCourses(jStr : String? = null) :  List<UniAppCourse>
    suspend fun getGrades(
        token : String ,
        holder : UiStateHolder<Map<String, List<UniAppGrade>>>
    )
    suspend fun updateExams(token : String)
    suspend fun searchPrograms(
        token : String,
        page : Int ,
        keyword : String = "",
        holder : UiStateHolder<List<UniAppProgramSearchData>>
    )
    suspend fun getProgramById(
        id : Int,
        token: String,
        holder : UiStateHolder<UniAppProgramData>
    )
    suspend fun getBuildings(
        token : String,
        holder : UiStateHolder<List<UniAppBuilding>>
    )
    suspend fun getEmptyClassrooms(
        page : Int,
        date : String,
        campus: Campus?,
        buildings : List<Int>?,
        floors : List<Int>?,
        token : String,
        holder : UiStateHolder<List<UniAppEmptyClassroom>>
    )
    suspend fun searchClassrooms(
        input : String,
        token : String,
        page : Int,
        holder : UiStateHolder<List<UniAppClassroomSearchResult>>
    )
    suspend fun getClassroomLessons(
        semester: Int,
        roomId : Int,
        token : String,
        holder : UiStateHolder<List<UniAppClassroomCourse>>
    )
    suspend fun checkLogin(
        token : String
    ): Int
}