package audio.security

import audio.config.AppProperties
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.SignatureException
import java.util.*


@Service
class TokenProvider(
        private var appProperties: AppProperties
) {
    private val logger = LoggerFactory.getLogger(TokenProvider::class.java)

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal

        val now = Date()
        val expiryDate = Date(now.time + appProperties.auth.tokenExpirationMsec)

        return Jwts.builder()
                .setId((userPrincipal.getId()!!).toString())
                .setIssuer(userPrincipal.getEmail())
                .setIssuedAt(Date())
                .setExpiration(expiryDate)
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, appProperties.auth.tokenSecret)
                .compact()
    }

    fun getUserIdFromToken(token: String): Long? {
        val claims = Jwts.parser()
                .setSigningKey(appProperties.auth.tokenSecret)
                .parseClaimsJws(token)
                .body

        return java.lang.Long.parseLong(claims.id)
    }

    fun getUserIdFromAuthHeader(authorization: String): Long? {
        return getUserIdFromToken(subtractToken(authorization))
    }

    fun getUserEmailFromToken(token: String): String? {
        val claims = Jwts.parser()
                .setSigningKey(appProperties.auth.tokenSecret)
                .parseClaimsJws(token)
                .body

        return claims.issuer
    }

    fun subtractToken(authorization: String): String {
        return if (authorization.startsWith("Bearer ")) {
            authorization.substring(7, authorization.length)
        } else {
            throw BadCredentialsException("Invalid token")
        }
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(appProperties.auth.tokenSecret).parseClaimsJws(authToken)
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