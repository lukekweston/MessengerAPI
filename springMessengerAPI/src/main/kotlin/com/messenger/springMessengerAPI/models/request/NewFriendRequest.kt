package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class NewFriendRequest(
    @JsonProperty("selfUserId")
    val selfUserId: Int,
    @JsonProperty("usernameOrEmail")
    val usernameOrEmail: String
)
