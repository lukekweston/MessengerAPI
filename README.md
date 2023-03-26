# MessengerAPI

This is a Kotlin SpringBoot MVC application that connects to a posgres database as well as firebase and has API endpoints for the messenger app to communicate with.

This application also uses liquibase to handle/manage database changes.

**Note: This is a work in progress, currently all endpoints do not require Auth**

# Setup

1) Update application.properties
* Set the 'spring.datasource.url' to the location of your posgres database
* Set the username and password if required
* Set the locations where the full res and low res images will be stored 'app.upload-full-res.dir' and 'app.upload-low-res.dir'
* Set the port which you would like the API to be visible on - you may need to change firewall rules for this port to allow incoming traffic, basic windows 10 instructions are at the bottom of this file

2) You will need to set up firebase-service-account.json, instructions for this are in this file


# API End points

All api endpoints with example requests are in the postman collection that is part of this repo






## Database diagram

![image](https://user-images.githubusercontent.com/49537169/227751348-bc0edb56-1aa9-44ee-af77-55d9b856a268.png)
