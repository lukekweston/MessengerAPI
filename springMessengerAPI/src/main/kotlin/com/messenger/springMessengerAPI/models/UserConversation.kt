package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class UserConversationId(
    @Column(name = "user_id")
    val userId: Int = 0,
    @Column(name = "conversation_id")
    val conversationId: Int = 0
) : Serializable {}

@Entity
@Table(name = "user_conversation")
class UserConversation {
    @EmbeddedId
    var id: UserConversationId? = null

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id_id", referencedColumnName = "id", nullable = false)
    var user: User? = null

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversation_id_id", referencedColumnName = "id", nullable = false)
    var conversation: Conversation? = null
}