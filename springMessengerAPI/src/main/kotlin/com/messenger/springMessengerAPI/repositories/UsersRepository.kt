package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<User, Int>{
    fun findUsersByUsername(username: String): User

    //Login method with no auth, this is for local test purposes only - the whole login process is basic
    @Query(nativeQuery = true, value = "select Top 1  * from Users where username = :username and password = :password COLLATE SQL_Latin1_General_CP1_CS_AS")
    fun findUserByUsernameAndPassword(username: String, password:String): User?

    fun findUsersById(id: Int): User?
}