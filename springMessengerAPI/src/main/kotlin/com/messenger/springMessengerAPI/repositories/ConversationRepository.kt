package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface ConversationRepository : JpaRepository<Conversation, Int> {
    fun findAllById(id: Int): Conversation

    @Query(nativeQuery = true, value = """SELECT c.*
                                    FROM conversations c
                                    WHERE (
                                        SELECT COUNT(*) 
                                        FROM user_conversation uc 
                                        WHERE uc.conversation_id = c.id AND uc.user_id IN (:user1Id, :user2Id)
                                    ) = 2
                                    AND c.group_conversation = false;"""
    )
    fun selectAPrivateNonGroupConversationForTwoUsers(user1Id: Int, user2Id: Int): Conversation?
}