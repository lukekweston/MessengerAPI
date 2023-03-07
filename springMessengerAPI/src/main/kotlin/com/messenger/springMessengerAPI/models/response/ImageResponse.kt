package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ImageResponse(
    @JsonProperty("messageId")
    val messageId: Int,
    @JsonProperty("imageBase64")
    val imageBase64: String,
)
