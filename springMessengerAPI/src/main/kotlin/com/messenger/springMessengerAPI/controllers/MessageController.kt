package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.models.request.MessagesForUserAfterDateRequest
import com.messenger.springMessengerAPI.models.request.NewMessageRequest
import com.messenger.springMessengerAPI.models.request.UpdateMessageRequest
import com.messenger.springMessengerAPI.models.response.MessageResponse
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.services.ConversationService
import com.messenger.springMessengerAPI.services.MessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class MessageController(private val messageService: MessageService) {
    @GetMapping("/getAllMessagesForConversation/{conversationId}")
    fun getAllMessagesForConversation(@PathVariable conversationId: Int): List<MessageResponse> =
            messageService.getAllMessagesForConversation(conversationId = conversationId)

    @GetMapping("/allMessagesForUser/{userId}")
    fun getAllMessagesForUser(@PathVariable userId: Int): List<MessageResponse> =
            messageService.getAllMessagesForUser(userId)

    @GetMapping("/getMessagesAfter")
    fun getAllMessagesForUserAfterDateTime(@RequestBody messagesForUserAfterDateRequest: MessagesForUserAfterDateRequest): List<MessageResponse> =
            messageService.getAllMessagesForUserAfterDateTime(messagesForUserAfterDateRequest.userId, messagesForUserAfterDateRequest.lastUpdateDateTime)


    @PostMapping("/sendMessage")
    fun sendMessage(@RequestBody messageRequest: NewMessageRequest): MessageResponse {
        return messageService.newMessage(messageRequest)
    }

    //This method should really have auth as only the user who created this message should be able to update it
    @PutMapping("/updateMessage")
    fun updateMessage(@RequestBody messageRequest: UpdateMessageRequest) = messageService.updateMessage(messageRequest)


    //This method should really have auth as only the user who created this message should be able to delete it
    @DeleteMapping("/deleteMessage/{messageId}")
    fun deleteMessage(@PathVariable messageId: Int) = messageService.deleteMessage(messageId)

}