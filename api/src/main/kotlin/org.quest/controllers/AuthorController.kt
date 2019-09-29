package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.repositories.AuthorRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController {

    private val log = LoggerFactory.getLogger("AuthorController")

    @Autowired
    private val authorRepository: AuthorRepository? = null

    @GetMapping("/{id}")
    fun getAuthor(@PathVariable id: Long): Author {
        log.info("get author with id: $id")
        return authorRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularAuthors(): List<Author> {
        log.info("get popular authors")
        return authorRepository!!.findByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findAuthor(@RequestParam q: String): List<Author> {
        log.info("find author by name stars with $q")
        return authorRepository!!.findAllByNameStartsWith(q)
    }

    @GetMapping("/{id}/albums")
    fun getAuthorAlbums(@PathVariable id: Long): List<Album> {
        log.info("get albums of author with id: $id")
        val author = authorRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.albums.toList()
    }

    @GetMapping("/{id}/compositions")
    fun getAuthorCompositions(@PathVariable id: Long): List<Composition> {
        log.info("get compositions of author with id: $id")
        val author = authorRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.compositions.toList()
    }

    @GetMapping("/{id}/genres")
    fun getAuthorGenres(@PathVariable id: Long): Set<Genre> {
        log.info("get genres of author with id: $id")
        val author = authorRepository!!.findById(id).orElseThrow { ResourceNotFoundException("Author", "id", id) }
        return author.genres
    }
}