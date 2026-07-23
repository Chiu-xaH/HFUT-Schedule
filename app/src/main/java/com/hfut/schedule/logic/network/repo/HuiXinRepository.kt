package com.hfut.schedule.logic.network.repo

import androidx.lifecycle.MutableLiveData

import com.google.gson.JsonObject
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinHefeiBuilding
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinHefeiBuildingResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinMonthBill
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinMonthBillResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinRangeBillResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinChangeLimitResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinFeeType
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinLoginResponse
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinPayStep1Response
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinPayStep2Response
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinPayStep3Response
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.xah.common.logic.state.UiStateHolder
import com.xah.common.logic.state.NetworkUiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.api.impl.HuiXinServiceCreator
import com.hfut.schedule.network.api.inf.HuiXinService
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.core.GsonInstance
import com.consumption.forecast.getConsumptionResult
import com.xah.common.logic.model.HuiXinBill
import com.xah.common.logic.model.HuiXinBillResponse
import com.consumption.forecast.model.result.TotalResult
import com.hfut.schedule.network.api.repo.HuiXinRepositoryInf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object HuiXinRepository : HuiXinRepositoryInf {
    private val huiXin = HuiXinServiceCreator.create(HuiXinService::class.java)

    override suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int,
        holder : UiStateHolder<HuiXinBill>
    ) = launchRequestState(
        holder = holder,
        request = { huiXin.Cardget(auth, page, size.toString()) },
        transformSuccess = { _, json -> parseHuiXinBills(json) }
    )
    @JvmStatic
    private fun parseHuiXinBills(json : String) : HuiXinBill = try {
        if(json.contains("操作成功")){
            GsonInstance.fromJson(json, HuiXinBillResponse::class.java).data
        } else
            throw Exception(json)
    } catch (e : Exception) { throw e }

    fun getHuiXinCardInfo(auth : String,huiXinCardInfoResponse : MutableLiveData<String?>) {
        val call = huiXin.getYue(auth)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val body = response.body()?.string()
                huiXinCardInfoResponse.value = body
                SharedPrefs.saveString("cardyue", body)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    override suspend fun checkHuiXinLogin(auth : String, holder : UiStateHolder<Boolean>)= launchRequestState(
        holder = holder,
        request = { huiXin.checkLogin(auth) },
        transformSuccess = { _, json -> parseCheckLHuiXinLogin(json) }
    )
    @JvmStatic
    private fun parseCheckLHuiXinLogin(json : String) : Boolean = try {
        if(json.contains("操作成功")) {
            true
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw  e }

    override suspend fun huiXinSingleLogin(studentId : String, password: String, holder : UiStateHolder<String>) {
        launchRequestState(
            holder = holder,
            request = { huiXin.login(studentId = studentId, password = password) },
            transformSuccess = { _, json -> parseHuiXinLogin(json) }
        )
    }
    private fun parseHuiXinLogin(json : String) : String = try {
        val token = GsonInstance.fromJson(json, HuiXinLoginResponse::class.java).token
        SharedPrefs.saveString("auth", token)
        showToast("一卡通登陆成功")
        token
    } catch (e : Exception) {
        showToast("一卡通登陆失败 ${e.message}")
        throw  e
    }

    override suspend fun payStep1(auth: String, json: String, pay : Float, type: HuiXinFeeType, holder : UiStateHolder<String>) =
        launchRequestState(
            holder = holder,
            request = {
                huiXin.pay(
                    auth = auth,
                    pay = pay,
                    flag = "choose",
                    paystep = 0,
                    json = json,
                    typeId = type.code,
                    isWX = null,
                    orderid = null,
                    password = null,
                    paytype = null,
                    paytypeid = null,
                    cardId = null
                )
            },
            transformSuccess = { _, json -> parseHuiXinPayStep1(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep1(result : String) : String = try {
        if(result.contains("操作成功")) {
            GsonInstance.fromJson(result, HuiXinPayStep1Response::class.java).data.orderId
        } else {
            throw Exception("Step1失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    override suspend fun payStep2(auth: String, orderId : String, type : HuiXinFeeType, holder : UiStateHolder<Map<String, String>>) =
        launchRequestState(
            holder = holder,
            request = {
                huiXin.pay(
                    auth = auth,
                    pay = null,
                    flag = null,
                    paystep = 2,
                    json = null,
                    typeId = 261,
                    isWX = null,
                    orderid = orderId,
                    password = null,
                    paytype = "CARDTSM",
                    paytypeid = type.payTypeId,
                    cardId = null
                )
            },
            transformSuccess = { _, json -> parseHuiXinPayStep2(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep2(result : String) : Map<String, String> = try {
        if(result.contains("操作成功")) {
            GsonInstance.fromJson(result, HuiXinPayStep2Response::class.java).data.passwordMap
        } else {
            throw Exception("Step2失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    override suspend fun payStep3(auth: String, orderId : String, password : String, uuid : String, type: HuiXinFeeType, holder : UiStateHolder<String>) =
        launchRequestState(
            holder = holder,
            request = {
                huiXin.pay(
                    auth = auth,
                    pay = null,
                    flag = null,
                    paystep = 2,
                    json = null,
                    isWX = 0,
                    orderid = orderId,
                    password = password,
                    paytype = "CARDTSM",
                    paytypeid = type.payTypeId,
                    cardId = uuid,
                    typeId = null
                )
            },
            transformSuccess = { _, json -> parseHuiXinPayStep3(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep3(result : String) : String = try {
        if(result.contains("success")) {
            GsonInstance.fromJson(result, HuiXinPayStep3Response::class.java).msg
        } else {
            throw Exception("支付失败")
        }
    } catch (e : Exception) { throw e }

    override suspend fun changeLimit(auth: String, json: JsonObject, holder : UiStateHolder<String>) =
        launchRequestState(
            holder = holder,
            request = { huiXin.changeLimit(auth, json) },
            transformSuccess = { _, json -> parseHuiXinChangeLimit(json) }
        )
    @JvmStatic
    private fun parseHuiXinChangeLimit(json : String) : String = try {
        GsonInstance.fromJson(json, HuiXinChangeLimitResponse::class.java).msg
    } catch (e : Exception) { throw e }

    override suspend fun searchDate(auth : String, timeFrom : String, timeTo : String, holder : UiStateHolder<Float>) =
        launchRequestState(
            holder = holder,
            request = { huiXin.searchDate(auth, timeFrom, timeTo) },
            transformSuccess = { _, json -> parseHuiXinRange(json) }
        )
    @JvmStatic
    private fun parseHuiXinRange(result : String) : Float = try {
        if(result.contains("操作成功")) {
            val data = GsonInstance.fromJson(result, HuiXinRangeBillResponse::class.java)
            data.data.expenses / 100
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    override suspend fun searchBills(auth : String, info: String, page : Int, holder : UiStateHolder<HuiXinBill>) =
        launchRequestState(
            holder = holder,
            request = {
                huiXin.searchBills(
                    auth,
                    info,
                    page,
                    Constant.DEFAULT_PAGE_SIZE
                )
            },
            transformSuccess = { _, json -> parseHuiXinSearchBills(json) }
        )
    @JvmStatic
    private fun parseHuiXinSearchBills(result : String) : HuiXinBill = try {
        if(result.contains("操作成功")) {
            GsonInstance.fromJson(result, HuiXinBillResponse::class.java).data
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    override suspend fun getMonthBills(auth : String, dateStr: String, holder : UiStateHolder<List<HuiXinMonthBill>>) =
        launchRequestState(
            holder = holder,
            request = { huiXin.getMonthYue(auth, dateStr) },
            transformSuccess = { _, json -> parseHuiXinMonthBills(json) }
        )
    @JvmStatic
    private fun parseHuiXinMonthBills(json : String) : List<HuiXinMonthBill> = try {
        if(json.contains("操作成功")) {
            val data = GsonInstance.fromJson(json, HuiXinMonthBillResponse::class.java)
            val bill = data.data
            bill.map { (date,balance) -> HuiXinMonthBill(date, balance) }
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw e }


    @JvmStatic
    private fun parseHefeiBuildings(json : String) : List<HuiXinHefeiBuilding> = try {
        GsonInstance.fromJson(json, HuiXinHefeiBuildingResponse::class.java).map.data
    } catch (e : Exception) { throw e }

    override suspend fun getHefeiRooms(
        auth: String,
        building: String?,
        holder: UiStateHolder<List<HuiXinHefeiBuilding>>
    ) = launchRequestState(
        request = {
            huiXin.getFee(
                auth = auth,
                type = "select",
                typeId = 1,
                campus = "1sh",
                level = if (building == null) "1" else "2",
                building = building
            )
        },
        holder = holder,
        transformSuccess = { _, json -> parseHefeiBuildings(json) }
    )


    fun getFee(
        auth: String,
        type : HuiXinFeeType,
        room : String? = null,
        phoneNumber : String? = null,
        building : String? = null,
        hefeiElectric : MutableLiveData<String?>,
        netValue : MutableLiveData<String?>,
        electricData : MutableLiveData<String?>,
        showerData : MutableLiveData<String?>
    ) {

        val feeItemId = type.code
        val campus = when(type) {
            HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "1sh"
            else -> null
        }
        val levels = when(type) {
            HuiXinFeeType.NET_XUANCHENG -> "0"
            HuiXinFeeType.ELECTRIC_XUANCHENG -> null
            HuiXinFeeType.SHOWER_XUANCHENG -> "1"
            HuiXinFeeType.SHOWER_HEFEI -> "未适配"
            HuiXinFeeType.WASHING_HEFEI -> "未适配"
            HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "1"
        }
        val rooms = when(type) {
            HuiXinFeeType.NET_XUANCHENG -> null
            HuiXinFeeType.ELECTRIC_XUANCHENG -> room
            HuiXinFeeType.SHOWER_XUANCHENG -> null
            HuiXinFeeType.SHOWER_HEFEI -> null
            HuiXinFeeType.WASHING_HEFEI -> "未适配"
            HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> room
        }
        val phoneNumbers = when(type) {
            HuiXinFeeType.NET_XUANCHENG -> null
            HuiXinFeeType.ELECTRIC_XUANCHENG -> null
            HuiXinFeeType.SHOWER_XUANCHENG -> phoneNumber
            HuiXinFeeType.SHOWER_HEFEI -> phoneNumber
            HuiXinFeeType.WASHING_HEFEI -> "未适配"
            HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> null
        }
        val buildings = when(type) {
            HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> building
            else -> null
        }
        val call = huiXin.getFee(
            auth = auth,
            typeId = feeItemId,
            room = rooms,
            level = levels,
            phoneNumber = phoneNumbers,
            type = "IEC",
            campus = campus,
            building = buildings
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                when(type) {
                    HuiXinFeeType.NET_XUANCHENG -> netValue.value = responseBody
                    HuiXinFeeType.ELECTRIC_XUANCHENG ->  electricData.value = responseBody
                    HuiXinFeeType.SHOWER_XUANCHENG -> showerData.value = responseBody
                    HuiXinFeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> hefeiElectric.value = responseBody
                    else -> {
                        showToast("未适配")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { t.printStackTrace() }
        })
    }

    suspend fun getCardPredicted(
        auth: String,
        huiXinBillResult : UiStateHolder<HuiXinBill>,
        cardPredictedResponse : UiStateHolder<TotalResult>
    ) = withContext(Dispatchers.IO) {
        suspend fun reloadAllBills(origin: HuiXinBill) {
            huiXinBillResult.clear()
            getCardBill(auth, page = 1, size = origin.total, huiXinBillResult)

            val newState = huiXinBillResult.state.first()
            when (newState) {
                is NetworkUiState.Error -> {
                    cardPredictedResponse.emitError(newState.exception, newState.code)
                }

                is NetworkUiState.Success -> {
                    try {
                        val data = getConsumptionResult(newState.data)
                        cardPredictedResponse.emitData(data)
                    } catch (e: Exception) {
                        cardPredictedResponse.emitError(e, PARSE_ERROR_CODE)
                    }
                }

                else -> {
                    cardPredictedResponse.emitError(Exception("未知错误"), null)
                }
            }
        }

        val currentState = huiXinBillResult.state.first()

        when (currentState) {
            is NetworkUiState.Success -> {
                val data = currentState.data
                if (data.size != data.total) {
                    reloadAllBills(data)
                }
            }

            else -> {
                // 第一次加载，拉取一条记录获取总数
                getCardBill(auth, page = 1, size = 1, huiXinBillResult)
                val stateAfterInit = huiXinBillResult.state.first()
                if (stateAfterInit is NetworkUiState.Success) {
                    reloadAllBills(stateAfterInit.data)
                }
            }
        }
    }
}