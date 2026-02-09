package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.ChatResponse
import com.hfut.schedule.logic.enumeration.ChatModel
import com.hfut.schedule.logic.model.ChatRequest
import com.hfut.schedule.logic.network.api.AIService
import com.hfut.schedule.logic.network.api.JxglstuService
import com.hfut.schedule.logic.network.servicecreator.AIServiceCreator
import com.hfut.schedule.logic.network.util.launchRequestState
import com.hfut.schedule.logic.util.network.state.StateHolder

object AIRepository {

    private fun createService(): AIService {
        return AIServiceCreator.create(AIService::class.java, ChatModel.ChatAnywhere)
    }

    private var ai = createService()

    fun updateService() {
        ai = createService()
    }

    /*
    suspend fun chat(
        token : String,
        model : ChatModel,
        holder : StateHolder<ChatResponse>
    ) = launchRequestState(
        holder = holder,
        request = {
            ai.chat(
                "Bearer $token",
                ChatRequest()
            )
        }
    )
     */
}