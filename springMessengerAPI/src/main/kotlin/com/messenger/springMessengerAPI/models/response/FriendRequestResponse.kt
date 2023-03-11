package com.messenger.springMessengerAPI.models.response

import com.fasterxml.jackson.annotation.JsonProperty


data class FriendRequestResponse(
    @JsonProperty("friendRequestSent")
    val friendRequestSent: Boolean,
    @JsonProperty("usernameOrEmailNotFound")
    val usernameOrEmailNotFound: Boolean,
    @JsonProperty("friendRequestAlreadySent")
    val friendRequestAlreadySent: Boolean,
    @JsonProperty("alreadyFriends")
    val alreadyFriends: Boolean,
    @JsonProperty("friendUserId")
    val friendUserId: Int?,
    @JsonProperty("friendUserName")
    val friendUserName: String?,
)
