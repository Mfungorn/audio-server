package audio.security.oauth2.user


abstract class OAuth2UserInfo(
        var attributes: Map<String, Any>
) {
    abstract fun getId(): String
    abstract fun getName(): String
    abstract fun getEmail(): String
}