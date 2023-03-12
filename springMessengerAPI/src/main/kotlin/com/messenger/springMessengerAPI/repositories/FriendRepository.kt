package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.Conversation
import com.messenger.springMessengerAPI.models.Friend
import com.messenger.springMessengerAPI.models.FriendshipStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface FriendRepository : JpaRepository<Friend, Int> {
    fun findBySelfUserIdAndFriendUserid(selfUserId: Int, friendId: Int): Friend?


    fun findAllBySelfUserId(selfUserId: Int): List<Friend>

    fun findFriendBySelfUserIdAndFriendUseridAndStatus(selfUserId: Int, friendUserId: Int, status: FriendshipStatus): Friend?

}