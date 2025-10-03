package com.hfut.schedule.logic.network.repo

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventForkCount
import com.hfut.schedule.logic.model.SupabaseEventOutput
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.model.SupabaseLoginResponse
import com.hfut.schedule.logic.model.SupabaseRefreshLoginBean
import com.hfut.schedule.logic.model.SupabaseUserLoginBean
import com.hfut.schedule.logic.network.api.SupabaseService
import com.hfut.schedule.logic.network.servicecreator.SupabaseServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestNone
import com.hfut.schedule.logic.network.util.launchRequestSimple
import com.hfut.schedule.logic.network.util.makeRequest
import com.hfut.schedule.logic.network.util.supabaseEventDtoToEntity
import com.hfut.schedule.logic.network.util.supabaseEventEntityToDto
import com.hfut.schedule.logic.network.util.supabaseEventForkDtoToEntity
import com.hfut.schedule.logic.util.network.state.StateHolder
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.storage.SharedPrefs.saveString
import com.hfut.schedule.ui.screen.supabase.login.getSchoolEmail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse

object SupabaseRepository {
    private val supabase = SupabaseServiceCreator.create(SupabaseService::class.java)

    suspend fun getTodayVisit(holder : StateHolder<Int>) = launchRequestSimple(
        holder = holder,
        request = { supabase.getTodayVisitCount().awaitResponse() },
        transformSuccess = { _, body -> parseTodayVisit(body) }
    )
    suspend fun getUserCount(holder : StateHolder<Int>) = launchRequestSimple(
        holder = holder,
        request = { supabase.getUserCount().awaitResponse() },
        transformSuccess = { _, body -> parseTodayVisit(body) }
    )
    @JvmStatic
    private fun parseTodayVisit(body : String) : Int = try {
        body.toInt()
    } catch (e : Exception) { throw e }

    fun supabaseReg(password: String,supabaseRegResp : MutableLiveData<String?>) = makeRequest(
        supabase.reg(user = SupabaseUserLoginBean(password = password)),
        supabaseRegResp
    )

    suspend fun supabaseLoginWithPassword(password : String,holder : StateHolder<SupabaseLoginResponse>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.login(
                    user = SupabaseUserLoginBean(password = password),
                    loginType = "password"
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseRefreshTokenSupabase(json) }
        )

    suspend fun supabaseLoginWithRefreshToken(refreshToken : String,holder : StateHolder<SupabaseLoginResponse>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.login(
                    user = SupabaseRefreshLoginBean(refreshToken),
                    loginType = "refresh_token"
                ).awaitResponse()
            },
            transformSuccess = { _, json -> parseRefreshTokenSupabase(json) }
        )
    @JvmStatic
    private fun parseRefreshTokenSupabase(json : String) : SupabaseLoginResponse = try {
        Gson().fromJson(json, SupabaseLoginResponse::class.java)
    } catch (e : Exception) { throw e }


    suspend fun supabaseDel(jwt : String,id : Int,holder : StateHolder<Boolean>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.delEvent(authorization = "Bearer $jwt", id = "eq.$id").awaitResponse()
            },
            transformSuccess = { _, _ -> true }
        )

    fun supabaseGetEvents(supabaseGetEventsResp : MutableLiveData<String?>) =
        makeRequest(supabase.getEvents(), supabaseGetEventsResp)

    fun supabaseAdd(jwt: String,event : SupabaseEventOutput,supabaseAddResp : MutableLiveData<Pair<Boolean,String?>?>) {
        val call = supabase.addEvent(authorization = "Bearer $jwt",entity = supabaseEventDtoToEntity(event))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseAddResp.value = Pair(response.isSuccessful,response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    fun supabaseAddCount(jwt: String,eventId : Int,supabaseAddCountResp : MutableLiveData<Boolean?>) {
        val call = supabase.eventDownloadAdd(authorization = "Bearer $jwt",entity = supabaseEventForkDtoToEntity(eventId))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                supabaseAddCountResp.value = response.isSuccessful
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    fun supabaseGetEventForkCount(jwt: String, eventId: Int,eventForkCountCache: SnapshotStateMap<Int, String>) {
        if(eventForkCountCache.containsKey(eventId)) {
            return
        }
        val call = supabase.getEventDownloadCount(authorization = "Bearer $jwt", entity = SupabaseEventForkCount(eventId = eventId))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    val count = response.body()?.string()
                    count?.let { eventForkCountCache[eventId] = count }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
        })
    }

    suspend fun supabaseGetEventCount(jwt: String,holder : StateHolder<String?>) =
        launchRequestSimple(
            holder = holder,
            request = { supabase.getEventCount(authorization = "Bearer $jwt").awaitResponse() },
            transformSuccess = { _, body -> body }
        )

    suspend fun supabaseGetEventLatest(jwt: String,holder : StateHolder<Boolean>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.getEventLatestTime(authorization = "Bearer $jwt").awaitResponse()
            },
            transformSuccess = { _, body -> parseSupabaseLatestEventTime(body) }
        )
    @JvmStatic
    private fun parseSupabaseLatestEventTime(body : String) : Boolean {
        try {
            if(prefs.getString("SUPABASE_LATEST",null) == body) {
                // 没有新的日程
                return false
            } else {
                // 有新的日程
                // 保存
                saveString("SUPABASE_LATEST",body)
                return true
            }
        } catch (e : Exception) { throw e }
    }

    suspend fun supabaseGetMyEvents(holder : StateHolder<List<SupabaseEventsInput>>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.getEvents(endTime = null, email = "eq." + getSchoolEmail()).awaitResponse()
            },
            transformSuccess = { _, json -> parseSupabaseMyEvents(json) }
        )
    private fun parseSupabaseMyEvents(json : String) : List<SupabaseEventsInput> = try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        list.mapNotNull { item -> supabaseEventEntityToDto(item) }
    } catch(e : Exception) { throw e }


    suspend fun supabaseCheckJwt(jwt: String,holder : StateHolder<Boolean>) = launchRequestSimple(
        holder = holder,
        request = { supabase.checkToken(authorization = "Bearer $jwt").awaitResponse() },
        transformSuccess = { _, _ -> true }
    )

    suspend fun supabaseUpdateEvent(jwt: String, id: Int, body : Map<String,Any>,holder : StateHolder<Boolean>) =
        launchRequestSimple(
            holder = holder,
            request = {
                supabase.updateEvent(
                    authorization = "Bearer $jwt",
                    id = "eq.$id",
                    body = body
                ).awaitResponse()
            },
            transformSuccess = { _, _ -> true }
        )


    suspend fun postUser() = launchRequestNone {
        supabase.postUsage().awaitResponse()
    }

}