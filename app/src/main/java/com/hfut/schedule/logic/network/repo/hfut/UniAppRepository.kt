package com.hfut.schedule.logic.network.repo.hfut

import com.google.gson.Gson
import com.hfut.schedule.logic.network.api.UniAppService
import com.hfut.schedule.logic.network.repo.hfut.UniAppLoginResponse.UniAppLoginError
import com.hfut.schedule.logic.network.repo.hfut.UniAppLoginResponse.UniAppLoginSuccessfulResponse
import com.hfut.schedule.logic.network.servicecreator.UniAppServiceCreator
import com.hfut.schedule.logic.util.network.Crypto
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.screen.home.cube.sub.getJxglstuPassword
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import retrofit2.awaitResponse

object UniAppRepository {
    private val uniApp = UniAppServiceCreator.create(UniAppService::class.java)

    suspend fun login()  {
        val sId = getPersonInfo().studentId ?: return
        val pwd = getJxglstuPassword() ?: return
        val request = uniApp.login(
            studentId = sId,
            password = Crypto.rsaEncrypt(pwd)
        ).awaitResponse()
        val json = request.body()?.string()
        if(json == null) {
            showToast("登陆合工大教务失败")
            return
        }
        if(!request.isSuccessful) {
            val msg = parseLogin(json,false)
            showToast("登陆合工大教务失败${msg}")
            return
        }
        val token = parseLogin(json,true)
        if(token == null) {
            showToast("登陆合工大教务失败2")
            return
        }
        DataStoreManager.saveUniAppJwt(token)
        showToast("登陆合工大教务成功")
    }
    @JvmStatic
    private fun parseLogin(
        json : String,
        isSuccessful : Boolean
    ) : String? = try {
        if(isSuccessful) {
            Gson().fromJson(json,UniAppLoginSuccessfulResponse::class.java).data.idToken
        } else {
            Gson().fromJson(json,UniAppLoginError::class.java).message
        }
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }
}

sealed class UniAppLoginResponse {
    data class UniAppLoginSuccessfulResponse(
        val data : UniAppLoginBean
    ) : UniAppLoginResponse()
    data class UniAppLoginError(
        val message : String
    ) : UniAppLoginResponse()
}

data class UniAppLoginBean(
    val idToken : String
)