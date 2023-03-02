package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import java.time.LocalDateTime


@Entity
@Table(name = "Conversations")
class Conversation(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "ConversationName")
        val conversationName: String? = null,

        @Column(name = "LastUpdated")
        val lastUpdated: LocalDateTime? = null,

        @JsonManagedReference
        @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
        val userConversation: MutableList<UserConversation> = mutableListOf()
)