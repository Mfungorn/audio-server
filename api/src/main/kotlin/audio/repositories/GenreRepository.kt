package audio.repositories

import audio.models.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GenreRepository : JpaRepository<Genre, Long> {
    fun findByName(name: String): Optional<Genre>
    fun findAllByNameStartsWith(q: String): Set<Genre>
}