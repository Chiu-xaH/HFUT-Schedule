package com.hfut.schedule.network.api.model.request.jxglstu

//提交结果
data class JxglstuSurveyPostRequest(
    val surveyAssoc : Int,
    val lessonSurveyTaskAssoc : Int,
    val radioQuestionAnswers : List<JxglstuSurveyPostRadioQuestionAnswer>,
    val blankQuestionAnswers : List<JxglstuSurveyPostBlankQuestionAnswer>
)

data class JxglstuSurveyPostRadioQuestionAnswer(
    val questionId : String,
    val optionName : String
)

data class JxglstuSurveyPostBlankQuestionAnswer(
    val questionId : String,
    val content : String
)