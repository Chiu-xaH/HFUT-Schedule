package com.hfut.schedule.network.api.repo

import com.google.gson.JsonObject
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.network.api.model.response.dto.SchoolNetInfo
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinFeeType
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinHefeiBuilding
import com.hfut.schedule.network.api.model.response.json.huixin.HuiXinMonthBill
import com.xah.common.logic.model.HuiXinBill
import com.xah.common.logic.state.UiStateHolder

interface HuiXinRepositoryInf {
    suspend fun getCardBill(
        auth : String,
        page : Int,
        size : Int = Constant.DEFAULT_PAGE_SIZE,
        holder : UiStateHolder<HuiXinBill>
    )
    /*
    fun getHuiXinCardInfo(auth : String,huiXinCardInfoResponse : MutableLiveData<String?>)
     */
    suspend fun checkHuiXinLogin(auth : String,holder : UiStateHolder<Boolean>)
    suspend fun huiXinSingleLogin(studentId : String,password: String,holder : UiStateHolder<String>)
    suspend fun payStep1(auth: String, json: String, pay : Float, type: HuiXinFeeType, holder : UiStateHolder<String>)
    suspend fun payStep2(auth: String, orderId : String, type : HuiXinFeeType, holder : UiStateHolder<Map<String, String>>)
    suspend fun payStep3(auth: String, orderId : String, password : String, uuid : String, type: HuiXinFeeType, holder : UiStateHolder<String>)
    suspend fun changeLimit(auth: String, json: JsonObject, holder : UiStateHolder<String>)
    suspend fun searchDate(auth : String, timeFrom : String, timeTo : String,holder : UiStateHolder<Float>)
    suspend fun searchBills(auth : String, info: String,page : Int,holder : UiStateHolder<HuiXinBill>)
    suspend fun getMonthBills(auth : String, dateStr: String,holder : UiStateHolder<List<HuiXinMonthBill>>)
    suspend fun getHefeiRooms(
        auth: String,
        building: String?,
        holder: UiStateHolder<List<HuiXinHefeiBuilding>>
    )
    suspend fun getSchoolNetInfo(auth: String, holder: UiStateHolder<SchoolNetInfo>)
    /*
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
    )
    suspend fun getCardPredicted(
        auth: String,
        huiXinBillResult : UiStateHolder<HuiXinBill>,
        cardPredictedResponse : UiStateHolder<TotalResult>
    )
     */
}
