package audio.security

import audio.exception.ResourceNotFoundException
import audio.repositories.CustomerRepository
import audio.repositories.ManagerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CustomUserDetailsService: UserDetailsService {
    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var managerRepository: ManagerRepository

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        var principal: UserPrincipal? = null
        customerRepository.findByEmail(email).ifPresent {
            principal = UserPrincipal.create(it)
        }
        managerRepository.findByEmail(email).ifPresent {
            principal = UserPrincipal.create(it)
        }
        return principal ?: throw UsernameNotFoundException("User not found with email : $email")
        //.orElseThrow { UsernameNotFoundException("User not found with email : $email") }
    }

    @Transactional
    fun loadUserById(id: Long): UserDetails {
        var principal: UserPrincipal? = null
        customerRepository.findById(id).ifPresent {
            principal = UserPrincipal.create(it)
        }
        managerRepository.findById(id).ifPresent {
            principal = UserPrincipal.create(it)
        }
        return principal ?: throw ResourceNotFoundException("User", "id", id as Any)

        // return UserPrincipal.create(user)
    }
}