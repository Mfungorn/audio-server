package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Customer
import org.quest.models.Manager
import org.quest.repositories.CustomerRepository
import org.quest.repositories.ManagerRepository
import org.quest.security.CurrentUser
import org.quest.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.beans.factory.annotation.Autowired

@RestController
class UserController {
    private val log = LoggerFactory.getLogger("UserController")

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var managerRepository: ManagerRepository

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): Customer {
        log.info("attempt to get customer ${userPrincipal.getId()}")
        return customerRepository.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("User", "id", userPrincipal.getId()!!) }
    }

    @GetMapping("/admin/me")
    @PreAuthorize("hasRole('ADMIN')")
    fun getCurrentAdmin(@CurrentUser userPrincipal: UserPrincipal): Manager {
        log.info("attempt to get manager ${userPrincipal.getId()}")
        return managerRepository.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("Admin", "id", userPrincipal.getId()!!) }
    }
}