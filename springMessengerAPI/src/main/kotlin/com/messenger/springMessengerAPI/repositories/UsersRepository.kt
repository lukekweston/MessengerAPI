package com.messenger.springMessengerAPI.repositories

import com.messenger.springMessengerAPI.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface UsersRepository : JpaRepository<User, Int> {
    fun findUsersByUsername(username: String): User?

    //Login method with no auth, this is for local test purposes only - the whole login process is basic
    @Query(
        nativeQuery = true, value = "SELECT * " +
                "FROM users " +
                "WHERE lower(user_name) = lower(:username) AND password = :password " +
                "LIMIT 1;"
    )
    fun findUserByUsernameAndPassword(username: String, password: String): User?

    fun findUsersByUsernameAndId(username: String, id: Int): User?


    fun findUsersById(id: Int): User?

    @Query(
        nativeQuery = true, value = "SELECT * " +
                "FROM users " +
                "WHERE lower(user_name) = lower(:usernameOrEmail) or lower(user_email) = lower(:usernameOrEmail) " +
                "LIMIT 1;"
    )
    fun findUsersByUsernameOrUserEmail(usernameOrEmail: String): User?

}