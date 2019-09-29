package org.quest.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class LoginRequest {
    @NotBlank
    @Email
    private var email: String? = null

    @NotBlank
    private var password: String? = null

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }
}