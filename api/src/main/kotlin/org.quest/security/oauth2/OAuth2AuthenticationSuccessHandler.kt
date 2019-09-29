package org.quest.security.oauth2

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import org.springframework.web.util.UriComponentsBuilder
import org.quest.util.CookieUtils
import javax.servlet.ServletException
import java.io.IOException
import org.quest.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication;
import org.quest.config.AppProperties
import org.quest.exception.BadRequestException
import java.net.URI


import org.quest.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.Companion.REDIRECT_URI_PARAM_COOKIE_NAME


@Component
class OAuth2AuthenticationSuccessHandler @Autowired constructor(var tokenProvider: TokenProvider, var appProperties: AppProperties,
                                                                var httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository) : SimpleUrlAuthenticationSuccessHandler() {


    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication) {
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            logger.debug("Response has already been committed. Unable to redirect to $targetUrl")
            return
        }

        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    protected fun determineTargetUrl(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication): String {
        val redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map { it.value }

        if (redirectUri.isPresent && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication")
        }

        val targetUrl = redirectUri.orElse(defaultTargetUrl)

        val token = tokenProvider.createToken(authentication)

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString()
    }

    protected fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)

        return appProperties.oauth2.authorizedRedirectUris
                .stream()
                .anyMatch { authorizedRedirectUri ->
                    // Only validate host and port. Let the clients use different paths if they want to
                    val authorizedURI = URI.create(authorizedRedirectUri)

                    if (authorizedURI.host.toLowerCase() == clientRedirectUri.host.toLowerCase() && authorizedURI.port == clientRedirectUri.port) {
                        return@anyMatch true
                    }
                    return@anyMatch false
                }
    }
}