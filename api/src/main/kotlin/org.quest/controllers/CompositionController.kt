package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.Album
import org.quest.models.Author
import org.quest.models.Composition
import org.quest.models.Genre
import org.quest.payload.CompositionPayload
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
@RequestMapping("/compositions")
class CompositionController {

    private val log = LoggerFactory.getLogger("CompositionController")

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var albumRepository: AlbumRepository

    @Autowired
    private lateinit var compositionRepository: CompositionRepository

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @GetMapping("/{id}")
    fun getComposition(@PathVariable id: Long): Composition {
        log.info("get composition with id: $id")
        return compositionRepository.findById(id).orElseThrow { ResourceNotFoundException("Composition", "id", id) }
    }

    @GetMapping("/popular")
    fun getPopularCompositions(): List<Composition> {
        log.info("attempt to get popular compositions")
        return compositionRepository.findByOrderByRatingDesc()
    }

    @GetMapping("/search")
    fun findComposition(@RequestParam q: String): List<Composition> {
        log.info("attempt to find composition by title stars with $q")
        return compositionRepository.findAllByTitleStartsWith(q)
    }

    @GetMapping("/{id}/authors")
    fun getCompositionAuthors(@PathVariable id: Long): List<Author> {
        log.info("attempt to get authors of composition with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        return composition.authors.toList()
    }

    @GetMapping("/{id}/albums")
    fun getCompositionAlbums(@PathVariable id: Long): List<Album> {
        log.info("attempt to get albums with composition with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        return composition.albums.toList()
    }

    @GetMapping("/{id}/genres")
    fun getCompositionGenres(@PathVariable id: Long): Set<Genre> {
        log.info("attempt to get genres of album with id: $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        return composition.genres
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createComposition(@RequestBody payload: CompositionPayload): ResponseEntity<Composition> {
        log.info("attempt to create composition with title: ${payload.title}")
        val composition = Composition(
                payload.title,
                payload.duration,
                payload.text,
                payload.price
        )
        val result = compositionRepository.save(composition)
        log.info("created composition id: ${result.id}")
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/authors")
    fun updateAuthors(@PathVariable id: Long, @RequestBody authorName: String): ResponseEntity<Composition> {
        log.info("attempt to add to composition $id author with name: $authorName")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        val author = authorRepository.findByName(authorName).orElseThrow {
            ResourceNotFoundException("Author", "name", authorName) }
        composition.addAuthor(author)
        authorRepository.save(author)
        val result = compositionRepository.save(composition)
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/albums")
    fun updateAlbums(@PathVariable id: Long, @RequestBody albumTitle: String): ResponseEntity<Composition> {
        log.info("attempt to add to composition $id album with title: $albumTitle")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        val album = albumRepository.findByTitle(albumTitle).orElseThrow {
            ResourceNotFoundException("Album", "title", albumTitle) }
        composition.addAlbum(album)
        albumRepository.save(album)
        val result = compositionRepository.save(composition)
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/genres")
    fun addGenre(@PathVariable id: Long, @RequestBody genreName: String): ResponseEntity<Composition> {
        log.info("attempt to add genre: $genreName to composition $id")
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Composition", "id", id) }
        val genre = genreRepository.findByName(genreName).orElseThrow {
            ResourceNotFoundException("Genre", "name", genreName) }
        composition.addGenre(genre)
        genreRepository.save(genre)
        val result = compositionRepository.save(composition)
        return ResponseEntity.ok(result)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteComposition(@PathVariable id: Long): ResponseEntity<Long> {
        val composition = compositionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("composition", "id", id) }
        log.info("attempt to delete composition ${composition.title}")
        compositionRepository.delete(composition)
        return ResponseEntity.ok(id)
    }
}