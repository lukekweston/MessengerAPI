package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UsersRepository
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.models.request.UserLoginRequest
import com.messenger.springMessengerAPI.models.response.UserLoginResponse
import org.springframework.stereotype.Service


@Service
class UsersService(private val usersRepository: UsersRepository) {

    fun getAllUsers(): List<User> = usersRepository.findAll()
    fun getUserByUserName(userName: String): User = usersRepository.findUsersByUsername(username = userName)

    fun loginAndVerifyUser(userLoginRequest: UserLoginRequest): UserLoginResponse {
        val user = usersRepository.findUserByUsernameAndPassword(userLoginRequest.userName, userLoginRequest.password)
        return UserLoginResponse(successfulLogin = user != null, userId = user?.id, userName = user?.username, userEmail = user?.useremail)
    }

    fun findUsernameById(id: Int): String? { return  usersRepository.findUsersById(id)?.username}
}