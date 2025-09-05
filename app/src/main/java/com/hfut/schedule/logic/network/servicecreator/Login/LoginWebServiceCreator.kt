package com.hfut.schedule.logic.network.servicecreator.Login

import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.network.servicecreator.BaseServiceCreator

object LoginWebServiceCreator : BaseServiceCreator(MyApplication.LOGIN_WEB_XC_URL)

object LoginWeb2ServiceCreator : BaseServiceCreator(MyApplication.LOGIN_WEB_XC2_URL)

object LoginWebHefeiServiceCreator : BaseServiceCreator(MyApplication.LOGIN_WEB_HEFEI_URL)
