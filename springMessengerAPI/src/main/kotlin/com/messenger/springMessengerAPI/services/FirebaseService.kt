package com.messenger.springMessengerAPI.services

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service


@Service
class FirebaseService(
    private val fcm: FirebaseMessaging,
    private val userConversationService: UserConversationService,
    private val usersService: UsersService
) {

    fun sendMessageToClients(message: com.messenger.springMessengerAPI.models.Message) {

//        get all the other users in the conversation that this message is for
        val usersToSendMessageTo =
            userConversationService.findAllOtherUserIdsForConversation(message.userId, message.conversationId).filter{ !it.firebaseRegToken.isNullOrEmpty() }

        //If no users can be sent a notification, return
        if(usersToSendMessageTo.isNullOrEmpty()){
            return
        }


        val usernameSending = usersService.findUsernameById(message.userId)

        val data = mapOf(
            "type" to "newMessage",
            "id" to message.id.toString(),
            "userId" to message.userId.toString(),
            "textMessage" to message.textMessage,
            "timeSent" to message.timeSent.toString(),
            "updatedTime" to message.updatedTime.toString(),
            "conversationId" to message.conversationId.toString(),
            "usernameSending" to usernameSending
        )


        val msg = MulticastMessage.builder()
            .putData("body", message.textMessage)
            .putAllData(data)
            .addAllTokens(usersToSendMessageTo.map { it.firebaseRegToken })
            .build()

        fcm.sendMulticast(msg)

    }
}