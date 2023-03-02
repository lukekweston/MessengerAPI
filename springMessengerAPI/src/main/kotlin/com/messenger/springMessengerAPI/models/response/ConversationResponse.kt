package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ConversationResponse(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("conversationName")
    val conversationName: String?,
    @JsonProperty("lastUpdated")
    val lastUpdated: LocalDateTime?,
)
