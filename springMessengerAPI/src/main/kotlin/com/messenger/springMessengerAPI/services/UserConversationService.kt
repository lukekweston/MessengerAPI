package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UserConversationRepository
import org.springframework.stereotype.Service

@Service
class UserConversationService(private val UserConversationRepository: UserConversationRepository) {

    fun findAllOtherUserIdsForConversation(userId: Int): List<Int> {
        return UserConversationRepository.findAllById_UserId(userId)
                .mapNotNull { it.id?.userId }
                .toMutableList()
    }
}