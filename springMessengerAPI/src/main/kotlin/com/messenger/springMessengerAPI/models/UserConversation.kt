package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "user_conversation")
class UserConversation (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_Id", referencedColumnName = "id", nullable = false)
    var user: User? = null,

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversation_Id", referencedColumnName = "id", nullable = false, unique = true)
    var conversation: Conversation? = null
)