package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*


@Entity
@Table(name="users")
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "user_name", unique = true, nullable = false)
        val username: String = "",

        @Column(name = "user_email", unique = true, nullable = false)
        val userEmail: String = "",

        @Column(name = "password", nullable = false)
        val password: String = "",

        @Column(name = "firebase_reg_token", nullable = true)
        var firebaseRegToken: String = "",


        @JsonManagedReference
        @OneToMany(mappedBy = "user")
        val userConversation: MutableList<UserConversation> = mutableListOf()
)