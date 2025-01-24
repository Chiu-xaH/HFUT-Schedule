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
    var list = mutableListOf<lessonSurveyTasks>()
    return try {
        val result = Gson().fromJson(vm.surveyListData.value, SurveyTeacherResponse::class.java).forStdLessonSurveySearchVms
        for(i in result.indices) {
            val teacherList = result[i].lessonSurveyTasks
            for(j in teacherList.indices) {
                // val element = teacherList[j]
                list.add(teacherList[j])
            }
        }
        list
    } catch (e : Exception) {
        list
    }
}
fun getSurveyChoice(vm : NetWorkViewModel) : MutableList<radioQuestions> {
    val list = mutableListOf<radioQuestions>()
    return try {
        val json = Gson().fromJson(vm.surveyData.value, SurveyResponse::class.java)
        val survey = json.survey
        val radioQuestions = survey.radioQuestions
        for(i in radioQuestions.indices) {
            list.add(radioQuestions[i])
        }
        list
    } catch (e : Exception) {
        list
    }
}
fun getSurveyInput(vm : NetWorkViewModel) : MutableList<blankQuestions> {
    val list = mutableListOf<blankQuestions>()
    return try {
        val json = Gson().fromJson(vm.surveyData.value, SurveyResponse::class.java)
        val survey = json.survey
        val blankQuestions = survey.blankQuestions
        for(i in blankQuestions.indices) {
            list.add(blankQuestions[i])
        }
        list
    } catch (e : Exception) {
        list
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
fun getOption(choiceList : radioQuestions) : MutableList<options> {
    val list = mutableListOf<options>()
    return try {
        //   for(i in choiceList.indices)  {
        for(j in choiceList.options.indices) {
            list.add(choiceList.options[j])
        }
        //   }
        list
    } catch (e : Exception) {
        list
    }
}