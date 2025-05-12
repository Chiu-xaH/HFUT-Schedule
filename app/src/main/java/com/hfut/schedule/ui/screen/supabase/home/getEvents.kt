package com.hfut.schedule.ui.screen.supabase.home

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.util.network.supabaseEventEntityToDto
import com.hfut.schedule.viewmodel.network.NetWorkViewModel

fun getEvents(vm: NetWorkViewModel) : List<SupabaseEventsInput> {
    val json = vm.supabaseGetEventsResp.value
    return try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        val newList = list.mapNotNull { item -> supabaseEventEntityToDto(item) }
        return newList
    } catch (e : Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun getInsertedEventId(vm: NetWorkViewModel) : Int? {
    val json = vm.supabaseAddResp.value?.second
    return try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
        if(list.size == 1) {
            list[0].id
        } else {
            null
        }
    } catch (e : Exception) {
        e.printStackTrace()
        null
    }
}

