package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.repositories.AlbumRepository
import org.quest.repositories.AuthorRepository
import org.quest.repositories.CompositionRepository
import org.quest.repositories.GenreRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/genres")
class GenreController {
    private val log = LoggerFactory.getLogger("GenreController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @GetMapping("/{name}")
    fun getGenre(@PathVariable name: String): Genre {
        log.info("get genre with name: $name")
        return genreRepository.findByName(name).orElseThrow { ResourceNotFoundException("Genre", "name", name) }
    }

    @GetMapping("/{name}/authors")
    fun getAuthorsByGenre(@PathVariable name: String): List<Author> {
        val genre = genreRepository.findByName(name).orElseThrow { ResourceNotFoundException("Genre", "name", name) }
        val result = authorRepository.findAllByGenres(genre)
        return result.sortedByDescending { it.rating }
    }

    @GetMapping("/{name}/albums")
    fun getAlbumsByGenre(@PathVariable name: String): List<Album> {
        val genre = genreRepository.findByName(name).orElseThrow { ResourceNotFoundException("Genre", "name", name) }
        val result = albumRepository.findAllByGenres(genre)
        return result.sortedByDescending { it.rating }
    }

    @GetMapping("/{name}/compositions")
    fun getCompositionsByGenre(@PathVariable name: String): List<Composition> {
        val genre = genreRepository.findByName(name).orElseThrow { ResourceNotFoundException("Genre", "name", name) }
        val result = compositionRepository.findAllByGenres(genre)
        return result.sortedByDescending { it.rating }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createGenre(@RequestBody name: String): ResponseEntity<Genre> {
        val genre = Genre(name)
        val result = genreRepository.save(genre)
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{name}")
    fun deleteGenre(@PathVariable name: String): ResponseEntity<String> {
        val genre = genreRepository.findByName(name).orElseThrow { ResourceNotFoundException("Genre", "name", name) }
        genreRepository.delete(genre)
        return ResponseEntity.ok(name)
    }
}