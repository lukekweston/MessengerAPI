package com.messenger.springMessengerAPI.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(FirebaseProperties::class)
class FirebaseConfiguration(private val firebaseProperties: FirebaseProperties) {
    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }

    @Bean
    fun firebaseApp(credentials: GoogleCredentials?): FirebaseApp? {
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun googleCredentials(): GoogleCredentials {

        return if (firebaseProperties.serviceAccount != null) {
            firebaseProperties.serviceAccount?.inputStream.use {
                GoogleCredentials.fromStream(it)
            }
        } else {
            GoogleCredentials.getApplicationDefault()
        }
    }

}