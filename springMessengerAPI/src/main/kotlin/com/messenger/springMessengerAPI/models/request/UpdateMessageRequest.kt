package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateMessageRequest (
    @JsonProperty("messageId")
    val messageId: Int,
    @JsonProperty("message")
    val message: String

)