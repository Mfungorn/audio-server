package audio.repositories

import audio.models.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {

    fun findByEmail(email: String): Optional<Customer>

    fun existsByEmail(email: String): Boolean?

}