package com.hfut.schedule.logic.network

import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuJSONServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Jxglstu.JxglstuXMLServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetAESKeyServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.GetCookieServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.LoginServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.OneGotoServiceCreator
import com.hfut.schedule.logic.network.ServiceCreator.Login.OneServiceCreator
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.api.LoginService
import com.hfut.schedule.logic.network.api.OneService

object Network {
    //调用接口
    private val LoginAPI = LoginServiceCreator.create(LoginService::class.java)
    private val getLoginCookieAPI = GetCookieServiceCreator.create(LoginService::class.java)
    private val getAESKeyAPI = GetAESKeyServiceCreator.create(LoginService::class.java)

    private val JxglstuJSONAPI = JxglstuJSONServiceCreator.create(JxglstuService::class.java)
    private val JxglstuXMLAPI = JxglstuXMLServiceCreator.create(JxglstuService::class.java)
    private val OnegotoAPI = OneGotoServiceCreator.create(LoginService::class.java)
    private val OneGetAPI = OneServiceCreator.create(OneService::class.java)

    
}