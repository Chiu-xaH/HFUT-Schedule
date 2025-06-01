package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.logic.model.jxglstu.PlanCoursesSearch
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree


fun planCoursesTransform(planCourses : PlanCourses) : ProgramPartThree? = try {
    with(planCourses) {
        val term = readableTerms[0]
        val course = course
        val courseName = course.nameZh
        val code = course.code
        val week = course.periodInfo.weeks
        val courseType = course.courseType.nameZh
        val remark = remark
        val isCompulsory = compulsory
        val credit = course.credits
        var depart = openDepartment.nameZh
        if(depart.contains("（")) depart = depart.substringBefore("（")

        ProgramPartThree(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory)
    }
} catch (e : Exception) {
    null
}

fun planCoursesTransform(planCourses : PlanCoursesSearch) : ProgramPartThree? = try {
    with(planCourses) {
        val term = terms[0].substringAfter("_").toIntOrNull()
        val course = course
        val courseName = course.nameZh
        val code = course.code
        val week = periodInfo.weeks
        val courseType = course.courseType.nameZh
        val remark = remark
        val isCompulsory = compulsory
        val credit = course.credits
        var depart = openDepartment.nameZh
        if(depart.contains("（")) depart = depart.substringBefore("（")

        ProgramPartThree(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory)
    }
} catch (e : Exception) {
    null
}