package com.hfut.schedule.ui.screen.home.search.function.survey

import com.hfut.schedule.logic.model.jxglstu.SurveyResponse
import com.hfut.schedule.logic.model.jxglstu.blankQuestions
import com.hfut.schedule.logic.model.jxglstu.options
import com.hfut.schedule.logic.model.jxglstu.radioQuestions

fun getSurveyChoice(bean : SurveyResponse) : List<radioQuestions> {
    return try {
        val survey = bean.survey
        val radioQuestions = survey.radioQuestions
        radioQuestions
    } catch (e : Exception) {
        emptyList()
    }
}
fun getSurveyInput(bean : SurveyResponse) : List<blankQuestions> {
    return try {
        val survey = bean.survey
        val blankQuestions = survey.blankQuestions
        blankQuestions
    } catch (e : Exception) {
        emptyList()
    }
}

fun getSurveyAssoc(bean: SurveyResponse) : Int {
    return try {
        bean.lessonSurveyLesson.surveyAssoc
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