package org.quest.controllers

import org.quest.exception.BadRequestException
import org.quest.models.AuthProvider
import org.quest.models.Customer
import org.quest.models.Manager
import org.quest.payload.AuthResponse
import org.quest.payload.LoginRequest
import org.quest.payload.SignUpRequest
import org.quest.repositories.CustomerRepository
import org.quest.repositories.ManagerRepository
import org.quest.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager


@RestController
@RequestMapping("/auth")
class AuthController {
    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val customerRepository: CustomerRepository? = null

    @Autowired
    private val managerRepository: ManagerRepository? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Autowired
    private val tokenProvider: TokenProvider? = null

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/login")
    fun authenticateCustomer(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProvider!!.createToken(authentication)
        return ResponseEntity.ok<Any>(AuthResponse(token))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/customer")
    fun registerCustomer(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        if (customerRepository!!.existsByEmail(signUpRequest.getEmail()!!)!!) {
            throw BadRequestException("Email address already in use.")
        }

        // Creating user's account
        val customer = Customer()
        customer.name = signUpRequest.getName()!!
        customer.email = signUpRequest.getEmail()!!
        customer.password = signUpRequest.getPassword()
        customer.balance = 0
        customer.phone = ""
        customer.provider = AuthProvider.local

        customer.password = passwordEncoder!!.encode(customer.password)

        val result = customerRepository.save(customer)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body<Any>(ApiResponse(true, "User registered successfully"))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/manager")
    fun registerManager(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        if (managerRepository!!.existsByEmail(signUpRequest.getEmail()!!)!!) {
            throw BadRequestException("Email address already in use.")
        }

        // Creating admin's account
        val manager = Manager()
        manager.name = signUpRequest.getName()!!
        manager.email = signUpRequest.getEmail()!!
        manager.password = signUpRequest.getPassword()
        manager.provider = AuthProvider.local

        manager.password = passwordEncoder!!.encode(manager.password)

        val result = managerRepository.save(manager)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/admin/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body<Any>(ApiResponse(true, "Admin registered successfully"))
    }
}