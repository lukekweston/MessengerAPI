package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import java.io.Serializable

@Embeddable
data class UserConversationId(
    @Column(name = "UserId")
    val userId: Int = 0,
    @Column(name = "ConversationId")
    val conversationId: Int = 0
) : Serializable {}

@Entity
@Table(name = "UserConversation")
class UserConversation {
    @EmbeddedId
    var id: UserConversationId? = null

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", nullable = false)
    var user: User? = null

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversationid", referencedColumnName = "id", nullable = false)
    var conversation: Conversation? = null
}