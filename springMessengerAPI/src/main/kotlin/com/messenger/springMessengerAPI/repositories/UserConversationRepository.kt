package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.UserConversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserConversationRepository : JpaRepository<UserConversation, Int> {


    fun findAllByUserId(userId: Int): List<UserConversation>
    fun findAllByConversationId(conversationId: Int): List<UserConversation>
    fun deleteAllByConversationId(conversationId: Int)

}
