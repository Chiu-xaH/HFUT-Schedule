package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.App.MyApplication

object AcademicServiceCreator : BaseServiceCreator(isJSONorXML = false, url = MyApplication.ACADEMIC_URL)