package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SuccessResponse(
    @JsonProperty("success")
    val success: Boolean = false,
)
