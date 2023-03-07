package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "messages")
class Message(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "user_id")
        val userId: Int = 0,

        @Column(name = "text_message")
        val textMessage: String? = "",

        //Default to max, so never get a message that has no timesent
        @Column(name = "time_sent")
        val timeSent: LocalDateTime = LocalDateTime.MAX,

        @Column(name = "updated_time")
        val updatedTime: LocalDateTime? = null,

        @Column(name = "conversation_id")
        val conversationId: Int = 0,

        @Column(name="image_path_full_res")
        var imagePathFullRes: String? = null,

        @Column(name="image_path_low_res")
        var imagePathLowRes: String? = null
)