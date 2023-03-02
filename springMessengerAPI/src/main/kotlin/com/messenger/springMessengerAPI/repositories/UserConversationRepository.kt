package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.UserConversation
import com.messenger.springMessengerAPI.models.UserConversationId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserConversationRepository : JpaRepository<UserConversation, UserConversationId>{

    fun findAllById_UserId(userId: Int): List<UserConversation>
}