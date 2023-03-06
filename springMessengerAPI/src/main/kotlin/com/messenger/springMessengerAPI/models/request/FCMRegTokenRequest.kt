package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class FCMRegTokenRequest(
        @JsonProperty("userId")
        val userId: Int,
        @JsonProperty("firebaseRegistrationToken")
        val firebaseRegistrationToken: String,
)
