package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.messenger.springMessengerAPI.models.FriendshipStatus

data class FriendResponse(
    @JsonProperty("friendUserId")
    val friendUserId: Int,
    @JsonProperty("friendUserName")
    val friendUserName: String,
    @JsonProperty("friendshipStatus")
    val friendshipStatus: FriendshipStatus

)