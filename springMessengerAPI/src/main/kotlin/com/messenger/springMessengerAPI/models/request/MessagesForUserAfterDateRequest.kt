package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MessagesForUserAfterDateRequest(
        @JsonProperty("userId")
        val userId: Int,
        @JsonProperty("lastUpdateDateTime")
        val lastUpdateDateTime: LocalDateTime
)
