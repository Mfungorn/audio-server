package org.quest.repositories

import org.quest.models.Author
import org.quest.models.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GenreRepository : JpaRepository<Genre, Long> {
}