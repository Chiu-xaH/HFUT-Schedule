package com.hfut.schedule.ui.Activity.success.search.Search.Survey

import com.google.gson.Gson
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.SurveyResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.datamodel.Jxglstu.blankQuestions
import com.hfut.schedule.logic.datamodel.Jxglstu.lessonSurveyTasks
import com.hfut.schedule.logic.datamodel.Jxglstu.options
import com.hfut.schedule.logic.datamodel.Jxglstu.radioQuestions

fun getSurveyList(vm : LoginSuccessViewModel) : MutableList<lessonSurveyTasks> {
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
fun getSurveyChoice(vm : LoginSuccessViewModel) : MutableList<radioQuestions> {
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
fun getSurveyInput(vm : LoginSuccessViewModel) : MutableList<blankQuestions> {
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

fun getSurveyAssoc(vm : LoginSuccessViewModel) : Int {
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