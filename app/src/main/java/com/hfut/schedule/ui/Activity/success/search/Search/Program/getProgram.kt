package com.hfut.schedule.ui.Activity.success.search.Search.Program

import android.util.Log
import com.google.gson.Gson
import com.hfut.schedule.ViewModel.LoginSuccessViewModel
import com.hfut.schedule.logic.datamodel.Jxglstu.PlanCourses
import com.hfut.schedule.logic.datamodel.Jxglstu.ProgramResponse
import com.hfut.schedule.logic.utils.SharePrefs.Save
import com.hfut.schedule.logic.utils.SharePrefs.prefs


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
                            Save("programJSON",json)
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

fun getProgramListOne(): MutableList<ProgramPartOne> {
    val list = mutableListOf<ProgramPartOne>()
    return try {
        val json = prefs.getString("program","")
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
        list
    }
}

fun getProgramListTwo(item : Int): MutableList<ProgramPartTwo> {
    val list = mutableListOf<ProgramPartTwo>()
    try {
        val partl = getProgramListOne()[item]
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



fun getProgramListThree(item1 : Int,item2 : Int): MutableList<ProgramPartThree> {
    val list = mutableListOf<ProgramPartThree>()
    try {
        val partl = getProgramListTwo(item1)[item2]
        val partChilren = partl
        val part = partl.part

        for(i in part.indices) {
            val term = part[i].readableTerms[0]
            val course = part[i].course
            val courseName = course.nameZh
            val credit = course.credits
            val depart = part[i].openDepartment.nameZh
            list.add(ProgramPartThree(term,courseName,credit,depart))
        }
        return list
    } catch (e : Exception) {
        return list
    }
}


data class InProgramResponse(val ProgramShow : List<ProgramShow>)

data class ProgramPartOne(val type : String?,
                          val requiedCredits : Double?,
                          val partChildren : List<ProgramResponse?>,
                          val partCourse : List<PlanCourses>)
data class ProgramPartTwo(val type : String?,
                          val requiedCredits : Double?,
                          val part : List<PlanCourses>)
data class ProgramPartThree(val term : Int?,
                            val name : String,
                            val credit : Double?,
                            val depart :String)
data class ProgramShow(val name : String,
                       val type : String?,
                       val credit : Double?,
                       val term : List<Int?>,
                       val studyMust : Boolean?,
                       val school : String?,
                       val remark : String?)