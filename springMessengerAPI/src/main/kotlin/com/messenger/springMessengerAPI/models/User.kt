package com.messenger.springMessengerAPI.models

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*


@Entity
@Table(name="Users")
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "UserName", unique = true, nullable = false)
        val username: String = "",

        @Column(name = "UserEmail", unique = true, nullable = false)
        val useremail: String = "",

        @Column(name = "Password", nullable = false)
        val password: String = "",

        @JsonManagedReference
        @OneToMany(mappedBy = "user")
        val userConversation: MutableList<UserConversation> = mutableListOf()
)