package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "Messages")
class Message(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "UserId")
        val userId: Int = 0,

        @Column(name = "TextMessage")
        val textMessage: String? = "",

        //Default to max, so never get a message that has no timesent
        @Column(name = "TimeSent")
        val timeSent: LocalDateTime = LocalDateTime.MAX,

        @Column(name = "UpdatedTime")
        val updatedTime: LocalDateTime? = null,

        @Column(name = "ConversationId")
        val conversationId: Int = 0,
)