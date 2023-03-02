package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface MessageRepository : JpaRepository<Message, Int>{
    fun findAllByConversationId(conversationId: Int): List<Message>
}