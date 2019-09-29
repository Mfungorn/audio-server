package org.quest.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint::class.java)

    override fun commence(
            request: HttpServletRequest?,
            response: HttpServletResponse?,
            authException: AuthenticationException?
    ) {
        logger.error("Responding with unauthorized error. Message - {}", authException?.message)
        response?.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                authException?.localizedMessage)
    }


}