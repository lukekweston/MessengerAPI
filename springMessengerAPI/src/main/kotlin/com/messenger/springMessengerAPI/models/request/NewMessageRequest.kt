package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class NewMessageRequest (
    @JsonProperty("userId")
    val userId: Int,
    @JsonProperty("conversationId")
    val conversationId: Int,
    @JsonProperty("message")
    val message: String

)