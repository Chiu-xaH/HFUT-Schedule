package com.hfut.schedule.network.api.repo

import com.hfut.schedule.network.api.model.response.json.supabase.SupabaseLoginResponse
import com.xah.common.logic.state.UiStateHolder

interface SupabaseRepositoryInf {
    suspend fun getTodayVisit(holder : UiStateHolder<Int>)
    suspend fun getUserCount(holder : UiStateHolder<Int>)
    /*
    fun supabaseReg(password: String,supabaseRegResp : MutableLiveData<String?>)
     */
    suspend fun supabaseLoginWithPassword(password : String,holder : UiStateHolder<SupabaseLoginResponse>)
    suspend fun supabaseLoginWithRefreshToken(refreshToken : String,holder : UiStateHolder<SupabaseLoginResponse>)
    suspend fun supabaseDel(jwt : String,id : Int,holder : UiStateHolder<Boolean>)
    /*
    fun supabaseGetEvents(supabaseGetEventsResp : MutableLiveData<String?>)
    fun supabaseAdd(jwt: String,event : SupabaseEventOutput,supabaseAddResp : MutableLiveData<Pair<Boolean,String?>?>) {
    fun supabaseAddCount(jwt: String,eventId : Int,supabaseAddCountResp : MutableLiveData<Boolean?>) {
    fun supabaseGetEventForkCount(jwt: String, eventId: Int,eventForkCountCache: SnapshotStateMap<Int, String>)
     */
    suspend fun supabaseGetEventCount(jwt: String,holder : UiStateHolder<String?>)
    suspend fun supabaseGetEventLatest(jwt: String,holder : UiStateHolder<Boolean>)
    /*
    suspend fun supabaseGetMyEvents(holder : UiStateHolder<List<SupabaseEventsInput>>)
     */
    suspend fun supabaseCheckJwt(jwt: String,holder : UiStateHolder<Boolean>)
    suspend fun supabaseUpdateEvent(jwt: String, id: Int, body : Map<String,Any>,holder : UiStateHolder<Boolean>)
    suspend fun postUser(): Int
}