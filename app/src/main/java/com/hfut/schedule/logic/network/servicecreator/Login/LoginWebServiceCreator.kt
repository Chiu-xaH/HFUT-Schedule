package com.hfut.schedule.logic.network.servicecreator.Login

import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.logic.network.servicecreator.BaseServiceCreator

object LoginWebServiceCreator : BaseServiceCreator(MyApplication.loginWebURL,false)

object LoginWeb2ServiceCreator : BaseServiceCreator(MyApplication.loginWebURL2,false)