package audio.controllers

import audio.exception.ResourceNotFoundException
import audio.models.Customer
import audio.models.Manager
import audio.payload.CustomerProfilePayload
import audio.repositories.CustomerRepository
import audio.repositories.ManagerRepository
import audio.security.CurrentUser
import audio.security.TokenProvider
import audio.security.UserPrincipal
import audio.util.toCustomerProfilePayload
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    private val log = LoggerFactory.getLogger("UserController")

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var managerRepository: ManagerRepository

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @GetMapping("/user/profile")
    fun getUser(
            @RequestHeader(value = "Authorization") authorization: String
    ): CustomerProfilePayload {
        val customerId = tokenProvider.getUserIdFromAuthHeader(authorization)
                ?: throw BadCredentialsException("Invalid token")
        val customer = customerRepository.findById(customerId)
                .orElseThrow { ResourceNotFoundException("User", "id", customerId) }
        log.info("find customer - ${customer.name}")
        return customer.toCustomerProfilePayload()
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): Customer {
        log.info("attempt to get customer ${userPrincipal.getId()}")
        return customerRepository.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("User", "id", userPrincipal.getId()!!) }
    }

    @GetMapping("/admin/me")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun getCurrentAdmin(@CurrentUser userPrincipal: UserPrincipal): Manager {
        log.info("attempt to get manager ${userPrincipal.getId()}")
        return managerRepository.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("Admin", "id", userPrincipal.getId()!!) }
    }
}