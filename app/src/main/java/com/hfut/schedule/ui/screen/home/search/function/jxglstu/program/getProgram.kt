package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import com.hfut.schedule.network.api.model.response.json.jxglstu.program.JxglstuProgramPlanCourse
import com.hfut.schedule.network.api.model.response.json.shared.ProgramSearchPlanCourse
import com.hfut.schedule.logic.model.dto.JxglstuProgramItemDto
import com.hfut.schedule.ui.component.icon.filterDepartmentName
import com.xah.common.logic.util.LogUtil


fun planCoursesTransform(planCourses : JxglstuProgramPlanCourse) : JxglstuProgramItemDto? = try {
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
        val depart = openDepartment.nameZh.filterDepartmentName()
        JxglstuProgramItemDto(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory)
    }
} catch (e : Exception) {
    LogUtil.error(e)
    null
}

fun planCoursesTransform(planCourses : ProgramSearchPlanCourse) : JxglstuProgramItemDto? = try {
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
        val depart = openDepartment.nameZh.filterDepartmentName()
        JxglstuProgramItemDto(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory)
    }
} catch (e : Exception) {
    LogUtil.error(e)
    null
}