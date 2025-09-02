package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.application.MyApplication

object AcademicXCServiceCreator : BaseServiceCreator(isJSONorXML = false, url = MyApplication.XC_ACADEMIC_URL)