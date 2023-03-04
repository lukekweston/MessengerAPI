package com.messenger.springMessengerAPI.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource

@ConfigurationProperties(prefix = "gcp.firebase")
class FirebaseProperties {
    var serviceAccount: Resource? = null
}