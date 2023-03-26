package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.services.UserConversationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class UserConversationController(private val userConversationService: UserConversationService) {

}