package com.hfut.schedule.ui.screen.supabase.home

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfut.schedule.logic.model.SupabaseEventEntity
import com.hfut.schedule.logic.model.SupabaseEventsInput
import com.hfut.schedule.logic.network.util.supabaseEventEntityToDto
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.xah.uicommon.util.LogUtil

fun getEvents(vm: NetWorkViewModel) : List<SupabaseEventsInput> {
    val json = vm.supabaseGetEventsResp.value
    return try {
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
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
        val list : List<SupabaseEventEntity> = Gson().fromJson(json,object : TypeToken<List<SupabaseEventEntity>>() {}.type)
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

