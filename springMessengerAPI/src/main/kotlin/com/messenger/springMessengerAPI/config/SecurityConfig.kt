package com.messenger.springMessengerAPI.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.SecurityFilterChain


@EnableWebSecurity

class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    protected fun configure(security: HttpSecurity) {
        security.httpBasic().disable()
    }
}