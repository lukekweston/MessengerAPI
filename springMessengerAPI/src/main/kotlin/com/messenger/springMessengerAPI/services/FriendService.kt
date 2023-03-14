package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.Friend
import com.messenger.springMessengerAPI.models.FriendshipStatus
import com.messenger.springMessengerAPI.models.request.NewFriendRequest
import com.messenger.springMessengerAPI.models.request.UpdateFriendStatusRequest
import com.messenger.springMessengerAPI.models.response.ConversationResponse
import com.messenger.springMessengerAPI.models.response.FriendRequestResponse
import com.messenger.springMessengerAPI.models.response.FriendResponse
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.repositories.FriendRepository
import com.messenger.springMessengerAPI.repositories.UserConversationRepository
import com.messenger.springMessengerAPI.repositories.UsersRepository
import org.springframework.stereotype.Service


@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UsersRepository,
    private val conversationService: ConversationService,
    private val firebaseService: FirebaseService
) {

    fun findAllFriendsForAUser(userId: Int): List<FriendResponse> {
        return friendRepository.findAllBySelfUserId(userId).map {
            FriendResponse(
                friendUserId = it.friendUserid,
                friendUserName = userRepository.findUsersById(it.friendUserid)!!.username,
                friendshipStatus = it.status,
                conversationId =  conversationService.findAPrivateConversationForTwoUsers(userId, it.friendUserid)?.id
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


    /**
     * This method updates the friendship status of two people, and creates or deletes a conversation depending
     * if the friendship status is Friends or declined/removed
     *
     *
     * @param updateFriendStatusRequest - contains the friends to be updated and the new status
     * @return Success response true if method is completed without error, else false
     */
    //Todo - this method only supports FriendshipStatus of Friends, declined, removed - in the future can do more statuses
    fun updateFriendship(updateFriendStatusRequest: UpdateFriendStatusRequest): ConversationResponse {
        try {
            val userSelf = userRepository.findUsersById(updateFriendStatusRequest.selfUserId)
            val userTo = userRepository.findUsersByUsernameOrUserEmail(updateFriendStatusRequest.friendUsername)

            var conversation: Conversation? = null

            val newFriendshipStatus = FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus)

            //If friendship status is declined ore removed, delete the entry
            //Delete existing conversation and messages if they exist
            if (newFriendshipStatus == FriendshipStatus.Declined || newFriendshipStatus == FriendshipStatus.Removed
            ) {
                val selfToFriend = friendRepository.findBySelfUserIdAndFriendUserid(userSelf!!.id, userTo!!.id)
                friendRepository.delete(selfToFriend!!)
                val friendToSelf = friendRepository.findBySelfUserIdAndFriendUserid(userTo!!.id, userSelf!!.id)
                friendRepository.delete(friendToSelf!!)


                val existingConversation = conversationService.findAPrivateConversationForTwoUsers(userSelf.id, userTo.id)

                //If the conversation exists, delete the data
                if(existingConversation != null){
                    //Delete userConversations
                    conversationService.deleteAllDataToDoWithConversation(existingConversation)
                }



            }
            //Friends - so create a new relationship and create a new conversation for these two friends
            else if (newFriendshipStatus == FriendshipStatus.Friends) {
                //Update the self To Friend relationship
                val selfToFriend = friendRepository.findBySelfUserIdAndFriendUserid(userSelf!!.id, userTo!!.id)
                selfToFriend!!.status = newFriendshipStatus
                friendRepository.save(selfToFriend)

                //Update the Friend to self relationship
                val friendToSelf = friendRepository.findBySelfUserIdAndFriendUserid(userTo!!.id, userSelf!!.id)
                friendToSelf!!.status = newFriendshipStatus
                friendRepository.save(friendToSelf)


                //Create a new conversation
                conversation = conversationService.getOrStartAPrivateConversationForTwoUsers(userSelf, userTo)
            }



            firebaseService.sendFriendStatusUpdate(
                userSelf!!,
                userTo!!,
                FriendshipStatus.valueOf(updateFriendStatusRequest.friendshipStatus),
                conversation
            )

            if(conversation != null){
                return ConversationResponse( id = conversation.id,
                    conversationName = conversationService.getConversationName(conversation, userSelf.id),
                    lastUpdated = null
                )
            }
            return ConversationResponse()

        } catch (e: Exception) {
            println("Error updating friend status")
            println(e.stackTrace)
            println(e.message)
            return ConversationResponse(success = false)
        }

    }


}