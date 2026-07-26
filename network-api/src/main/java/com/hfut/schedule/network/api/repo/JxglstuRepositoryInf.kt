package com.hfut.schedule.network.api.repo

import com.google.gson.JsonObject
import com.hfut.schedule.network.api.model.response.html.JxglstuExam
import com.hfut.schedule.network.api.model.response.html.JxglstuTermGrade
import com.hfut.schedule.network.api.model.response.html.JxglstuTransferMajorDetail
import com.hfut.schedule.network.api.model.response.json.jxglstu.JxglstuSelectedCourseConfirmation
import com.hfut.schedule.network.api.model.response.json.jxglstu.JxglstuSelectedCourseConfirmationType
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuCourseTime
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuLesson
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTermLessonResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.lesson.JxglstuTextbook
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.JxglstuProgramResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramCompetitionResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.program.competition.JxglstuProgramSimpleCompletionResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.select.JxglstuSelectCourseDetailResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.select.JxglstuSelectCourseResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.survey.JxglstuSurveyLesson
import com.hfut.schedule.network.api.model.response.json.jxglstu.survey.JxglstuSurveyQuestionsResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorMyApplyResponse
import com.hfut.schedule.network.api.model.response.json.jxglstu.transfer.JxglstuTransferMajorResponse
import com.xah.common.logic.state.UiStateHolder

interface JxglstuRepositoryInf {
    fun updateServices()
    suspend fun checkJxglstuCanUse(): Int
    suspend fun postTransfer(
        cookie: String,
        batchId: String,
        id : String,
        phoneNumber : String,
        studentId : UiStateHolder<Int>,
        postTransferResponse: UiStateHolder<String>
    )
    suspend fun getFormCookie(
        cookie: String,
        batchId: String,
        id : String,
        studentId : UiStateHolder<Int>,
        fromCookie : UiStateHolder<String>
    ) : Unit?
    suspend fun cancelTransfer(
        cookie: String,
        batchId: String,
        id : String,
        studentId : UiStateHolder<Int>,
        cancelTransferResponse : UiStateHolder<Boolean>
    ) : Unit?
    suspend fun verify(cookie: String): Int
    suspend fun getSelectCourse(
        cookie: String,
        studentId : UiStateHolder<Int>,
        bizTypeIdResponse : UiStateHolder<Int>,
        selectCourseData : UiStateHolder<List<JxglstuSelectCourseResponse>>
    )
    suspend fun getSelectCourseInfo(cookie: String, id : Int,holder : UiStateHolder<List<JxglstuSelectCourseDetailResponse>>)
    /*
    fun getSCount(cookie: String,id : Int,stdCountData : MutableLiveData<String?>)
     */
    suspend fun getRequestID(
        cookie: String,
        lessonId : Int,
        courseId : Int,
        type : String,
        studentId : UiStateHolder<Int>,
        requestIdData : UiStateHolder<String>
    )
    suspend fun getSelectedCourse(
        cookie: String,
        courseId : Int,
        studentId : UiStateHolder<Int>,
        selectedData : UiStateHolder<List<JxglstuSelectCourseDetailResponse>>
    )
    suspend fun postSelect(
        cookie: String,
        requestId : String,
        studentId : UiStateHolder<Int>,
        selectResultData : UiStateHolder<Pair<Boolean, String>>
    )
    suspend fun getTransfer(
        cookie: String,
        batchId: String,
        studentId : UiStateHolder<Int>,
        transferData : UiStateHolder<JxglstuTransferMajorResponse>
    ) : Unit?
    suspend fun getTransferList(
        cookie: String,
        studentId : UiStateHolder<Int>,
        transferListData : UiStateHolder<List<JxglstuTransferMajorDetail>>
    ) : Unit?
    suspend fun getMyApply(
        cookie: String,
        batchId: String,
        studentId: UiStateHolder<Int>,
        myApplyData : UiStateHolder<JxglstuTransferMajorMyApplyResponse>
    ) : Unit?
    suspend fun getGradeFromJxglstu(
        cookie: String,
        semester: Int?,
        studentId: UiStateHolder<Int>,
        jxglstuGradeData : UiStateHolder<List<JxglstuTermGrade>>
    ) : Unit?
    suspend fun parseJxglstuGrade(html: String): List<JxglstuTermGrade>
    fun jxglstuLogin(cookie : String)
    suspend fun getBizTypeId(cookie: String,studentId : Int,holder : UiStateHolder<Int>)
    suspend fun getStudentId(cookie : String,holder : UiStateHolder<Int>)
    suspend fun getLessonIds(cookie : String,studentId : Int,bizTypeId : Int,holder : UiStateHolder<JxglstuTermLessonResponse>)
    suspend fun getDatum(
        cookie : String,
        lessonIdList : List<Int>,
        studentId : UiStateHolder<Int>,
        datumData : UiStateHolder<String>
    ) : Unit?
    suspend fun getInfo(cookie : String,studentId : UiStateHolder<Int>)
    suspend fun getLessonTimes(cookie: String,timeCampusId : Int,holder : UiStateHolder<List<JxglstuCourseTime>>)
    suspend fun getProgram(
        cookie: String,
        studentId: UiStateHolder<Int>,
        programData : UiStateHolder<JxglstuProgramResponse>
    ) : Unit?
    suspend fun getProgramCompletion(cookie: String,holder : UiStateHolder<JxglstuProgramSimpleCompletionResponse>)
    suspend fun getProgramPerformance(
        cookie: String,
        studentId: UiStateHolder<Int>,
        programPerformanceData : UiStateHolder<JxglstuProgramCompetitionResponse>
    ) : Unit?
    suspend fun searchCourse(
        cookie: String,
        className : String?,
        courseName : String?,
        semester : Int,
        courseId : String?,
        studentId: UiStateHolder<Int>,
        courseSearchResponse : UiStateHolder<List<JxglstuLesson>>
    ) : Unit?
    suspend fun getSurveyList(
        cookie: String,
        semester : Int,
        studentId: UiStateHolder<Int>,
        surveyListData : UiStateHolder<List<JxglstuSurveyLesson>>
    ) : Unit?
    suspend fun getSurvey(cookie: String, id : String,holder : UiStateHolder<JxglstuSurveyQuestionsResponse>)
    suspend fun getSurveyToken(
        cookie: String,
        id : String,
        studentId : UiStateHolder<Int>,
        surveyToken : UiStateHolder<String>
    ) : Unit?
    suspend fun postSurvey(cookie : String, json: JsonObject): Int
    suspend fun getPhoto(cookie : String,studentId : UiStateHolder<Int>) : Unit?
    suspend fun getCourseBook(
        cookie: String,
        semester: Int,
        studentId: UiStateHolder<Int>,
        bizTypeIdResponse: UiStateHolder<Int>,
        courseBookResponse : UiStateHolder<Pair<Int, Map<Long, JxglstuTextbook>>>
    ) : Unit?
    fun parseCourseBook(json: String) : Map<Long, JxglstuTextbook>
    fun parseDatumCourse(result: String) : List<JxglstuLesson>
    suspend fun getExam(cookie: String, studentId : UiStateHolder<Int>, examHolder : UiStateHolder<List<JxglstuExam>>)
    suspend fun parseJxglstuExam(html : String) : List<JxglstuExam>
    suspend fun getSelectCourseConfirmation(cookie: String, studentId : UiStateHolder<Int>, holder : UiStateHolder< Map<JxglstuSelectedCourseConfirmationType, List<JxglstuSelectedCourseConfirmation>>>)
}