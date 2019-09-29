package org.quest.repositories

import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CompositionRepository : JpaRepository<Composition, Long> {
    fun findByTitle(title: String): Optional<Composition>
    fun findAllByTitleStartsWith(query: String): List<Composition>
    fun findByOrderByRatingDesc(): List<Composition>
}