# MessengerAPI

This is a Kotlin SpringBoot MVC application that connects to a posgres database as well as firebase and has API endpoints for the messenger app to communicate with.

This application also uses liquibase to handle/manage database changes.

**Note: This is a work in progress, currently all endpoints do not require Auth and are unprotected**

# Setup

1) Update application.properties
* Set the 'spring.datasource.url' to the location of your posgres database
* Set the username and password if required
* Set the locations where the full res and low res images will be stored 'app.upload-full-res.dir' and 'app.upload-low-res.dir'
* Set the port which you would like the API to be visible on - you may need to change firewall rules for this port to allow incoming traffic, basic windows 10 instructions are at the bottom of this file

2) You will need to set up firebase-service-account.json, instructions for this are in this file


# API End points

**All API endpoints with example requests are in the postman collection that is part of this repo**

This is basic documentation on what each endpoint does

| URL      | Parameters | Description of the endpoint |
| :---        |    :----   |          :--- |
| POST /loginUser     | Body including username, password and firebase registration token     | Checks the username and password are correct for a user, if they are it will set the firebase registration token for a user and return a boolean value that the user has logged in successfully along with the users details. If the password does not match it will return false |
| POST /logoutUser   | Body including userId and username        | Deletes the firebase registration token, unlinking the device to the logged in user  |
| GET /getUsername/{id} | {id} the userId to get the userName for | Gets the username for a specific userId |
| POST /checkFCMRegToken |  Body including userId and firebase registration token | Used to check the user hasn't logged in on another device |
| GET /findAllFriendsForUser/{userId} | {userId} userId to find all the friends for | Returns a list of all the friends that a user has  - used for populating the room database in the app when logging in a user to cache the data|
| GET /getConversationsForUser/{userId} | {userId} userId to find all the conversations for | Returns a list of all the conversations that the user is involved in - used for populating the room database in the app when logging in a user to cache the data |
| GET /getAllMessagesForConversation/{conversationId} | {conversationId} conversationId to get all the messages for | Returns a list of all the messages for the conversation, only low res images are returned  |
| GET /allMessagesForUser/{userId} | {userId} userId to get all the messages for | Gets all the messages for all the conversations that a user is involved in, only low res images are returned - used for populating the room database in the app when logging in a user to cache the data |
| GET /getMessagesAfter | Request body including a userId and datetime for getting messages after | Gets all the messages after a current time point - will be used for if the user loses internet connection and then regains it |
| POST /sendMessage | Request has a body that includes: 
**userId** - user sending the message
**conversationId** - conversation the message is for
**message** - text that the message contains
**imageBase64FullRes** - full sized image as base64 encoded string
**imageBase64LowRes** - compressed image as base64 encoded string (images are compressed in the app before they are sent) | |



## Database diagram

![image](https://user-images.githubusercontent.com/49537169/227751348-bc0edb56-1aa9-44ee-af77-55d9b856a268.png)
