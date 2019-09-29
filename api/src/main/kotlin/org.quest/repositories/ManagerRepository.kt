package org.quest.repositories

import org.quest.models.Customer
import org.quest.models.Manager
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ManagerRepository : JpaRepository<Manager, Long> {

    fun findByEmail(email: String): Optional<Manager>

    fun existsByEmail(email: String): Boolean?

}