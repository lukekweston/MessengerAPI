package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UsersForConversationRequest (
    @JsonProperty("userIdSelf")
    val userIdSelf: Int,
    @JsonProperty("otherUserId")
    val otherUserId: Int,
)