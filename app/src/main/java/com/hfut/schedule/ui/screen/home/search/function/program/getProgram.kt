package com.hfut.schedule.ui.screen.home.search.function.program

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.logic.model.jxglstu.ProgramBean
import com.hfut.schedule.logic.model.jxglstu.ProgramCompletionResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramPartOne
import com.hfut.schedule.logic.model.jxglstu.ProgramPartThree
import com.hfut.schedule.logic.model.jxglstu.ProgramPartTwo
import com.hfut.schedule.logic.model.jxglstu.ProgramResponse
import com.hfut.schedule.logic.model.jxglstu.ProgramShow
import com.hfut.schedule.logic.model.jxglstu.item
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs


fun getProgram()  {
    val list = mutableListOf<ProgramShow>()
    try {
        val json = prefs.getString("program","")
        val result = Gson().fromJson(json,ProgramResponse::class.java)
        val children = result.children
        for (i in children.indices) {
            val inChildren = children[i].children
            if (inChildren != null) {
                for(j in inChildren.indices) {
                    val planCourses = inChildren[j].planCourses
                    val type = inChildren[j].type?.nameZh
                    if (planCourses != null) {
                        for(k in planCourses.indices) {
                            val courseName = planCourses[k].course.nameZh
                            val term = planCourses[k].readableTerms
                            val remark = planCourses[k].remark
                            val credit = planCourses[k].course.credits
                            val school = planCourses[k].openDepartment.nameZh
                            list.add(ProgramShow(courseName,type, credit,term,null,school,remark))
                            val json = Gson().toJson(list)
                          //  Log.d("dd",json)
                            saveString("programJSON",json)
                        }
                     //   return list
                    } //else return list
                }
               // return list
            } //else return list
        } //else return list
    } catch (e : Exception) {
       // return list
    }
}

fun getProgramListOne(vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartOne> {
    val list = mutableListOf<ProgramPartOne>()
    return try {
        val json = if(ifSaved) {
            prefs.getString("program","")
        } else vm.ProgramData.value
        val result = Gson().fromJson(json,ProgramResponse::class.java)
        val children = result.children

        for(i in children.indices) {
            val type = children[i].type?.nameZh
            val requiedCredits = children[i].requireInfo?.requiredCredits
            val partChilren = children[i].children
            val partCourse = children[i].planCourses
            list.add(ProgramPartOne(type, requiedCredits, partChilren,partCourse))
        }
        list
    } catch (e : Exception) {
//        Log.d("eeee",e.toString())
        list
    }
}

fun getProgramListTwo(item : Int, vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartTwo> {
    val list = mutableListOf<ProgramPartTwo>()
    try {
        val partl = getProgramListOne(vm,ifSaved)[item]
        //判断，在公共基础课程直接展开，不进入三级数组
        if(partl.partChildren.isEmpty()) {
            val part = partl.partCourse
           list.add(ProgramPartTwo("直接跳转",null, part))
            return list
        } else {
            val part = partl.partChildren
            for(i in part.indices) {
                val partTwo = part[i]
                val type = partTwo?.type?.nameZh
                val requiredCredits = partTwo?.requireInfo?.requiredCredits
                val course = partTwo?.planCourses
                course?.let { ProgramPartTwo(type,requiredCredits, it) }?.let { list.add(it) }
            }
            return list
        }
    } catch (e : Exception) {
        return list
    }
}



fun getProgramListThree(item1 : Int, item2 : Int, vm : NetWorkViewModel, ifSaved : Boolean): MutableList<ProgramPartThree> {
    val list = mutableListOf<ProgramPartThree>()
    try {
        val partl = getProgramListTwo(item1,vm,ifSaved)[item2]
        val partChilren = partl
        val part = partl.part

        for(i in part.indices) {
            val term = part[i].readableTerms[0]
            val course = part[i].course
            val courseName = course.nameZh
            val code = course.code
            val week = course.periodInfo.weeks
            val courseType = course.courseType.nameZh
            val remark = part[i].remark
            val isCompulsory = part[i].compulsory
            val credit = course.credits
            var depart = part[i].openDepartment.nameZh
            if(depart.contains("（")) depart = depart.substringBefore("（")

            list.add(ProgramPartThree(term,courseName,credit, depart,code,week,courseType,remark,isCompulsory))
        }
        return list
    } catch (e : Exception) {
        return list
    }
}
