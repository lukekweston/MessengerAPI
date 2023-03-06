package com.messenger.springMessengerAPI.services

import com.messenger.springMessengerAPI.repositories.UsersRepository
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.models.request.FCMRegTokenRequest
import com.messenger.springMessengerAPI.models.request.UserLoginRequest
import com.messenger.springMessengerAPI.models.request.UserLogoutRequest
import com.messenger.springMessengerAPI.models.response.SuccessResponse
import com.messenger.springMessengerAPI.models.response.UserLoginResponse
import org.springframework.stereotype.Service


@Service
class UsersService(private val usersRepository: UsersRepository) {

    fun getAllUsers(): List<User> = usersRepository.findAll()
    fun getUserByUserName(userName: String): User? = usersRepository.findUsersByUsername(username = userName)

    fun loginAndVerifyUser(userLoginRequest: UserLoginRequest): UserLoginResponse {
        val user = usersRepository.findUserByUsernameAndPassword(userLoginRequest.userName, userLoginRequest.password)
        if(user != null){
            user.firebaseRegToken = userLoginRequest.firebaseRegistrationToken
            usersRepository.save(user)
        }
        return UserLoginResponse(successfulLogin = user != null, userId = user?.id, userName = user?.username, userEmail = user?.userEmail)
    }

    fun logoutUser(userLogoutRequest: UserLogoutRequest): SuccessResponse {
        val user = usersRepository.findUsersByUsernameAndId(userLogoutRequest.userName, userLogoutRequest.userId)
        if(user != null){
            user.firebaseRegToken = ""
            usersRepository.save(user)
            return SuccessResponse(success = true)
        }
        return SuccessResponse(success = false)
    }

    fun findUsernameById(id: Int): String? { return  usersRepository.findUsersById(id)?.username}

    fun checkFCMRegToken(fcmRegTokenRequest: FCMRegTokenRequest) : SuccessResponse{
        return SuccessResponse(success = usersRepository.findUsersById(fcmRegTokenRequest.userId)?.firebaseRegToken == fcmRegTokenRequest.firebaseRegistrationToken)
    }
}