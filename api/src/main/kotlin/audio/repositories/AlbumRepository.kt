package audio.repositories

import audio.models.Album
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AlbumRepository : JpaRepository<Album, Long> {
    fun findByTitle(title: String): Optional<Album>
    fun findAllByTitleStartsWith(query: String): List<Album>
    fun findByOrderByRatingDesc(): List<Album>
}