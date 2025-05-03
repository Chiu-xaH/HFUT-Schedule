package com.hfut.schedule.ui.screen.home.search.function.survey

import com.google.gson.Gson
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.SurveyTeacherResponse
import com.hfut.schedule.logic.model.jxglstu.blankQuestions
import com.hfut.schedule.logic.model.jxglstu.lessonSurveyTasks
import com.hfut.schedule.logic.model.jxglstu.options
import com.hfut.schedule.logic.model.jxglstu.radioQuestions

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