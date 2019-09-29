package org.quest.payload

class AuthResponse {
    private var accessToken: String? = null
    private var tokenType = "Bearer"

    constructor(accessToken: String) {
        this.accessToken = accessToken
    }

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    fun getTokenType(): String {
        return tokenType
    }

    fun setTokenType(tokenType: String) {
        this.tokenType = tokenType
    }
}