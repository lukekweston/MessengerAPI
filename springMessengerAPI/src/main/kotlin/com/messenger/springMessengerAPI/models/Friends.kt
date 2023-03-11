package com.messenger.springMessengerAPI.models

import jakarta.persistence.*


@Entity
@Table(name = "friends")
class Friend(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,

        @Column(name = "self_user_id")
        val selfUserId: Int = 0,

        @Column(name = "friend_user_id")
        val friendUserid: Int = 0,

        @Column(name = "status")
        var status: FriendshipStatus = FriendshipStatus.Unknown,
)

enum class FriendshipStatus{
        Sent, Pending, Friends, Blocked, Declined, Removed, Unknown

}