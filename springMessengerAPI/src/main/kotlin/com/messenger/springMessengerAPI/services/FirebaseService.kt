package com.messenger.springMessengerAPI.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.FriendshipStatus
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.repositories.UsersRepository
import org.springframework.stereotype.Service


@Service
class FirebaseService(
    private val fcm: FirebaseMessaging,
    private val userConversationService: UserConversationService,
    private val usersService: UsersService,
    private val conversationService: ConversationService,
    private val usersRepository: UsersRepository
) {


    /**
     * This Function takes a newly created "message" and sends the message as fcm.message to all the other participants in the chat
     * @param message the newly created message
     */
    fun sendMessageToClients(message: com.messenger.springMessengerAPI.models.Message) {

//        get all the other users in the conversation that this message is for
        val usersToSendMessageTo =
            userConversationService.findAllOtherUserIdsForConversation(message.userId, message.conversationId)
                .filter { !it.firebaseRegToken.isNullOrEmpty() }

        //If no users can be sent a notification, return
        if (usersToSendMessageTo.isNullOrEmpty()) {
            return
        }


        val usernameSending = usersService.findUsernameById(message.userId)

        val type = if (message.imagePathFullRes == null) "newMessage" else "newImageMessage"

        val data = mapOf(
            "type" to type,
            "id" to message.id.toString(),
            "userId" to message.userId.toString(),
            "textMessage" to message.textMessage,
            "timeSent" to message.timeSent.toString(),
            "updatedTime" to message.updatedTime.toString(),
            "conversationId" to message.conversationId.toString(),
            "usernameSending" to usernameSending,
        )


        val msg = MulticastMessage.builder()
            .putData("body", message.textMessage)
            .putAllData(data)
            .addAllTokens(usersToSendMessageTo.map { it.firebaseRegToken })
            .build()

        fcm.sendMulticast(msg)

    }


    fun sendFriendRequest(fromUserId: Int, toUserId: Int) {
        val fromUser = usersRepository.findUsersById(fromUserId)
        val toUser = usersRepository.findUsersById(toUserId)

        //Return if toUser is not logged in on a device
        if (toUser!!.firebaseRegToken.isNullOrEmpty()) {
            return
        }


        val data = mapOf(
            "title" to fromUser!!.username + " sent you a friend request",
            "body" to "Accept to start chatting",
            "type" to "friendRequest",
            "fromUserId" to fromUser.id.toString(),
            "fromUserName" to fromUser.username,
            "toUserId" to toUser!!.id.toString(),
        )


        val msg = Message.builder()
            .putAllData(data)
            .setToken(toUser.firebaseRegToken)
            .build()

        fcm.send(msg)

    }

    fun sendFriendStatusUpdate(
        userFrom: User,
        userTo: User,
        friendshipStatus: FriendshipStatus,
        conversation: Conversation?
    ) {

        //Return if toUser is not logged in on a device
        if (userTo!!.firebaseRegToken.isNullOrEmpty()) {
            return
        }

        val data = mapOf(
            "title" to if (friendshipStatus == FriendshipStatus.Friends) "${userTo.username} accepted your friend request!" else "",
            "body" to "You can now start chatting",
            "type" to "friendStatusUpdate",
            "status" to friendshipStatus.toString(),
            "fromUserId" to userFrom.id.toString(),
            "fromUserName" to userFrom.username,
            "toUserId" to userTo!!.id.toString(),
            "conversationId" to (conversation?.id.toString() ?: ""),
            "conversationName" to if (conversation != null) conversationService.getConversationName(
                conversation,
                userTo.id
            ) else ""
        )


        val msg = Message.builder()
            .putAllData(data)
            .setToken(userTo.firebaseRegToken)
            .build()

        fcm.send(msg)

    }
}