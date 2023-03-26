package com.messenger.springMessengerAPI.models.request

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateFriendStatusRequest(
    @JsonProperty("selfUserId")
    val selfUserId: Int,
    @JsonProperty("friendUsername")
    val friendUsername: String,
    @JsonProperty("friendshipStatus")
    val friendshipStatus: String
)
