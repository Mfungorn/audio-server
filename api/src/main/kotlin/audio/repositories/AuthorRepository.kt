package audio.repositories

import audio.models.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuthorRepository : JpaRepository<Author, Long> {
    fun findByName(name: String): Optional<Author>
    fun findAllByNameStartsWith(query: String): List<Author>
    fun findAllByOrderByRatingDesc(): List<Author>
}