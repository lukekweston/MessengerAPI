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

        var lowResImageBase64: String? = null
        //Save the images to files using the newly created messageRow as the id of the images
        if(newMessageRequest.imageBase64 != null) {
            //Save the full image and compressed images in their directories, update the value on message
            message.imagePathFullRes = saveImage(newMessageRequest.imageBase64, message.id, true)

            //Compress the image to a max size of 500kb
            lowResImageBase64 = resizeImage(newMessageRequest.imageBase64)
            //Save the image compressed to 500kb
            message.imagePathLowRes = saveImage(lowResImageBase64, message.id, false)

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
            updatedTime = message.updatedTime,
            imageLowRes = lowResImageBase64
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

    fun resizeImage(base64Image: String): String {
        val maxFileSize = 128000 // 128 KB in bytes

        val pattern = "[^A-Za-z0-9+/=]"
        val cleanImageString = base64Image.replace(Regex(pattern), "")
        val decodedBytes = Base64.getDecoder().decode(cleanImageString)

        // Check if the image is already below the maximum size
        if (decodedBytes.size <= maxFileSize) {
            return base64Image
        }

        val inputStream = ByteArrayInputStream(decodedBytes)
        val originalImage = ImageIO.read(inputStream)

        val width = originalImage.width
        val height = originalImage.height

        var newWidth = width
        var newHeight = height

        // Resize image if it's too large
        val scaleFactor = Math.sqrt(maxFileSize.toDouble() / decodedBytes.size)
        newWidth = (width * scaleFactor).toInt()
        newHeight = (height * scaleFactor).toInt()

        val resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        bufferedImage.graphics.drawImage(resizedImage, 0, 0, null)

        val outputStream = ByteArrayOutputStream()
        try {
            ImageIO.write(bufferedImage, "jpg", outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val resizedBytes = outputStream.toByteArray()
        val encodedString = Base64.getEncoder().encodeToString(resizedBytes)

        return encodedString
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