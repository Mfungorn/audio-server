package org.quest.payload

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class SignUpRequest {
    @NotBlank
    private var name: String? = null

    @NotBlank
    @Email
    private var email: String? = null

    @NotBlank
    private var password: String? = null

    fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

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