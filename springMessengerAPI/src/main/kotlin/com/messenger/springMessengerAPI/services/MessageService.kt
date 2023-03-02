package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.models.request.NewMessageRequest
import com.messenger.springMessengerAPI.models.request.UpdateMessageRequest
import com.messenger.springMessengerAPI.models.response.MessageResponse
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.repositories.MessageRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val conversationService: ConversationService,
    private val userService: UsersService
) {
    fun getAllMessagesForConversation(conversationId: Int): List<MessageResponse> {
        return messageRepository.findAllByConversationId(conversationId).map { mapMessageToMessageResponse(it) }
    }

    fun getAllMessagesForUser(userId: Int): List<MessageResponse> {
        val conversationsUserBelongsToo = conversationService.getConversationsForUser(userId)

        val messages = mutableListOf<MessageResponse>()
        for (conversation in conversationsUserBelongsToo) {
            messages += messageRepository.findAllByConversationId(conversationId = conversation.id)
                .map { mapMessageToMessageResponse(it) }
        }
        return messages
    }

    fun getAllMessagesForUserAfterDateTime(userId: Int, dateTime: LocalDateTime): List<MessageResponse> {
        return getAllMessagesForUser(userId).filter { it.timeSent > dateTime || (it.updatedTime != null && it.updatedTime > dateTime) }
    }

    fun newMessage(newMessageRequest: NewMessageRequest): MessageResponse {
        val message = messageRepository.save(
            Message(
                userId = newMessageRequest.userId,
                textMessage = newMessageRequest.message,
                timeSent = LocalDateTime.now(),
                conversationId = newMessageRequest.conversationId
            )
        )

        return MessageResponse(
            id = message.id,
            userId = message.userId,
            conversationId = message.conversationId,
            username = userService.findUsernameById(message.userId)!!,
            textMessage = message.textMessage,
            timeSent = message.timeSent,
            updatedTime = message.updatedTime
        )
    }

    fun updateMessage(messageToUpdate: UpdateMessageRequest) {
        val messageOld = messageRepository.findById(messageToUpdate.messageId)
        if (messageOld.isPresent) {
            messageRepository.save(
                Message(
                    //New values
                    textMessage = messageToUpdate.message,
                    updatedTime = LocalDateTime.now(),
                    //Keep the rest the same
                    id = messageOld.get().id,
                    userId = messageOld.get().userId,
                    timeSent = messageOld.get().timeSent,
                    conversationId = messageOld.get().conversationId
                )
            )
        }
    }

    fun deleteMessage(messageId: Int) {
        messageRepository.deleteById(messageId)
    }


    //Map to object that app expects
    fun mapMessageToMessageResponse(message: Message): MessageResponse {
        return MessageResponse(
            id = message.id,
            userId = message.userId,
            conversationId = message.conversationId,
            username = userService.findUsernameById(message.userId)!!,
            textMessage = message.textMessage,
            timeSent = message.timeSent,
            updatedTime = message.updatedTime
        )
    }
}