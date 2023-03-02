package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MessageResponse(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("conversationId")
    val conversationId: Int,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("message")
    val textMessage: String?,
    @JsonProperty("timeSent")
    val timeSent: LocalDateTime,
    @JsonProperty("timeUpdated")
    val updatedTime: LocalDateTime?,
    )
