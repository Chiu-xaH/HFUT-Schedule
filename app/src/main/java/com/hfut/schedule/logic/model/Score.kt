package com.hfut.schedule.logic.model

data class ScoreWithGPA(val gpa : Float,val score : Score)
data class Score(val min : Double,val max : Double,)
// 五星制
enum class ScoreGrade(val label : String,val gpa : Float,val score : Int) {
    // 100 80 70 60 0
    A("优",3.9f,100),B("良",3.0f,80),C("中",2.0f,70),D("及格",1.2f,60),F("不及格",0.0f,0)
}

val scoreWithGPA = listOf(
    ScoreWithGPA(4.3f, Score(95.0,100.0)),
    ScoreWithGPA(4.0f, Score(90.0,94.9)),
    ScoreWithGPA(3.7f, Score(85.0,89.9)),
    ScoreWithGPA(3.3f, Score(82.0,84.9)),
    ScoreWithGPA(3.0f, Score(78.0,81.9)),
    ScoreWithGPA(2.7f, Score(75.0,77.9)),
    ScoreWithGPA(2.3f, Score(72.0,74.9)),
    ScoreWithGPA(2.0f, Score(68.0,71.9)),
    ScoreWithGPA(1.7f, Score(66.0,67.9)),
    ScoreWithGPA(1.3f, Score(64.0,65.9)),
    ScoreWithGPA(1.0f, Score(60.0,63.9)),
    ScoreWithGPA(0f, Score(0.0,59.9)),
)

data class ScoreWithGPALevel(val gpa : Float,val score : Score?)
