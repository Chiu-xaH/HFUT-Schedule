package com.hfut.schedule.ui.activity.home.search.functions.survey

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.NetWorkViewModel
import com.hfut.schedule.logic.beans.jxglstu.SurveyResponse
import com.hfut.schedule.logic.beans.jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.beans.jxglstu.blankQuestions
import com.hfut.schedule.logic.beans.jxglstu.lessonSurveyTasks
import com.hfut.schedule.logic.beans.jxglstu.options
import com.hfut.schedule.logic.beans.jxglstu.radioQuestions

fun getSurveyList(vm : NetWorkViewModel) : MutableList<lessonSurveyTasks> {
    val list = mutableListOf<lessonSurveyTasks>()
    return try {
        val result = Gson().fromJson(vm.surveyListData.value, SurveyTeacherResponse::class.java).forStdLessonSurveySearchVms
        for(i in result.indices) {
            val teacherList = result[i].lessonSurveyTasks
            for(j in teacherList.indices) {
                list.add(teacherList[j])
            }
        }
        list
    } catch (e : Exception) {
        list
    }
}
fun getSurveyChoice(vm : NetWorkViewModel) : List<radioQuestions> {
    return try {
        val json = Gson().fromJson(vm.surveyData.value, SurveyResponse::class.java)
        val survey = json.survey
        val radioQuestions = survey.radioQuestions
        radioQuestions
    } catch (e : Exception) {
        emptyList()
    }
}
fun getSurveyInput(vm : NetWorkViewModel) : List<blankQuestions> {
    return try {
        val json = Gson().fromJson(vm.surveyData.value, SurveyResponse::class.java)
        val survey = json.survey
        val blankQuestions = survey.blankQuestions
        blankQuestions
    } catch (e : Exception) {
        emptyList()
    }
}

fun getSurveyAssoc(vm : NetWorkViewModel) : Int {
    return try {
        val json = Gson().fromJson(vm.surveyData.value, SurveyResponse::class.java)
        json.lessonSurveyLesson.surveyAssoc
    } catch (e : Exception) {
        0
    }
}
fun getOption(choiceList : radioQuestions) : List<options> {
    return try {
        choiceList.options
    } catch (e : Exception) {
        emptyList()
    }
}