package audio.controllers

import audio.exception.ResourceNotFoundException
import audio.models.Author
import audio.models.Composition
import audio.models.Customer
import audio.models.Favorites
import audio.repositories.CustomerRepository
import audio.security.TokenProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/favorites")
class FavoritesController {

    private val log = LoggerFactory.getLogger("FavoritesController")

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @GetMapping
    fun getFavorites(
            @RequestHeader(value = "Authorization") authorization: String
    ): ResponseEntity<Favorites> {
        log.info("attempt to get customer's favorites")
        val customer = getCustomerFromToken(authorization)
        val result = Favorites(
                customer.favoriteAuthors,
                customer.favoriteCompositions
        )
        return ResponseEntity.ok(result)
    }

    @GetMapping("/authors")
    fun getFavoriteAuthors(
            @RequestHeader(value = "Authorization") authorization: String
    ): Set<Author> {
        log.info("attempt to get customer's favorite authors")
        val customer = getCustomerFromToken(authorization)
        return customer.favoriteAuthors
    }

    @GetMapping("/compositions")
    fun getFavoriteCompositions(
            @RequestHeader(value = "Authorization") authorization: String
    ): Set<Composition> {
        log.info("attempt to get customer's favorite compositions")
        val customer = getCustomerFromToken(authorization)
        return customer.favoriteCompositions
    }

    private fun getCustomerFromToken(authorization: String): Customer {
        log.info("attempt to find customer")
        val customerId = tokenProvider.getUserIdFromAuthHeader(authorization)
                ?: throw BadCredentialsException("Invalid token")
        val customer = customerRepository.findById(customerId)
                .orElseThrow { ResourceNotFoundException("User", "id", customerId) }
        log.info("find customer - ${customer.name}")
        return customer
    }
}