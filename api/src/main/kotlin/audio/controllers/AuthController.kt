package audio.controllers

import audio.exception.BadRequestException
import audio.models.AuthProvider
import audio.models.Customer
import audio.payload.ApiResponse
import audio.payload.AuthResponse
import audio.payload.LoginRequest
import audio.payload.SignUpRequest
import audio.repositories.CustomerRepository
import audio.repositories.ManagerRepository
import audio.security.TokenProvider
import io.jsonwebtoken.ExpiredJwtException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController {
    private val log = LoggerFactory.getLogger("AuthController")

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var managerRepository: ManagerRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    @GetMapping("/check")
    fun checkAuthorization(
            @RequestHeader(value = "Authorization") authorization: String
    ): ResponseEntity<*> {
        log.info("attempt to check authorization")
        return try {
            val token = tokenProvider.subtractToken(authorization)
            log.info("token claims: id=${tokenProvider.getUserIdFromToken(token)}; email=${tokenProvider.getUserEmailFromToken(token)}")
            if (tokenProvider.validateToken(token))
                ResponseEntity.ok("Validation succeeded")
            else
                ResponseEntity.badRequest().body("Validation failed")
        } catch (e: ExpiredJwtException) {
            return ResponseEntity.badRequest().body("Token expired")
        }
    }

    @PostMapping("/login")
    fun authenticateCustomer(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        log.info("attempt to authenticate customer ${loginRequest.getEmail()}")
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProvider.createToken(authentication)
        return ResponseEntity.ok<Any>(AuthResponse(token))
    }

    @PostMapping("/admin/login")
    fun authenticateManager(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        log.info("attempt to authenticate manager ${loginRequest.getEmail()}")
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProvider.createToken(authentication)
        return ResponseEntity.ok<Any>(AuthResponse(token))
    }

    @PostMapping("/register/customer")
    fun registerCustomer(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        log.info("attempt to register customer ${signUpRequest.getEmail()}")

        if (customerRepository.existsByEmail(signUpRequest.getEmail()!!)!!) {
            log.info("customer with this email already exists")
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

        customer.password = passwordEncoder.encode(customer.password)

        val result = customerRepository.save(customer)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body<Any>(ApiResponse(true, "User registered successfully"))
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/register/manager")
//    fun registerManager(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
//        log.info("attempt to register manager ${signUpRequest.getEmail()}")
//        if (managerRepository.existsByEmail(signUpRequest.getEmail()!!)!!) {
//            log.info("manager with this email already exists")
//            throw BadRequestException("Email address already in use.")
//        }
//
//        // Creating admin's account
//        val manager = Manager()
//        manager.name = signUpRequest.getName()!!
//        manager.email = signUpRequest.getEmail()!!
//        manager.password = signUpRequest.getPassword()
//        manager.provider = AuthProvider.local
//
//        manager.password = passwordEncoder.encode(manager.password)
//
//        val result = managerRepository.save(manager)
//
//        val location = ServletUriComponentsBuilder
//                .fromCurrentContextPath().path("/admin/me")
//                .buildAndExpand(result.id).toUri()
//
//        return ResponseEntity.created(location)
//                .body<Any>(ApiResponse(true, "Admin registered successfully"))
//    }
}