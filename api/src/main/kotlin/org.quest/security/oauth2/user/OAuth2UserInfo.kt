package org.quest.security.oauth2.user


abstract class OAuth2UserInfo {
    lateinit var attributes: Map<String, Any>

    constructor(attributes: Map<String, Any>) {
        this.attributes = attributes;
    }

    public abstract fun getId(): String;

    public abstract fun getName(): String;

    public abstract fun getEmail(): String;
}