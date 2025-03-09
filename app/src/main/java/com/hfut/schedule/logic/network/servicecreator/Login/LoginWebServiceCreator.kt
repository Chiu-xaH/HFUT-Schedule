package com.hfut.schedule.logic.network.servicecreator.Login

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.servicecreator.BaseServiceCreator

object LoginWebServiceCreator : BaseServiceCreator(MyApplication.LOGIN_WEB_XUANCHENG_URL,false)

object LoginWeb2ServiceCreator : BaseServiceCreator(MyApplication.LOGIN_WEB_XUANCHENG2_URL,false)