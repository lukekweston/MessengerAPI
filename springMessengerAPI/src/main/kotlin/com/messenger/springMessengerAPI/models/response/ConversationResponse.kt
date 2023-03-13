package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ConversationResponse(
    @JsonProperty("id")
    val id: Int? = null,
    @JsonProperty("conversationName")
    val conversationName: String? = null,
    @JsonProperty("lastUpdated")
    val lastUpdated: LocalDateTime? = null,
    @JsonProperty("success")
    val success: Boolean = true
)
