package com.hfut.schedule.logic.network.servicecreator

import com.hfut.schedule.App.MyApplication

object AcademicXCServiceCreator : BaseServiceCreator(isJSONorXML = false, url = MyApplication.XC_ACADEMIC_URL)