package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.Friend
import com.messenger.springMessengerAPI.models.request.NewFriendRequest
import com.messenger.springMessengerAPI.models.request.UpdateFriendStatusRequest
import com.messenger.springMessengerAPI.models.response.FriendRequestResponse
import com.messenger.springMessengerAPI.models.response.FriendResponse
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.services.FriendService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class FriendController(private val friendService: FriendService) {

    @GetMapping("/findAllFriendsForUser/{userId}")
    fun findAllFriendsForUser(@PathVariable userId: Int): List<FriendResponse> {
        return friendService.findAllFriendsForAUser(userId)
    }


    @PostMapping("/sendFriendRequest")
    fun sendNewFriendRequest(@RequestBody newFriendRequest: NewFriendRequest): FriendRequestResponse {
        return friendService.sendNewFriendRequest(newFriendRequest)
    }

    @PostMapping("/updateFriendStatus")
    fun updateFriendStatus(@RequestBody updateFriendshipRequest: UpdateFriendStatusRequest): SuccessResponse{
        return friendService.updateFriendship(updateFriendshipRequest)
    }



}




