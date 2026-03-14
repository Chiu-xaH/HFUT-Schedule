package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.logic.enumeration.ChatModel
import com.hfut.schedule.logic.network.impl.AiServiceCreator
import com.hfut.schedule.network.api.AiService

object AiRepository {

    private fun createService(): AiService {
        return AiServiceCreator.create(AiService::class.java, ChatModel.ChatAnywhere)
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