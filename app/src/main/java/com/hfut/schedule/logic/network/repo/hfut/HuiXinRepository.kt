package com.hfut.schedule.logic.network.repo.hfut

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hfut.schedule.logic.model.HuiXinHefeiBuildingBean
import com.hfut.schedule.logic.model.HuiXinHefeiBuildingsResponse
import com.hfut.schedule.logic.model.huixin.BillMonth
import com.hfut.schedule.logic.model.huixin.BillMonthResponse
import com.hfut.schedule.logic.model.huixin.BillRangeResponse
import com.hfut.schedule.logic.model.huixin.ChangeLimitResponse
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.model.huixin.HuiXinLoginResponse
import com.hfut.schedule.logic.model.huixin.PayStep1Response
import com.hfut.schedule.logic.model.huixin.PayStep2Response
import com.hfut.schedule.logic.model.huixin.PayStep3Response
import com.hfut.schedule.logic.network.api.HuiXinService
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.servicecreator.HuiXinServiceCreator
import com.hfut.schedule.logic.util.network.getPageSize
import com.hfut.schedule.logic.util.network.state.PARSE_ERROR_CODE
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.shared.getConsumptionResult
import com.xah.shared.model.BillBean
import com.xah.shared.model.BillResponse
import com.xah.shared.model.TotalResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object HuiXinRepository {
    private val huiXin = HuiXinServiceCreator.create(HuiXinService::class.java)

    suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int = getPageSize(),
        holder : StateHolder<BillBean>
    ) = launchRequestSimple(
        holder = holder,
        request = { huiXin.Cardget(auth, page, size.toString()).awaitResponse() },
        transformSuccess = { _, json -> parseHuiXinBills(json) }
    )
    @JvmStatic
    private fun parseHuiXinBills(json : String) : BillBean = try {
        if(json.contains("操作成功")){
            Gson().fromJson(json, BillResponse::class.java).data
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

    suspend fun checkHuiXinLogin(auth : String,holder : StateHolder<Boolean>)= launchRequestSimple(
        holder = holder,
        request = { huiXin.checkLogin(auth).awaitResponse() },
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

    suspend fun huiXinSingleLogin(studentId : String,password: String,holder : StateHolder<String>) {
        launchRequestSimple(
            holder = holder,
            request = { huiXin.login(studentId = studentId, password = password).awaitResponse() },
            transformSuccess = { _, json -> parseHuiXinLogin(json) }
        )
    }
    private fun parseHuiXinLogin(json : String) : String = try {
        val token = Gson().fromJson(json, HuiXinLoginResponse::class.java).token
        SharedPrefs.saveString("auth", token)
        showToast("一卡通登陆成功")
        token
    } catch (e : Exception) {
        showToast("一卡通登陆失败 ${e.message}")
        throw  e
    }

    suspend fun payStep1(auth: String, json: String, pay : Float, type: FeeType, holder : StateHolder<String>) =
        launchRequestSimple(
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
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseHuiXinPayStep1(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep1(result : String) : String = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep1Response::class.java).data.orderid
        } else {
            throw Exception("Step1失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    suspend fun payStep2(auth: String, orderId : String, type : FeeType, holder : StateHolder<Map<String, String>>) =
        launchRequestSimple(
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
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseHuiXinPayStep2(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep2(result : String) : Map<String, String> = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, PayStep2Response::class.java).data.passwordMap
        } else {
            throw Exception("Step2失败 终止支付")
        }
    } catch (e : Exception) { throw e }

    suspend fun payStep3(auth: String, orderId : String, password : String, uuid : String, type: FeeType, holder : StateHolder<String>) =
        launchRequestSimple(
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
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseHuiXinPayStep3(json) }
        )
    @JvmStatic
    private fun parseHuiXinPayStep3(result : String) : String = try {
        if(result.contains("success")) {
            Gson().fromJson(result, PayStep3Response::class.java).msg
        } else {
            throw Exception("支付失败")
        }
    } catch (e : Exception) { throw e }

    suspend fun changeLimit(auth: String, json: JsonObject, holder : StateHolder<String>) =
        launchRequestSimple(
            holder = holder,
            request = { huiXin.changeLimit(auth, json).awaitResponse() },
            transformSuccess = { _, json -> parseHuiXinChangeLimit(json) }
        )
    @JvmStatic
    private fun parseHuiXinChangeLimit(json : String) : String = try {
        Gson().fromJson(json, ChangeLimitResponse::class.java).msg
    } catch (e : Exception) { throw e }

    suspend fun searchDate(auth : String, timeFrom : String, timeTo : String,holder : StateHolder<Float>) =
        launchRequestSimple(
            holder = holder,
            request = { huiXin.searchDate(auth, timeFrom, timeTo).awaitResponse() },
            transformSuccess = { _, json -> parseHuiXinRange(json) }
        )
    @JvmStatic
    private fun parseHuiXinRange(result : String) : Float = try {
        if(result.contains("操作成功")) {
            val data = Gson().fromJson(result, BillRangeResponse::class.java)
            data.data.expenses / 100
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    suspend fun searchBills(auth : String, info: String,page : Int,holder : StateHolder<BillBean>) =
        launchRequestSimple(
            holder = holder,
            request = {
                huiXin.searchBills(
                    auth,
                    info,
                    page,
                    getPageSize()
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseHuiXinSearchBills(json) }
        )
    @JvmStatic
    private fun parseHuiXinSearchBills(result : String) : BillBean = try {
        if(result.contains("操作成功")) {
            Gson().fromJson(result, BillResponse::class.java).data
        } else {
            throw Exception(result)
        }
    } catch (e : Exception) { throw e }

    suspend fun getMonthBills(auth : String, dateStr: String,holder : StateHolder<List<BillMonth>>) =
        launchRequestSimple(
            holder = holder,
            request = { huiXin.getMonthYue(auth, dateStr).awaitResponse() },
            transformSuccess = { _, json -> parseHuiXinMonthBills(json) }
        )
    @JvmStatic
    private fun parseHuiXinMonthBills(json : String) : List<BillMonth> = try {
        if(json.contains("操作成功")) {
            val data = Gson().fromJson(json, BillMonthResponse::class.java)
            val bill = data.data
            bill.map { (date,balance) -> BillMonth(date, balance) }
        } else {
            throw Exception(json)
        }
    } catch (e : Exception) { throw e }


    @JvmStatic
    private fun parseHefeiBuildings(json : String) : List<HuiXinHefeiBuildingBean> = try {
        Gson().fromJson(json, HuiXinHefeiBuildingsResponse::class.java).map.data
    } catch (e : Exception) { throw e }

    suspend fun getHefeiRooms(
        auth: String,
        building: String?,
        holder: StateHolder<List<HuiXinHefeiBuildingBean>>
    ) = launchRequestSimple(
        request = {
            huiXin.getFee(
                auth = auth,
                type = "select",
                typeId = 1,
                campus = "1sh",
                level = if(building == null) "1" else "2",
                building = building
            ).awaitResponse()
        },
        holder = holder,
        transformSuccess = { _,json -> parseHefeiBuildings(json) }
    )


    fun getFee(
        auth: String,
        type : FeeType,
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
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "1sh"
            else -> null
        }
        val levels = when(type) {
            FeeType.NET_XUANCHENG -> "0"
            FeeType.ELECTRIC_XUANCHENG -> null
            FeeType.SHOWER_XUANCHENG -> "1"
            FeeType.SHOWER_HEFEI -> "未适配"
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> "1"
        }
        val rooms = when(type) {
            FeeType.NET_XUANCHENG -> null
            FeeType.ELECTRIC_XUANCHENG -> room
            FeeType.SHOWER_XUANCHENG -> null
            FeeType.SHOWER_HEFEI -> null
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> room
        }
        val phoneNumbers = when(type) {
            FeeType.NET_XUANCHENG -> null
            FeeType.ELECTRIC_XUANCHENG -> null
            FeeType.SHOWER_XUANCHENG -> phoneNumber
            FeeType.SHOWER_HEFEI -> phoneNumber
            FeeType.WASHING_HEFEI -> "未适配"
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> null
        }
        val buildings = when(type) {
            FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> building
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
                    FeeType.NET_XUANCHENG -> netValue.value = responseBody
                    FeeType.ELECTRIC_XUANCHENG ->  electricData.value = responseBody
                    FeeType.SHOWER_XUANCHENG -> showerData.value = responseBody
                    FeeType.ELECTRIC_HEFEI_UNDERGRADUATE -> hefeiElectric.value = responseBody
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
        huiXinBillResult : StateHolder<BillBean>,
        cardPredictedResponse : StateHolder<TotalResult>
    ) = withContext(Dispatchers.IO) {
        suspend fun reloadAllBills(origin: BillBean) {
            huiXinBillResult.clear()
            getCardBill(auth, page = 1, size = origin.total, huiXinBillResult)

            val newState = huiXinBillResult.state.first()
            if (newState is UiState.Success) {
                try {
                    val data = getConsumptionResult(newState.data)
                    cardPredictedResponse.emitData(data)
                } catch (e: Exception) {
                    cardPredictedResponse.emitError(e, PARSE_ERROR_CODE)
                }
            }
        }

        val currentState = huiXinBillResult.state.first()

        when (currentState) {
            is UiState.Success -> {
                val data = currentState.data
                if (data.size != data.total) {
                    reloadAllBills(data)
                }
            }

            else -> {
                // 第一次加载，拉取一条记录获取总数
                getCardBill(auth, page = 1, size = 1, huiXinBillResult)
                val stateAfterInit = huiXinBillResult.state.first()
                if (stateAfterInit is UiState.Success) {
                    reloadAllBills(stateAfterInit.data)
                }
            }
        }
    }
}