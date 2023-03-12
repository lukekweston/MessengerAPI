package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import java.time.LocalDateTime


@Entity
@Table(name = "conversations")
class Conversation(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "conversation_name")
        val conversationName: String? = null,

        @Column(name = "last_updated")
        val lastUpdated: LocalDateTime? = null,

        @Column(name = "group_conversation")
        val groupConversation: Boolean = false,

        @JsonManagedReference
        @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
        val userConversation: MutableList<UserConversation> = mutableListOf()
)