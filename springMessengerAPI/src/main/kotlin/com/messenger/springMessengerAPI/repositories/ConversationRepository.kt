package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ConversationRepository : JpaRepository<Conversation, Int>{
    fun findAllById(id: Int): Conversation
}