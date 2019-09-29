package org.quest.security.oauth2.user

import org.quest.models.AuthProvider


class OAuth2UserInfoFactory {

    companion object {
        fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo? {
            return when {
                registrationId.equals(AuthProvider.google.toString(), ignoreCase = true) -> GoogleOAuth2UserInfo(attributes)
                registrationId.equals(AuthProvider.facebook.toString(), ignoreCase = true) -> FacebookOAuth2UserInfo(attributes)
                registrationId.equals(AuthProvider.github.toString(), ignoreCase = true) -> GithubOAuth2UserInfo(attributes)
                //here was exception
                else -> null
            }
        }
    }


}