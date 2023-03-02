package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UserLoginRequest(
        @JsonProperty("userName")
        val userName: String,
        @JsonProperty("password")
        val password: String
)
