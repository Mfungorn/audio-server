package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.repositories.CompositionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/compositions")
class CompositionController {

    private val log = LoggerFactory.getLogger("CompositionController")

    @Autowired
    private val compositionRepository: CompositionRepository? = null

    @GetMapping("/{id}")
    fun getComposition(@PathVariable id: Long): Composition {
        log.info("get composition with id: $id")
        return compositionRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Composition", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularCompositions(): List<Composition> {
        log.info("get popular compositions")
        return compositionRepository!!.findByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findComposition(@RequestParam q: String): List<Composition> {
        log.info("find composition by title stars with $q")
        return compositionRepository!!.findAllByTitleStartsWith(q)
    }

    @GetMapping("/{id}/authors")
    fun getCompositionAuthors(@PathVariable id: Long): List<Author> {
        log.info("get authors of composition with id: $id")
        val composition = compositionRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Composition", "id", id) }
        return composition.authors.toList()
    }

    @GetMapping("/{id}/albums")
    fun getCompositionAlbums(@PathVariable id: Long): List<Album> {
        log.info("get albums with composition with id: $id")
        val composition = compositionRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Composition", "id", id) }
        return composition.albums.toList()
    }

    @GetMapping("/{id}/genres")
    fun getCompositionGenres(@PathVariable id: Long): Set<Genre> {
        log.info("get genres of album with id: $id")
        val composition = compositionRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Composition", "id", id) }
        return composition.genres
    }
}