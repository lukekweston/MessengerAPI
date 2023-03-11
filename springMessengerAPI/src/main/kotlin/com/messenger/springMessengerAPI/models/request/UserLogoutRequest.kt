package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UserLogoutRequest(
        @JsonProperty("userId")
        val userId: Int,
        @JsonProperty("username")
        val username: String,
)
