package com.hfut.schedule.logic.network.interceptor

import kotlinx.coroutines.flow.MutableStateFlow

object GoToInterceptorState {
    var toOneCode = MutableStateFlow<String?>(null)
    var toCommunityTicket = MutableStateFlow<String?>(null)
}