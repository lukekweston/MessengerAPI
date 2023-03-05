package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.UserConversation
import com.messenger.springMessengerAPI.models.UserConversationId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface UserConversationRepository : JpaRepository<UserConversation, UserConversationId>{

//    @Query(nativeQuery = true, value = "select * from user_conversation where user_id = :userId")
    fun findAllById_UserId(userId: Int): List<UserConversation>


//    fun findAllById_user_id(userId: Int): List<UserConversation>
}
