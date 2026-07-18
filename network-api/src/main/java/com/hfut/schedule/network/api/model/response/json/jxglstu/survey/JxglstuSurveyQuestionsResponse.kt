package com.hfut.schedule.network.api.model.response.json.jxglstu.survey

//获取题目
data class JxglstuSurveyQuestionsResponse(
    val lessonSurveyLesson : JxglstuSurveyInfoLesson,
    val survey : JxglstuSurveyQuestions
)

data class JxglstuSurveyInfoLesson(
    val surveyAssoc : Int
)

data class JxglstuSurveyQuestions(
    val radioQuestions : List<JxglstuSurveyRadioQuestion>,
    val blankQuestions : List<JxglstuSurveyBlankQuestion>
)

data class JxglstuSurveyRadioQuestion(
    val id : String,
    val title : String,
    val options : List<JxglstuSurveyRadioQuestionOption>
)

data class JxglstuSurveyBlankQuestion(
    val id : String,
    val title : String
)

data class JxglstuSurveyRadioQuestionOption(
    val name : String,
    val score : Int
)
