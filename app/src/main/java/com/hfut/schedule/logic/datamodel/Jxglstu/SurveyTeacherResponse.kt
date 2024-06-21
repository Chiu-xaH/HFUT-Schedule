package com.hfut.schedule.logic.datamodel.Jxglstu

import android.os.IBinder

//获取教师
data class SurveyTeacherResponse(val forStdLessonSurveySearchVms : List<forStdLessonSurveySearchVms>)
data class forStdLessonSurveySearchVms(val lessonSurveyTasks : List<lessonSurveyTasks>)
data class lessonSurveyTasks(val id : Int,val submitted : Boolean,val teacher : teacher)


//获取题目
data class SurveyResponse(val lessonSurveyLesson : lessonSurveyLesson,val survey : survey)
data class lessonSurveyLesson(val surveyAssoc : Int)
data class survey(val radioQuestions : List<radioQuestions>,
                  val blankQuestions : List<blankQuestions>)
data class radioQuestions(val id : String,
                          val title : String,
                          val options : List<options>)
data class blankQuestions(val id : String,val title : String)
data class options(val name : String,val score : Int)

//提交结果
data class PostSurvey(val surveyAssoc : Int,
                      val lessonSurveyTaskAssoc : Int,
                      val radioQuestionAnswers : List<radioQuestionAnswer>,
                      val blankQuestionAnswers : List<blankQuestionAnswer>)
data class radioQuestionAnswer(val questionId : String, val optionName : String)
data class blankQuestionAnswer(val questionId : String, val content : String)
data class saveListChoice(val radioQuestionAnswers: List<radioQuestionAnswer>)
data class saveListInput(val blankQuestionAnswers : List<blankQuestionAnswer>)