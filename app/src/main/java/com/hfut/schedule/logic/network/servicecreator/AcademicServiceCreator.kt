package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.application.MyApplication

object AcademicServiceCreator : BaseServiceCreator(isJSONorXML = false, url = MyApplication.ACADEMIC_URL)