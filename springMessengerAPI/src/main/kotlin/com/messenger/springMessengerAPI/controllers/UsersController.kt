package com.messenger.springMessengerAPI.controllers

import com.messenger.springMessengerAPI.exceptions.ResourceNotFoundException
import com.messenger.springMessengerAPI.models.User
import com.messenger.springMessengerAPI.models.dto.ErrorResponse
import com.messenger.springMessengerAPI.models.request.UserLoginRequest
import com.messenger.springMessengerAPI.models.response.UserLoginResponse
import com.messenger.springMessengerAPI.services.UsersService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
class UsersController(private val usersService: UsersService) {
    
    @PostMapping("/loginUser")
    fun verifyAndLogInUser(@RequestBody userLoginRequest: UserLoginRequest) : UserLoginResponse  = usersService.loginAndVerifyUser(userLoginRequest)

   @GetMapping("/getUsername/{id}")
    fun getUsernameById(@PathVariable id: Int): String {
        val userName = usersService.findUsernameById(id)
       if(userName.isNullOrEmpty()){
           throw ResourceNotFoundException("Username not found for user $id")
       }
       return userName
    }


    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ErrorResponse {
        return ErrorResponse("Resource not found", ex.message)
    }

}