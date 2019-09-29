package org.quest.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.quest.repositories.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.quest.exception.ResourceNotFoundException
import org.quest.repositories.ManagerRepository


@Service
class CustomUserDetailsService: UserDetailsService {
    @Autowired
    var customerRepository: CustomerRepository? = null

    @Autowired
    var managerRepository: ManagerRepository? = null

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val customer = customerRepository!!.findByEmail(email)
        return if (customer.isPresent) {
            UserPrincipal.create(customer.get())
        } else {
            val manager = managerRepository!!.findByEmail(email)
                    .orElseThrow { UsernameNotFoundException("User not found with email : $email") }
            UserPrincipal.create(manager)
        }
                //.orElseThrow { UsernameNotFoundException("User not found with email : $email") }
    }

    @Transactional
    fun loadUserById(id: Long?): UserDetails {
        val customer = customerRepository!!.findById(id)
        return if (customer.isPresent) {
            UserPrincipal.create(customer.get())
        } else {
            val manager = managerRepository!!.findById(id).orElseThrow<RuntimeException> {
                ResourceNotFoundException("User", "id", id as Any)
            }
            UserPrincipal.create(manager)
        }

        // return UserPrincipal.create(user)
    }
}