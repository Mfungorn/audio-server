package org.quest.security

import org.springframework.stereotype.Service
import java.security.SignatureException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import java.util.*
import io.jsonwebtoken.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.quest.config.AppProperties


@Service
class TokenProvider {

    private val logger = LoggerFactory.getLogger(TokenProvider::class.java)

    private var appProperties: AppProperties? = null

    constructor(appProperties: AppProperties) {
        this.appProperties = appProperties
    }

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.getPrincipal() as UserPrincipal

        val now = Date()
        val expiryDate = Date(now.getTime() + appProperties?.auth!!.tokenExpirationMsec)

        return Jwts.builder()
                .setSubject(java.lang.Long.toString(userPrincipal.getId()!!))
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, appProperties!!.auth.tokenSecret)
                .compact()
    }

    fun getUserIdFromToken(token: String): Long? {
        val claims = Jwts.parser()
                .setSigningKey(appProperties!!.auth.tokenSecret)
                .parseClaimsJws(token)
                .body

        return java.lang.Long.parseLong(claims.subject)
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(appProperties!!.auth.tokenSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }
}