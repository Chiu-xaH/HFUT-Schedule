package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.logic.enumeration.ChatModel
import com.hfut.schedule.logic.network.api.AIService
import com.hfut.schedule.logic.network.servicecreator.AIServiceCreator

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