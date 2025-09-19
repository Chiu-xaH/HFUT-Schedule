package com.hfut.schedule.logic.network.repo.hfut

import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.network.api.LoginWebsService
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.login.LoginWeb2ServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.LoginWebHefeiServiceCreator
import com.hfut.schedule.logic.network.servicecreator.login.LoginWebServiceCreator
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.WebInfo
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.person.getPersonInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

object LoginSchoolNetRepository {
    private val loginWebHefei = LoginWebHefeiServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb = LoginWebServiceCreator.create(LoginWebsService::class.java)
    private val loginWeb2 = LoginWeb2ServiceCreator.create(LoginWebsService::class.java)

    suspend fun loginSchoolNet(campus: CampusRegion = getCampusRegion(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().studentId?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        CampusRegion.HEFEI -> {
                            val location = "123"
                            launchRequestSimple(
                                holder = loginSchoolNetResponse,
                                request = {
                                    loginWebHefei.loginWeb(uid, pwd, location).awaitResponse()
                                },
                                transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                            )
                        }

                        CampusRegion.XUANCHENG -> {
                            val location = "宣州Login"
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = {
                                        loginWeb2.loginWeb(uid, pwd, location).awaitResponse()
                                    },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    suspend fun logoutSchoolNet(campus: CampusRegion = getCampusRegion(), loginSchoolNetResponse : StateHolder<Boolean>) =
        withContext(Dispatchers.IO) {
            getPersonInfo().studentId?.let { uid ->
                getCardPsk()?.let { pwd ->
                    when (campus) {
                        CampusRegion.HEFEI -> {
                            val location = "123"
                            launchRequestSimple(
                                holder = loginSchoolNetResponse,
                                request = {
                                    loginWebHefei.loginWeb(uid, pwd, location).awaitResponse()
                                },
                                transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                            )
                        }

                        CampusRegion.XUANCHENG -> {
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                            launch {
                                launchRequestSimple(
                                    holder = loginSchoolNetResponse,
                                    request = { loginWeb2.logoutWeb().awaitResponse() },
                                    transformSuccess = { _, body -> parseLoginSchoolNet(body) }
                                )
                            }
                        }
                    }
                }
            }
        }
    // 目前仅适配了宣区
    @JvmStatic
    private fun parseLoginSchoolNet(result : String) : Boolean = try {
        if(result.contains("成功") && !result.contains("已使用")) {
            true
        } else if(result.contains("已使用")) {
            false
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    suspend fun getWebInfo(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb.getInfo().awaitResponse() },
        transformSuccess = { _, json -> parseWebInfo(json) }
    )

    suspend fun getWebInfo2(infoWebValue : StateHolder<WebInfo>) = launchRequestSimple(
        holder = infoWebValue,
        request = { loginWeb2.getInfo().awaitResponse() },
        transformSuccess = { _, json -> parseWebInfo(json) }
    )
    @JvmStatic
    private fun parseWebInfo(html : String) : WebInfo = try {
        //本段照搬前端
        val flow = html.substringAfter("flow").substringBefore(" ").substringAfter("'").toDouble()
        val fee = html.substringAfter("fee").substringBefore(" ").substringAfter("'").toDouble()
        var flow0 = flow % 1024
        val flow1 = flow - flow0
        flow0 *= 1000
        flow0 -= flow0 % 1024
        var fee1 = fee - fee % 100
        var flow3 = "."
        if (flow0 / 1024 < 10) flow3 = ".00"
        else { if (flow0 / 1024 < 100) flow3 = ".0"; }
        val resultFee = (fee1 / 10000).toString()
        val resultFlow : String = ((flow1 / 1024).toString() + flow3 + (flow0 / 1024)).substringBefore(".")
        val result = WebInfo(resultFee, resultFlow)
        SharedPrefs.saveString("memoryWeb", result.flow)
        result
    } catch (e : Exception) { throw e }

}