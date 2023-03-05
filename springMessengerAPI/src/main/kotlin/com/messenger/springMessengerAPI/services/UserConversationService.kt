package com.messenger.springMessengerAPI.services

import com.google.firebase.database.Exclude
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.repositories.UserConversationRepository
import org.springframework.stereotype.Service

@Service
class UserConversationService(
    private val userConversationRepository: UserConversationRepository,
    private val usersService: UsersService
) {

    fun findAllOtherUserIdsForConversation(userIdToExclude: Int, conversationId: Int): List<User> {
        val allUsersInConversation = userConversationRepository.findAllById_ConversationId(conversationId)

        val userList: List<User> =
            allUsersInConversation.mapNotNull { it.takeIf { userConvo -> userConvo.user != null }?.user }
                .filter {it.id != userIdToExclude }

        return userList
    }
}