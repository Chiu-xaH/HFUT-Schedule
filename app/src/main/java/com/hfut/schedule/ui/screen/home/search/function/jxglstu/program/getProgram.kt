package com.hfut.schedule.ui.screen.home.search.function.jxglstu.program

import com.google.gson.Gson
import com.hfut.schedule.logic.model.jxglstu.PlanCourses
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.ProgramPartOne
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramPartTwo
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramShow
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs

//fun getProgramListOne(vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartOne> {
//    val list = mutableListOf<ProgramPartOne>()
//    return try {
//        val json = if(ifSaved) {
//            prefs.getString("program","")
//        } else vm.programData.value
//        val result = Gson().fromJson(json,ProgramResponse::class.java)
//        val children = result.children
//
//        for(i in children.indices) {
//            val type = children[i].type?.nameZh
//            val requiedCredits = children[i].requireInfo?.requiredCredits
//            val partChilren = children[i].children
//            val partCourse = children[i].planCourses
//            list.add(ProgramPartOne(type, requiedCredits, partChilren,partCourse))
//        }
//        list
//    } catch (e : Exception) {
////        Log.d("eeee",e.toString())
//        list
//    }
//}
//
//fun getProgramListTwo(item : Int, vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartTwo> {
//    val list = mutableListOf<ProgramPartTwo>()
//    try {
//        val partl = getProgramListOne(vm,ifSaved)[item]
//        //判断，在公共基础课程直接展开，不进入三级数组
//        if(partl.partChildren.isEmpty()) {
//            val part = partl.partCourse
//           list.add(ProgramPartTwo("直接跳转",null, part))
//            return list
//        } else {
//            val part = partl.partChildren
//            for(i in part.indices) {
//                val partTwo = part[i]
//                val type = partTwo?.type?.nameZh
//                val requiredCredits = partTwo?.requireInfo?.requiredCredits
//                val course = partTwo?.planCourses
//                course?.let { ProgramPartTwo(type,requiredCredits, it) }?.let { list.add(it) }
//            }
//            return list
//        }
//    } catch (e : Exception) {
//        return list
//    }
//}
//
//fun getProgramListThree(item1 : Int, item2 : Int, vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartThree> {
//    val list = mutableListOf<ProgramPartThree>()
//    try {
//        val partl = getProgramListTwo(item1,vm,ifSaved)[item2]
//        val part = partl.part
//
//        for(i in part.indices) {
//            val term = part[i].readableTerms[0]
//            val course = part[i].course
//            val courseName = course.nameZh
//            val code = course.code
//            val week = course.periodInfo.weeks
//            val courseType = course.courseType.nameZh
//            val remark = part[i].remark
//            val isCompulsory = part[i].compulsory
//            val credit = course.credits
//            var depart = part[i].openDepartment.nameZh
//            if(depart.contains("（")) depart = depart.substringBefore("（")
//
//            list.add(ProgramPartThree(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory))
//        }
//        return list
//    } catch (e : Exception) {
//        return list
//    }
//}


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