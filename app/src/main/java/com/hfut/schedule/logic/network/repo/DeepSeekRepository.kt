package com.hfut.schedule.logic.network.repo

import com.hfut.schedule.logic.model.enumeration.ChatModel
import com.hfut.schedule.logic.model.enumeration.DeepSeekModel
import com.hfut.schedule.logic.network.impl.AiServiceCreator
import com.hfut.schedule.logic.util.network.launchRequestState
import com.hfut.schedule.network.api.inf.AiService
import com.hfut.schedule.network.api.model.request.ai.AiChatRequest
import com.hfut.schedule.network.api.model.response.json.ai.AiChatChoice
import com.hfut.schedule.network.api.model.response.json.ai.AiChatMsg
import com.hfut.schedule.network.api.model.response.json.ai.AiChatResponse
import com.hfut.schedule.network.api.repo.DeepSeekRepositoryInf
import com.hfut.schedule.network.core.GsonInstance
import com.xah.common.logic.state.UiStateHolder

object DeepSeekRepository : DeepSeekRepositoryInf {

    private val deepSeek = AiServiceCreator.create(AiService::class.java, ChatModel.DeepSeek)

    suspend fun chat(
        apiKey : String,
        msgs : List<AiChatMsg>,
        model : DeepSeekModel = DeepSeekModel.FLASH,
        holder : UiStateHolder<List<AiChatChoice>>
    ) = launchRequestState(
        holder = holder,
        request = {
            deepSeek.chat(
                "Bearer $apiKey",
                AiChatRequest(model.modelName, msgs)
            )
        },
        transformSuccess = { _,json -> parseChat(json) }
    )

    @JvmStatic
    private fun parseChat(json : String) = try {
        GsonInstance.fromJson(json,AiChatResponse::class.java).choices
    } catch (e : Exception) { throw e }
}