package com.messenger.springMessengerAPI.controllers

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


//Inject the FirebaseMessaging
@RestController
class FCMController(private val fcm: FirebaseMessaging) {

//    https://stackoverflow.com/questions/72470311/send-push-notification-from-springboot

    @PostMapping("/testNotification/{message}")
    fun sendNotification(@PathVariable message: String): ResponseEntity<String> {

        val notification: Notification = Notification
            .builder()
            .setTitle("Hello world")
            .setBody(message)
            .build()

        val data = mapOf(
            "key1" to "value1",
            "key2" to "value2",
            "key3" to "hello is this working adding data like this"
        )


        val msg = Message.builder()
            .putData("body", message)
            .putAllData(data)
            .setToken("fUfOAufwR-OHIZZO5WCEWt:APA91bGOWpOvxDwdLzghNb0iBQgndOucw_h0rlXgsPAY2BsKSglG1LPg0iSJ_7Q13NtsH8MDbbg_pVOlJoeXRC9N2cGzl5Q78a-Ol62Yj-2Q0wtCkHxe0VtFzeMSlH9ifz2vIXdrjBlr")
            .build()

        val id = fcm.send(msg)
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(id)

//        val multicastMessage: MulticastMessage =
//            MulticastMessage
//            .builder()
//            .addToken("fUfOAufwR-OHIZZO5WCEWt:APA91bGOWpOvxDwdLzghNb0iBQgndOucw_h0rlXgsPAY2BsKSglG1LPg0iSJ_7Q13NtsH8MDbbg_pVOlJoeXRC9N2cGzl5Q78a-Ol62Yj-2Q0wtCkHxe0VtFzeMSlH9ifz2vIXdrjBlr")
//            .setNotification(notification)
//            .build()
//
//        val id = fcm.sendMulticast(multicastMessage)
//        return ResponseEntity
//            .status(HttpStatus.ACCEPTED)
//            .body("ok")


//
//        val msg = Message.builder()
//            .setTopic("test")
//            .putData("body", message)
//            .build()
//
//        val id = fcm.send(msg)
//        return ResponseEntity
//            .status(HttpStatus.ACCEPTED)
//            .body(id)


    }
}