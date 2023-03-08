package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Message
import com.messenger.springMessengerAPI.models.request.NewMessageRequest
import com.messenger.springMessengerAPI.models.request.UpdateMessageRequest
import com.messenger.springMessengerAPI.models.response.ImageResponse
import com.messenger.springMessengerAPI.models.response.MessageResponse
import com.messenger.springMessengerAPI.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

//import org.apache.commons.codec.binary.Base64
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val conversationService: ConversationService,
    private val userService: UsersService,
    private val firebaseService: FirebaseService,

    @Value("\${app.upload-full-res.dir}")
    private val fullResDirectory: String,

    @Value("\${app.upload-low-res.dir}")
    private val lowResDirectory: String

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
                conversationId = newMessageRequest.conversationId,
            )
        )

        //Save the images to files using the newly created messageRow as the id of the images
        if(newMessageRequest.imageBase64FullRes != null) {
            //Save the full image and compressed images in their directories, update the value on message
            message.imagePathFullRes = saveImage(newMessageRequest.imageBase64FullRes, message.id, true)
        }
        
        if(newMessageRequest.imageBase64LowRes != null) {
            message.imagePathLowRes = saveImage(newMessageRequest.imageBase64LowRes, message.id, false)
            messageRepository.save(message)
        }

        firebaseService.sendMessageToClients(message)

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
            updatedTime = message.updatedTime,
            imageLowRes = if(message.imagePathLowRes != null) readFileAsBase64(message.imagePathLowRes!!) else null

        )
    }

    private fun saveImage(imageData: String, id: Int, fullRes: Boolean): String{

        var imagePath = if(fullRes){
            "$fullResDirectory/$id.jpg"
        } else{
            "$lowResDirectory/$id.jpg"
        }

        val data = org.apache.commons.codec.binary.Base64.decodeBase64(imageData)
        val file = File(imagePath)
        Files.write(Paths.get(file.absolutePath), data)
        return imagePath

    }



    fun getImageForMessage(messageId: Int, lowRes: Boolean = true): ImageResponse{
        val message = messageRepository.findById(messageId).get()

        val image = if (lowRes) message.imagePathLowRes else message.imagePathFullRes
        var imageBase64 = ""
        if(!image.isNullOrEmpty()) {
            imageBase64 = readFileAsBase64(image)
        }
        return ImageResponse(messageId = messageId, imageBase64 = imageBase64)
    }

    fun readFileAsBase64(filePath: String): String {
        val file = File(filePath)
        val bytes = file.readBytes()
        return Base64.getEncoder().encodeToString(bytes)
    }
}