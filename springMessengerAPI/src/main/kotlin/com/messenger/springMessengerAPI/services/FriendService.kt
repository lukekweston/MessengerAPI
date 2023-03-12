package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Friend
import com.messenger.springMessengerAPI.models.FriendshipStatus
import com.messenger.springMessengerAPI.models.request.NewFriendRequest
import com.messenger.springMessengerAPI.models.request.UpdateFriendStatusRequest
import com.messenger.springMessengerAPI.models.response.FriendRequestResponse
import com.messenger.springMessengerAPI.models.response.FriendResponse
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.repositories.FriendRepository
import com.messenger.springMessengerAPI.repositories.UsersRepository
import org.springframework.stereotype.Service


@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UsersRepository,
    private val firebaseService: FirebaseService
) {

    fun findAllFriendsForAUser(userId: Int): List<FriendResponse> {
        return friendRepository.findAllBySelfUserId(userId).map{
            FriendResponse(
                friendUserId = it.friendUserid,
                friendUserName = userRepository.findUsersById(it.friendUserid)!!.username,
                friendshipStatus = it.status
            )
        }
    }

    fun sendNewFriendRequest(friendRequest: NewFriendRequest): FriendRequestResponse {

        //Email has to have @ char, username cannot contain @ char
        val friend = userRepository.findUsersByUsernameOrUserEmail(friendRequest.usernameOrEmail)

        //Return userNameOrEmail not found
        //Cannot send a friend request to yourself
        if (friend == null || friend.id == friendRequest.selfUserId) {
            return FriendRequestResponse(
                friendRequestSent = false,
                usernameOrEmailNotFound = true,
                friendRequestAlreadySent = false,
                alreadyFriends = false,
                friendUserId = null,
                friendUserName = null
            )
        }

        //Check that the relationship doesn't already exist
        val existingRequest = friendRepository.findBySelfUserIdAndFriendUserid(friendRequest.selfUserId, friend.id)

        //Resend if there is a declined friend status
        if (existingRequest != null && existingRequest.status != FriendshipStatus.Declined) {
            //Check if there is already a pending friend request
            if (existingRequest.status == FriendshipStatus.Sent) {
                return FriendRequestResponse(
                    friendRequestSent = false,
                    usernameOrEmailNotFound = false,
                    friendRequestAlreadySent = true,
                    alreadyFriends = false,
                    friendUserId = null,
                    friendUserName = null
                )
            }
            //Check if already friends
            if (existingRequest.status == FriendshipStatus.Friends) {
                return FriendRequestResponse(
                    friendRequestSent = false,
                    usernameOrEmailNotFound = false,
                    friendRequestAlreadySent = true,
                    alreadyFriends = false,
                    friendUserId = null,
                    friendUserName = null
                )
            }

            //Todo, handle other cases - blocked, Declined many times...
            //Will make it appear in the ui that the friend request has been sent currently
            return FriendRequestResponse(
                friendRequestSent = true,
                usernameOrEmailNotFound = false,
                friendRequestAlreadySent = false,
                alreadyFriends = false,
                friendUserId = null,
                friendUserName = null
            )
        } else {
            //Create new friends
            friendRepository.save(
                Friend(
                    selfUserId = friendRequest.selfUserId,
                    friendUserid = friend.id,
                    status = FriendshipStatus.Sent
                )
            )
            friendRepository.save(
                Friend(
                    selfUserId = friend.id,
                    friendUserid = friendRequest.selfUserId,
                    status = FriendshipStatus.Pending
                )
            )

            //Send the friend request to the other user
            firebaseService.sendFriendRequest(friendRequest.selfUserId, friend.id)


            //Return successful friend send
            return FriendRequestResponse(
                friendRequestSent = true,
                usernameOrEmailNotFound = false,
                friendRequestAlreadySent = false,
                alreadyFriends = false,
                friendUserId = friend.id,
                friendUserName = friend.username
            )


        }
    }


    fun updateFriendship(updateFriendStatusRequest: UpdateFriendStatusRequest): SuccessResponse{
        try {
            val userSelf = userRepository.findUsersById(updateFriendStatusRequest.selfUserId)
            val userTo = userRepository.findUsersByUsernameOrUserEmail(updateFriendStatusRequest.friendUsername)

            //If friendship status is declined, delete the entry
            if(FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus) == FriendshipStatus.Declined) {
                val selfToFriend = friendRepository.findBySelfUserIdAndFriendUserid(userSelf!!.id, userTo!!.id)
                friendRepository.delete(selfToFriend!!)
                val friendToSelf = friendRepository.findBySelfUserIdAndFriendUserid(userTo!!.id, userSelf!!.id)
                friendRepository.delete(friendToSelf!!)

            }
            //Else update exising entries
            else{
                //Update the self To Friend relationship
                val selfToFriend = friendRepository.findBySelfUserIdAndFriendUserid(userSelf!!.id, userTo!!.id)
                selfToFriend!!.status = FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus)
                friendRepository.save(selfToFriend)

                //Update the Friend to self relationship
                val friendToSelf = friendRepository.findBySelfUserIdAndFriendUserid(userTo!!.id, userSelf!!.id)
                friendToSelf!!.status = FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus)
                friendRepository.save(friendToSelf)
            }

            firebaseService.sendFriendStatusUpdate(userSelf, userTo, FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus))

            return SuccessResponse(true)
        }
        catch (e: Exception){
            println("Error updating friend status")
            println(e.stackTrace)
            println(e.message)
            return SuccessResponse(false)
        }

    }


}