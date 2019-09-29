package org.quest.security.oauth2

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import org.quest.util.CookieUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class HttpCookieOAuth2AuthorizationRequestRepository: AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
    companion object {
        val REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri"
    }

    private val cookieExpireSeconds = 180

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map { cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest::class.java) }
                .orElse(null)
    }

    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?, request: HttpServletRequest, response: HttpServletResponse) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), cookieExpireSeconds)
        val redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)
        if (redirectUriAfterLogin.isNotBlank()) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds)
        }
    }

    override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest {
        return this.loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }
}