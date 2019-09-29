package org.quest.security.oauth2

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.web.util.UriComponentsBuilder
import org.quest.util.CookieUtils
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import org.quest.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.Companion.REDIRECT_URI_PARAM_COOKIE_NAME


@Component
class OAuth2AuthenticationFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    @Autowired
    var httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository? = null

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationFailure(request: HttpServletRequest, response: HttpServletResponse, exception: AuthenticationException) {
        var targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map { it.value }.orElse("/")

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.localizedMessage)
                .build().toUriString()

        httpCookieOAuth2AuthorizationRequestRepository!!.removeAuthorizationRequestCookies(request, response)

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}