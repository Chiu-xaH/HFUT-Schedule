package com.hfut.schedule.ui.screen.supabase.home


import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.util.network.supabaseEventEntityToDto
import com.hfut.schedule.network.util.GsonInstance
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.common.logic.util.LogUtil

fun getEvents(vm: NetWorkViewModel) : List<SupabaseEventsInput> {
    val json = vm.supabaseGetEventsResp.value
    return try {
        val list : List<SupabaseEventEntity> = GsonInstance.fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        val newList = list.mapNotNull { item -> supabaseEventEntityToDto(item) }
        return newList
    } catch (e : Exception) {
        LogUtil.error(e)
        emptyList()
    }
}

fun getInsertedEventId(vm: NetWorkViewModel) : Int? {
    val json = vm.supabaseAddResp.value?.second
    return try {
        val list : List<SupabaseEventEntity> = GsonInstance.fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        if(list.size == 1) {
            list[0].id
        } else {
            null
        }
    } catch (e : Exception) {
        LogUtil.error(e)
        null
    }
}

