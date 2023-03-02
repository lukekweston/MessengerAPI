package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.UserConversation
import com.messenger.springMessengerAPI.models.response.ConversationResponse
import com.messenger.springMessengerAPI.repositories.ConversationRepository
import com.messenger.springMessengerAPI.repositories.UserConversationRepository
import org.springframework.stereotype.Service


@Service
class ConversationService(private val conversationRepository: ConversationRepository, private val userConversationRepository: UserConversationRepository) {

    fun getConversationsForUser(userId: Int): List<ConversationResponse> {
        val conversations = userConversationRepository.findAllById_UserId(userId).map { it.conversation!! }
        return conversations.map {
            ConversationResponse(
                    id = it.id,
                    conversationName = getConversationName(it, userId),
                    lastUpdated = it.lastUpdated
            )
        }
    }

    //Conversation name will either be the name specified for a conversation
    //Or it will be a list of the participants
    fun getConversationName(conversation: Conversation, userId: Int): String {
        //
        if (!conversation.conversationName.isNullOrEmpty()) {
            return conversation.conversationName
        }
        //If conversation list has one member, its with yourself
        if (conversation.userConversation.size == 1) {
            return conversation.userConversation.first().user?.username ?: "Unknown conversation"
        }
        //Else the conversation would be with a group of participants
        else {
            var convoName = ""
            for (userConvo in conversation.userConversation) {
                userConvo.user?.let {
                    if (it.id != userId) {
                        convoName += it.username + ", "
                    }
                }
            }
            //Remove last ", "
            return convoName.dropLast(2)

        }
    }
}