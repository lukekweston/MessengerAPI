package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.response.ConversationResponse
import com.messenger.springMessengerAPI.services.ConversationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class ConversationController(private val conversationService: ConversationService) {
    @GetMapping("/getConversationsForUser/{userId}")
    fun getConversationsForUser(@PathVariable userId: Int): List<ConversationResponse?> =
        conversationService.getConversationsForUser(userId = userId)

}