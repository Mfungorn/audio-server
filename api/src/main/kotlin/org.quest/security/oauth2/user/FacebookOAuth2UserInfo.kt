package org.quest.security.oauth2.user

class FacebookOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override fun getId(): String {
        return attributes.get("id") as String;
    }

    override fun getName(): String {
        return attributes.get("name") as String
    }

    override fun getEmail(): String {
        return attributes.get("email") as String
    }
}