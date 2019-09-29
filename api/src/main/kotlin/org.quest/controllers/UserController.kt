package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Customer
import org.quest.models.Manager
import org.quest.repositories.CustomerRepository
import org.quest.repositories.ManagerRepository
import org.quest.security.CurrentUser
import org.quest.security.UserPrincipal
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.beans.factory.annotation.Autowired

@RestController
class UserController {

    @Autowired
    private val customerRepository: CustomerRepository? = null

    @Autowired
    private val managerRepository: ManagerRepository? = null

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): Customer {
        return customerRepository!!.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("User", "id", userPrincipal.getId()!!) }
    }

    @GetMapping("/admin/me")
    @PreAuthorize("hasRole('ADMIN')")
    fun getCurrentAdmin(@CurrentUser userPrincipal: UserPrincipal): Manager {
        return managerRepository!!.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("Admin", "id", userPrincipal.getId()!!) }
    }
}