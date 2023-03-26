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

![image](https://user-images.githubusercontent.com/49537169/227753427-98efc110-28bb-4859-b487-c60f6285a5e1.png)

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
| POST /sendMessage | Request has a body that includes:<br> **userId** - user sending the message<br> **conversationId** - conversation the message is for<br> **message** - text that the message contains<br> **imageBase64FullRes** - full sized image as base64 encoded string<br> **imageBase64LowRes** - compressed image as base64 encoded string (images are compressed in the app before they are sent) | This endpoint creates a new message in the database, images are saved as files seperate to the database and their locations are saved to the the database (this is to improve database performance), once a message has been saved the message including its serverside id will be returned to the app and all other users that belong to the conversation that this message is for will be sent a push notification with the details for this message including if the message is a text message or an image (this will not include the image if there is one, the app will download the image when the user opens the conversation)|
| PUT /updateMessage | Body that has the details on the message to be updated | This endpoint updates a message that is saved in the database |
| DELETE /deleteMessage/{messageId} | {messageId} - the id of the message to be deleted | This endpoint deletes a message |
| GET /getLowResImageForMessage/{messageId} | {messageId} - the id for the message to get the low res image for | Returns the image as a dto including the low res image encoded as a base64 string | 
| GET /getFullResImageForMessage/{messageId} | {messageId} - the id for the message to get the high res image for | Returns the image as a dto including the high res image encoded as a base64 string | 
| GET /findAllFriendsForUser/{userId} | {userID} - the userId to get all the friends for | This endpoint returns all the friends/relationships the user has - used for populating the room database in the app when logging in a user to cache the data |
| POST /sendFriendRequest | Body includes the persons id sending the request and to who the request is for | Returns details on if the friend request is valid - if it is valid (as in sending to a real user) the endpoint will create relationships in the database and send the other user a firebase push notification that they have been sent a friend request, the app will read the data with this friend request and create a friend request in the app |
| POST /updateFriendStatus | Body contains: <br> **selfUserId** - The userId of the user who sent the update <br> **friendUsername** - friendUsername to update for <br> **friendshipStatus** - what the friendship status is being updated to | The api updates the friend status, and then sends a push notification to the other user, the app will read the push notification and do different things depending on the data sent, examples - it will show a notification on friendship accepted, it wont when you get deleted/blocked. This endpoint returns a conversation response, this is so the app can delete the conversation from the local room/cache data base to remove this conversation from their app|



## Database diagram

![image](https://user-images.githubusercontent.com/49537169/227751348-bc0edb56-1aa9-44ee-af77-55d9b856a268.png)
