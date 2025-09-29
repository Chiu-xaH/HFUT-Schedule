package com.hfut.schedule.service.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.hfut.schedule.logic.network.util.toTimestamp
import com.hfut.schedule.logic.util.sys.getJxglstuCourseSchedule
import com.xah.shared.Course
import com.xah.shared.IJxglstuAidlInterface
import com.xah.shared.JxglstuCourseGroup


class RemoteService : Service() {
    private fun getJxglstuCourseGroup(): List<JxglstuCourseGroup> {
        val list = getJxglstuCourseSchedule().map {
            Course(
                dateTime = Pair(it.time.start.toTimestamp(),it.time.end.toTimestamp()),
                place = it.place,
                courseName = it.courseName
            )
        }
        return list.groupBy { item ->
            item.dateTime.first
        }.map { (date, courses) ->
            JxglstuCourseGroup(date, courses)
        }
    }

    private val binder = object : IJxglstuAidlInterface.Stub() {
        override fun getJxglstuCourseGroups(): List<JxglstuCourseGroup> {
            return getJxglstuCourseGroup()
        }
    }
    override fun onBind(intent: Intent?): IBinder = binder
}

